package com.ssii.demogeofences2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class EvaluationActivity extends AppCompatActivity implements Observer {

    final int MAX_CONCEPTS = 7;
    final int FIRST_INDEX = 0;
    final String PROGRESS = "/" + MAX_CONCEPTS;

    Button checkButton;
    ProgressBar progressBar, loadProgressBar;
    ImageView imageView;
    EditText inputNameConcept;
    TextView correctNameText, progressText;
    FloatingActionButton nextFAButton;
    android.support.v7.widget.Toolbar toolbar;

    VocabularyDManager vocabularyDManager;
    String currentPlace, user;
    List<OrderedConcept> orderedConceptList;
    HashMap<String, OrderedConcept> orderedConceptHashMap;
    HashMap<Integer, ShownConcept> evaluatedConcepts;
    int index, currentError;
    Concept currentConcept;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String appearanceTime, shownTextTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    StorageReference gsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        index = 0;
        evaluatedConcepts = new HashMap<>();
        orderedConceptHashMap = new HashMap<>();
        vocabularyDManager = VocabularyDManager.getInstance();
        initializeComponents();
        Log.d("TEST", "aNTES DEL bUNDLE");
        Bundle bundle = getIntent().getExtras();
        currentPlace = bundle.getString("currentPlace");
        user = bundle.getString("user");
        Log.d("TEST", "dESPUÉS");

       // orderedConceptList = new ArrayList<>();
        vocabularyDManager.addObserver(this);
        Log.d("TEST", "CONCEPTSCURRETNTPLACE SIZE = " + VocabularyDManager.conceptsCurrentPlace.size());
        if (vocabularyDManager.conceptsCurrentPlace.size() <= 0)
            vocabularyDManager.getVocabulary(currentPlace);
        else vocabularyDManager.getOrderedConcepts(currentPlace, user);
    }

    private void initializeComponents() {
        checkButton = (Button)findViewById(R.id.check);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        inputNameConcept = (EditText) findViewById(R.id.inputNameConcept);
        progressText = findViewById(R.id.progressText);
        correctNameText = findViewById(R.id.correctName);
        correctNameText.setText("");
        nextFAButton = findViewById(R.id.nextFloatingButton);
        nextFAButton.setVisibility(View.INVISIBLE);
        loadProgressBar = findViewById(R.id.loadProgressBar);

        progressText.setText(index + PROGRESS);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "ON CLICK");
                checkAnswer();
            }
        });

        nextFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextClick();
            }
        });

        loadProgressBar.setMax(MAX_CONCEPTS);

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.evaluation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_home:
                createAlertDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
       createAlertDialog();
    }

    private void createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Si abandonas la evaluación perderás tu progreso. ¿Realmente quieres salir?")
                .setCancelable(false)
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                initializeMainActivity();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void update(Observable observable, Object o) {
        switch (o.toString()) {
            case "getOrderedConcepts":
                Log.d("TEST", "después de gtOrderedConcepts");
                orderedConceptList = new ArrayList<>(VocabularyDManager.conceptsToEvaluate.values());
                Collections.sort(orderedConceptList);
                chooseConcept();
                break;
            case "getVocabulary":
                vocabularyDManager.getOrderedConcepts(currentPlace, user);
                break;
                default:
                    break;
        }
    }

    private void nextClick() {
        if (index >= 7) {
            ShownConcept newShownConcept = new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName());
            newShownConcept.setError(currentError);
            Log.d("TEST", "El error es: " + newShownConcept.getError());
            evaluatedConcepts.put(index, newShownConcept);
            vocabularyDManager.sendEvaluatedConcepts(evaluatedConcepts, currentPlace, user);
            for (OrderedConcept o: orderedConceptList) {
                orderedConceptHashMap.put(o.getName(), o);
            }
            vocabularyDManager.sendTaughtConceptsInOrder(orderedConceptHashMap, currentPlace, user);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //AQUI RESUMEN?
            builder.setMessage("Has aprendido todas las palabras disponibles en este contexto")
                    .setCancelable(false)
                    .setPositiveButton("Volver",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    initializeMainActivity();


                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            ShownConcept newShownConcept = new ShownConcept(appearanceTime, shownTextTime, currentConcept.getName());
            newShownConcept.setError(currentError);
            Log.d("TEST", "El error es: " + newShownConcept.getError());
            evaluatedConcepts.put(index, newShownConcept);
            Log.d("TEST", "Añadiendo nuevo shown concept");
            chooseConcept();
        }


    }

    private void initializeMainActivity() {
        vocabularyDManager.deleteObserver(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void chooseConcept() {
        nextFAButton.setVisibility(View.INVISIBLE);
        checkButton.setVisibility(View.VISIBLE);
        inputNameConcept.setText("");
        correctNameText.setText("");
        enableEditText(true);
        inputNameConcept.setTextColor(Color.DKGRAY);

        currentConcept = VocabularyDManager.conceptsCurrentPlace.get(orderedConceptList.get(FIRST_INDEX).getName());

        // Show concept image
        gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
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
                        Date date = new Date();
                        appearanceTime = dateFormat.format(date);
                        return false;
                    }
                })
                .into(imageView);
    }

    private void checkAnswer() {
        Log.d("TEST", "checkAnswer");
        boolean correct = false;
        String answer = inputNameConcept.getText().toString();
        if (answer.length()==0) {
            Toast.makeText(this, "Escribe el nombre del concepto", Toast.LENGTH_LONG).show();
            return;
        }
        answer = answer.substring(0,1).toUpperCase() + answer.substring(1).toLowerCase();
        if (answer.equals(currentConcept.getName())) {
            Log.d("TEST", "RESPUESTA CORRECTA");
            inputNameConcept.setTextColor(Color.rgb(0,128,0));
            correct = true;
            currentError = 0;
        }
        else {
            correctNameText.setText(currentConcept.getName());
            currentError = 1;
        }
        Date date = new Date();
        shownTextTime = dateFormat.format(date);

        inputNameConcept.setText(answer);
        enableEditText(false);
        updateConceptPosition(correct);
    }

    private void updateConceptPosition(boolean correct) {
        OrderedConcept orderedConcept = orderedConceptList.get(FIRST_INDEX);
        int strenght = orderedConcept.getStrength();
        if (correct)
            orderedConcept.setStrength(strenght+1);
        else
            orderedConcept.setStrength(0);
        strenght = orderedConcept.getStrength();
        int position = orderedConceptList.indexOf(orderedConcept) + (int)Math.pow(2, strenght+1);
        Log.d("TEST", "NUEVA POSICION de "+ orderedConcept.getName() + " es " + position);
        orderedConcept.setPosition(position);
        Collections.sort(orderedConceptList);
        index++;
        loadProgressBar.setProgress(index);
        nextFAButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.INVISIBLE);
        progressText.setText(index + PROGRESS);
    }

    private void enableEditText(boolean editable) {
        inputNameConcept.setFocusable(editable);
        inputNameConcept.setClickable(editable);
        inputNameConcept.setCursorVisible(editable);
        inputNameConcept.setFocusableInTouchMode(editable);
    }

}
