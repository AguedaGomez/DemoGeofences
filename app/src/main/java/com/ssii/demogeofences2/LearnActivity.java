package com.ssii.demogeofences2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssii.demogeofences2.Objects.Concept;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class LearnActivity extends AppCompatActivity implements Observer{

    Button unknowButton, knowButton, homeButton, testButton;
    ImageView imagenViewConcept;
    TextView nameConceptTextV;
    FloatingActionButton nextFAButton;

    VocabularyData vocabularyData;
    VocabularyManager vocabularyManager;
    String currentPlace;
    HashMap<String, Concept> concepts;
    HashMap<String, Concept> knownToughtConcepts;
    Object[] conceptsKeys;
    Concept currentConcept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        knownToughtConcepts = new HashMap<>();
        initializeComponents();
        loadVocabulary();


    }

    private void loadVocabulary() {
        Bundle bundle = getIntent().getExtras();
        currentPlace = bundle.getString("currentPlace");
        vocabularyData = new VocabularyData();
        vocabularyManager = new VocabularyManager();
        vocabularyData.addObserver(this);
        Log.d("TEST", "DESPUÉS DE AÑADIR OBSERVADORES");
        vocabularyData.getVocabulary(currentPlace);

    }

    private void initializeComponents() {
        unknowButton = (Button)findViewById(R.id.unknowButton);
        knowButton = (Button)findViewById(R.id.knowButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        testButton = (Button)findViewById(R.id.testButton);

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
                currentConcept.setWeight(15);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentConcept.setWeight(10);
                concepts.remove(currentConcept.getName());
                conceptsKeys = concepts.keySet().toArray();
                knownToughtConcepts.put(currentConcept.getName(), currentConcept);
                nextFAButton.setVisibility(View.VISIBLE);
                unknowButton.setVisibility(View.INVISIBLE);
                knowButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void chooseConcept() {
        Log.d("TEST", "chooseConcept");
        Object key = conceptsKeys[new Random().nextInt(conceptsKeys.length)];
        currentConcept = concepts.get(key);
        showConcept(currentConcept);
    }

    private void showConcept(Concept currentConcept) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl(currentConcept.getImage());
        nameConceptTextV.setText(currentConcept.getName());
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .into(imagenViewConcept);

    }

    private void showName() {
        nameConceptTextV.setVisibility(View.VISIBLE);
        if(!knownToughtConcepts.containsKey(currentConcept.getName())) {
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
        concepts = (HashMap<String, Concept>)o;
        conceptsKeys = concepts.keySet().toArray();
        chooseConcept();
        Log.d("TEST", " DESDE ACTIVITY SE HAN CARGADO LAS PALABRAS");
    }


}
