package com.ssii.demogeofences2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class EvaluationActivity extends AppCompatActivity implements Observer {

    final int MAX_CONCEPTS = 7;
    final int FIRST_INDEX = 0;

    Button checkButton;
    ProgressBar progressBar, loadProgressBar;
    ImageView imageView;
    EditText inputNameConcept;
    TextView correctNameText;
    FloatingActionButton nextFAButton;


    VocabularyDManager vocabularyDManager;
    String currentPlace;
    List<OrderedConcept> orderedConceptList;
    int index;
    Concept currentConcept;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        index = 0;
        vocabularyDManager = VocabularyDManager.getInstance();
        initializeComponents();
        Log.d("TEST", "aNTES DEL bUNDLE");
        Bundle bundle = getIntent().getExtras();
        currentPlace = bundle.getString("currentPlace");
        Log.d("TEST", "dESPUÉS");

       // orderedConceptList = new ArrayList<>();
        vocabularyDManager.addObserver(this);
        Log.d("TEST", "CONCEPTSCURRETNTPLACE SIZE = " + VocabularyDManager.conceptsCurrentPlace.size());
        if (vocabularyDManager.conceptsCurrentPlace.size() <= 0)
            vocabularyDManager.getVocabulary(currentPlace);
        else vocabularyDManager.getOrderedConcepts(currentPlace);
    }

    private void initializeComponents() {
        checkButton = (Button)findViewById(R.id.check);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        inputNameConcept = (EditText) findViewById(R.id.inputNameConcept);
        correctNameText = findViewById(R.id.correctName);
        correctNameText.setText("");
        nextFAButton = findViewById(R.id.nextFloatingButton);
        nextFAButton.setVisibility(View.INVISIBLE);
        loadProgressBar = findViewById(R.id.loadProgressBar);

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
                chooseConcept();
            }
        });

        loadProgressBar.setMax(MAX_CONCEPTS);

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
                vocabularyDManager.getOrderedConcepts(currentPlace);
                break;
                default:
                    break;
        }
      /*  if (o == null) {
            // get all vocabulary is completed
            Log.d("TEST", "ya se han cargado todas las palabras del vocabulario");
            vocabularyDManager.getOrderedConcepts(currentPlace);
        }
        else {

            Log.d("TEST", "ordenando");
            orderedConceptList = (List<OrderedConcept>)o;
            Log.d("TEST", "1");
            Collections.sort(orderedConceptList);
            Log.d("TEST", "2");
            chooseConcept();
        }*/

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
        StorageReference gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
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
                .into(imageView);

    }

    private void checkAnswer() {
        Log.d("TEST", "checkAnswer");
        boolean correct = false;
        String answer = inputNameConcept.getText().toString();
        Log.d("TEST", "longitud answer: " + answer.length());
        if (answer.length()==0) {
            Toast.makeText(this, "Escribe el nombre del concepto", Toast.LENGTH_LONG).show();
            return;
        }
        answer = answer.substring(0,1).toUpperCase() + answer.substring(1).toLowerCase();
        if (answer.equals(currentConcept.getName())) {
            Log.d("TEST", "RESPUESTA CORRECTA");
            inputNameConcept.setTextColor(Color.rgb(0,128,0));
            correct = true;
        }
        else {
            correctNameText.setText(currentConcept.getName());
        }


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
        int position = (int)Math.pow(2, strenght);
        Log.d("TEST", "NUEVA POSICION de "+ orderedConcept.getName() + " es " + position);
        orderedConcept.setPosition(position);
        Collections.sort(orderedConceptList);
        index++;
        loadProgressBar.setProgress(index);
        nextFAButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.INVISIBLE);
    }

    private void enableEditText(boolean editable) {
        inputNameConcept.setFocusable(editable);
        inputNameConcept.setClickable(editable);
        inputNameConcept.setCursorVisible(editable);
        inputNameConcept.setFocusableInTouchMode(editable);
    }
}
