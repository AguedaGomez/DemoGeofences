package com.ssii.demogeofences2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Set;

public class LearnActivity extends AppCompatActivity implements Observer{

    Button unknowButton, knowButton;
    ImageView imagenViewConcept;
    TextView nameConceptTextV;
    FloatingActionButton nextFAButton;
    ProgressBar progressBar;
    android.support.v7.widget.Toolbar toolbar;
    MenuItem currentItem;

    VocabularyDataManager vocabularyDataManager;
    HashMap<String, Concept> concepts;
    HashMap<String, Concept> knownTaughtConcepts;
    HashMap<String, ShownConcept> taughtConcepts;
    HashMap<String, OrderedConcept> orderedConcepts;
    Concept currentConcept;
    Set<String> conceptsKeys;
    String appearanceTime, shownTextTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String user;
    StorageReference gsReference;
    int indexPosition = 0;


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
        vocabularyDataManager = VocabularyDataManager.getInstance();
        vocabularyDataManager.addObserver(this);
        Log.d("TEST", "DESPUÉS DE AÑADIR OBSERVADORES");
        vocabularyDataManager.getOrderedConcepts();

    }

    private void initializeComponents() {
        unknowButton = (Button)findViewById(R.id.unknowButton);
        knowButton = (Button)findViewById(R.id.knowButton);
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
                OrderedConcept orderedConcept = new OrderedConcept(currentConcept.getName(), 0, indexPosition);
                addOrderedConcept(orderedConcept);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taughtConcepts.put(currentConcept.getName(), new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName()));
                OrderedConcept orderedConcept = new OrderedConcept(currentConcept.getName(), 1, indexPosition);
                addOrderedConcept(orderedConcept);
                conceptsKeys.remove(currentConcept.getName());
                knownTaughtConcepts.put(currentConcept.getName(), currentConcept);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "on click");
                if (orderedConcepts.size() > VocabularyDataManager.conceptsToEvaluate.size())
                    saveConceptsInOrder();
                else
                    initializeMainActivity();
            }
        });
        getSupportActionBar().setTitle("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.learn_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_test:
                currentItem = item;
                saveConceptsInOrder();
                return true;
            case R.id.action_help:
                showHelp();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.learn_help_info))
                .setTitle(R.string.help_title)
                .setIcon(R.drawable.ic_help_outline_red_24dp)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_main_help_positive_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                            }
                        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        initializeMainActivity();
    }

    private void addOrderedConcept(OrderedConcept orderedConcept) {
        if(!orderedConcepts.containsKey(currentConcept.getName())) {
            orderedConcepts.put(currentConcept.getName(), orderedConcept);
            indexPosition++;
        }
    }
    private void chooseConcept() {
        Log.d("TEST", "chooseConcept");
        if (conceptsKeys.size() > 0) {
            Object key = conceptsKeys.toArray()[new Random().nextInt(conceptsKeys.size())];
            currentConcept = concepts.get(key);
           /* OrderedConcept orderedConcept = new OrderedConcept(currentConcept.getName(), 0, indexPosition);
            if(!orderedConcepts.containsKey(currentConcept.getName())) {
                orderedConcepts.put(currentConcept.getName(), orderedConcept);
                indexPosition++;
            }*/
            showConcept(currentConcept);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.end_learn_text))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.end_learn_negative_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    if (orderedConcepts.size() > VocabularyDataManager.conceptsToEvaluate.size())
                                        saveConceptsInOrder();
                                    else
                                        initializeMainActivity();
                                }
                            })
                    .setPositiveButton(getString(R.string.end_learn_positive_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Guardar los conceptos aprendidos con los pesos actualizados
                                    if (orderedConcepts.size() > VocabularyDataManager.conceptsToEvaluate.size())
                                        saveConceptsInOrder();
                                    else
                                        initializeEvaluationActivity();

                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private void saveConceptsInOrder() {
        Log.d("TEST", "en save");
        vocabularyDataManager.sendTaughtConceptsInOrder(orderedConcepts);
    }

    private void saveTaughtConcepts() {
        vocabularyDataManager.sendTaughtConcepts(taughtConcepts);
    }

    private void initializeMainActivity() {
        Log.d("TEST", "en initializeMain");
        vocabularyDataManager.deleteObserver(this);
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void initializeEvaluationActivity() {
        vocabularyDataManager.deleteObserver(this);
        Intent intent = new Intent(this, EvaluationActivity.class);
        finish();

        startActivity(intent);
    }

    private void showConcept(Concept currentConcept) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
        nameConceptTextV.setText(currentConcept.getName());
       Glide.with(getApplicationContext())
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
                concepts = VocabularyDataManager.conceptsCurrentPlace;
                conceptsKeys = new HashSet<>(concepts.keySet());
                removeConceptsAlreadyTaught();
                //conceptsKeys.removeAll(knownTaughtConcepts.keySet());
                chooseConcept();
                Log.d("TEST", " DESDE ACTIVITY SE HAN CARGADO LAS PALABRAS");
                break;
            case "getOrderedConcepts":
                orderedConcepts = VocabularyDataManager.conceptsToEvaluate;
                //Log.d("TEST", "ORDERED CONCEPTS TIENE: " + orderedConcepts.size());
                vocabularyDataManager.getVocabulary();
                break;
            case "sendTaughtConcepts":
                if (currentItem.getItemId()==android.R.id.home) {
                    Log.d("TEST", "HOME");
                    initializeMainActivity();
                }

                else if (currentItem.getItemId()== R.id.action_test)
                    initializeEvaluationActivity();
                else {
                    Log.d("TEST", "ELSE");
                    initializeMainActivity();
                }

                break;
            case "sendTaughtConceptsInOrder":
                saveTaughtConcepts();

        }

    }

    private void removeConceptsAlreadyTaught() {
        List<String> conceptsTaught = new ArrayList<>();
        conceptsKeys.removeAll(knownTaughtConcepts.keySet());
        for (OrderedConcept concept: orderedConcepts.values()) {
            if (concept.getStrength() == 1) {
                conceptsTaught.add(concept.getName());
            }
        }
        conceptsKeys.removeAll(conceptsTaught);
    }

}
