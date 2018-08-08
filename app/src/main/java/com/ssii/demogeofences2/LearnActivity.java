package com.ssii.demogeofences2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LearnActivity extends AppCompatActivity {

    Button unknowButton, knowButton, homeButton, testButton;
    ImageButton imageButton;
    TextView nameConceptTextV;
    FloatingActionButton nextFAButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        initializeComponents();
    }

    private void initializeComponents() {
        unknowButton = (Button)findViewById(R.id.unknowButton);
        knowButton = (Button)findViewById(R.id.knowButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        testButton = (Button)findViewById(R.id.testButton);

        imageButton = (ImageButton)findViewById(R.id.imageConceptButton);
        nameConceptTextV = (TextView)findViewById(R.id.nameConceptTextV);
        nextFAButton = (FloatingActionButton)findViewById(R.id.nextFloatingButton);

        nameConceptTextV.setVisibility(View.INVISIBLE);
        nextFAButton.setVisibility(View.INVISIBLE);
        unknowButton.setVisibility(View.INVISIBLE);
        knowButton.setVisibility(View.INVISIBLE);
    }

    private void chooseConcept() {

    }

    private void showConcept() {

    }

    private void showName() {
        nameConceptTextV.setVisibility(View.INVISIBLE);
    }

    private void hideButtons() {
        nextFAButton.setVisibility(View.INVISIBLE);
        unknowButton.setVisibility(View.INVISIBLE);
        knowButton.setVisibility(View.INVISIBLE);
    }
}
