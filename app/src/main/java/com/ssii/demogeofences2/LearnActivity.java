package com.ssii.demogeofences2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssii.demogeofences2.Objects.Concept;
import com.ssii.demogeofences2.Objects.OrderedConcept;
import com.ssii.demogeofences2.Objects.ShownConcept;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Set;

public class LearnActivity extends AppCompatActivity implements Observer{

    Button unknowButton, knowButton, homeButton, testButton;
    ImageView imagenViewConcept;
    TextView nameConceptTextV;
    FloatingActionButton nextFAButton;
    ProgressBar progressBar;

    VocabularyDManager vocabularyDManager;
    String currentPlace;
    HashMap<String, Concept> concepts;
    HashMap<String, Concept> knownTaughtConcepts;
    HashMap<String, ShownConcept> taughtConcepts;
    HashMap<String, OrderedConcept> orderedConcepts;
    Concept currentConcept;
    Set<String> conceptsKeys;
    String appearanceTime, shownTextTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        knownTaughtConcepts = new HashMap<>();
        taughtConcepts = new HashMap<>();
        orderedConcepts = new HashMap<>();
        initializeComponents();
        loadVocabulary();
    }

    private void loadVocabulary() {
        Bundle bundle = getIntent().getExtras();
        currentPlace = bundle.getString("currentPlace");
        vocabularyDManager = VocabularyDManager.getInstance();
        vocabularyDManager.addObserver(this);
        Log.d("TEST", "DESPUÉS DE AÑADIR OBSERVADORES");
        vocabularyDManager.getOrderedConcepts(currentPlace);

    }

    private void initializeComponents() {
        unknowButton = (Button)findViewById(R.id.unknowButton);
        knowButton = (Button)findViewById(R.id.knowButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        testButton = (Button)findViewById(R.id.testButton);
        progressBar = findViewById(R.id.progressBar);

        imagenViewConcept = (ImageView)findViewById(R.id.imageViewConcept);
        nameConceptTextV = (TextView)findViewById(R.id.nameConceptTextV);
        nextFAButton = (FloatingActionButton)findViewById(R.id.nextFloatingButton);

        nameConceptTextV.setVisibility(View.INVISIBLE);
        nextFAButton.setVisibility(View.INVISIBLE);
        unknowButton.setVisibility(View.INVISIBLE);
        knowButton.setVisibility(View.INVISIBLE);

        imagenViewConcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showName();
            }
        });
        nextFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtons();
                chooseConcept();
            }
        });
        unknowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taughtConcepts.put(currentConcept.getName(), new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName()));
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taughtConcepts.put(currentConcept.getName(), new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName()));
                conceptsKeys.remove(currentConcept.getName());
                knownTaughtConcepts.put(currentConcept.getName(), currentConcept);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void chooseConcept() {
        Log.d("TEST", "chooseConcept");
        if (conceptsKeys.size() > 0) {
            Object key = conceptsKeys.toArray()[new Random().nextInt(conceptsKeys.size())];
            currentConcept = concepts.get(key);
            OrderedConcept orderedConcept = new OrderedConcept(currentConcept.getName(), 1, 0);
            if(!orderedConcepts.containsKey(currentConcept.getName()))
                orderedConcepts.put(currentConcept.getName(), orderedConcept);
            showConcept(currentConcept);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Has aprendido todas las palabras disponibles en este contexto")
                    .setCancelable(false)
                    .setNegativeButton("Volver al menú",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    initializeMainActivity();
                                }
                            })
                    .setPositiveButton("Evaluar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Guardar los conceptos aprendidos con los pesos actualizados
                                    saveTaughtConcepts();


                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private void saveConceptsInOrder() {
        vocabularyDManager.sendTaughtConceptsInOrder(orderedConcepts, currentPlace);
    }

    private void saveTaughtConcepts() {
        vocabularyDManager.sendTaughtConcepts(taughtConcepts, currentPlace);
    }

    private void initializeMainActivity() {
        vocabularyDManager.deleteObserver(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void initializeEvaluationActivity() {
        vocabularyDManager.deleteObserver(this);
        Intent intent = new Intent(this, EvaluationActivity.class);
        intent.putExtra("currentPlace", currentPlace);
        startActivity(intent);
    }

    private void showConcept(Concept currentConcept) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
        nameConceptTextV.setText(currentConcept.getName());
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imagenViewConcept);
        Date date = new Date();
        appearanceTime = dateFormat.format(date);
    }

    private void showName() {
        Date date = new Date();
        shownTextTime = dateFormat.format(date);
        nameConceptTextV.setVisibility(View.VISIBLE);
        if(!knownTaughtConcepts.containsKey(currentConcept.getName())) {
            unknowButton.setVisibility(View.VISIBLE);
            knowButton.setVisibility(View.VISIBLE);
        }
        else
            nextFAButton.setVisibility(View.VISIBLE);
    }

    private void hideButtons() {
        nextFAButton.setVisibility(View.INVISIBLE);
        unknowButton.setVisibility(View.INVISIBLE);
        knowButton.setVisibility(View.INVISIBLE);
        nameConceptTextV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void update(Observable observable, Object o) {
        switch (o.toString()) {
            case "getVocabulary":
                concepts = VocabularyDManager.conceptsCurrentPlace;
                conceptsKeys = new HashSet<>(concepts.keySet());
                conceptsKeys.removeAll(knownTaughtConcepts.keySet());
                chooseConcept();
                Log.d("TEST", " DESDE ACTIVITY SE HAN CARGADO LAS PALABRAS");
                break;
            case "getOrderedConcepts":
                orderedConcepts = VocabularyDManager.conceptsToEvaluate;
                //Log.d("TEST", "ORDERED CONCEPTS TIENE: " + orderedConcepts.size());
                vocabularyDManager.getVocabulary(currentPlace);
                break;
            case "sendTaughtConcepts":
                saveConceptsInOrder();
                break;
            case "sendTaughtConceptsInOrder":
                initializeEvaluationActivity();
                break;
        }
      /* if(o.toString().equals("getVocabulary")) {
           concepts = VocabularyDManager.conceptsCurrentPlace;
           conceptsKeys = new HashSet<>(concepts.keySet());
           conceptsKeys.removeAll(knownTaughtConcepts.keySet());
           chooseConcept();
           Log.d("TEST", " DESDE ACTIVITY SE HAN CARGADO LAS PALABRAS");
      }

      else if (o.toString().equals("sendTaughtConcepts")) {
           saveConceptsInOrder();
       }
      else if (o.toString().equals("sendTaughtConceptsInOrder")) {
           initializeEvaluationActivity();
       }*/

    }


}
