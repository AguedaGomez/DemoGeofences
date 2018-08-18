package com.ssii.demogeofences2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    final int MAX_CONCEPTS = 10;
    final int MIN_CONCEPTS = 1;

    Button checkButton;
    ProgressBar progressBar;
    ImageView imageView;
    EditText inputNameConcept;
    TextView correctNameText;


    VocabularyDManager vocabularyDManager;
    String currentPlace;
    List<OrderedConcept> orderedConceptList;
    int index;
    Concept currentConcept;

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

        orderedConceptList = new ArrayList<>();
        //vocabularyDataManager = new VocabularyDataManager();
       // vocabularyDataManager.addObserver(this);
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

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "ON CLICK");
                checkAnswer();
            }
        });

        /*progressBar.setMax(MAX_CONCEPTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(MIN_CONCEPTS, true);
        }*/

    }

    @Override
    public void update(Observable observable, Object o) {
        if (o == null) {
            // esto es si se ha completado todo el vocabulario
            Log.d("TEST", "ya se han cargado todas las palabras del vocabulario");
            vocabularyDManager.getOrderedConcepts(currentPlace);
        }
        else {
            // ya se ha conseguido el vocabulario
            Log.d("TEST", "ordenando");
            //vocabularyDManager.getOrderedConcepts(currentPlace);
            orderedConceptList = (List<OrderedConcept>)o;
            Log.d("TEST", "1");
            Collections.sort(orderedConceptList);
            Log.d("TEST", "2");
            chooseConcept();
        }

    }

    private void chooseConcept() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        currentConcept = VocabularyDManager.conceptsCurrentPlace.get(orderedConceptList.get(index).getName());
        StorageReference gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .into(imageView);

    }

    private void checkAnswer() {
        Log.d("TEST", "checkAnswer");
        String answer = inputNameConcept.getText().toString();
        answer = answer.substring(0,1).toUpperCase() + answer.substring(1).toLowerCase();
        if (answer.equals(currentConcept.getName())) {
            inputNameConcept.setTextColor(Color.rgb(0,128,0));
            inputNameConcept.setFocusable(false);
            inputNameConcept.setText(answer);
            correctNameText.setText(currentConcept.getName());
        }
    }
}
