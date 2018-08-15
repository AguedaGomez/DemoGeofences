package com.ssii.demogeofences2;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ssii.demogeofences2.Objects.Concept;

import java.util.HashMap;

public class EvaluationActivity extends AppCompatActivity {

    final int MAX_CONCEPTS = 10;
    final int MIN_CONCEPTS = 1;

    Button checkButton;
    ProgressBar progressBar;
    ImageView imageView;
    EditText inputNameConcept;

    HashMap<String, Concept> concepts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        initializeComponents();
    }

    private void initializeComponents() {
        checkButton = (Button)findViewById(R.id.check);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imageView = (ImageView)findViewById(R.id.imageView);
        inputNameConcept = (EditText) findViewById(R.id.inputNameConcept);

        progressBar.setMax(MAX_CONCEPTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(MIN_CONCEPTS, true);
        }

        concepts = (HashMap<String, Concept>)getIntent().getSerializableExtra("knownTaughtConcepts");
        Log.d("TEST", concepts.size() + "");
    }
}
