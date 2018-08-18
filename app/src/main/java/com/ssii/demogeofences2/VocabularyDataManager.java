package com.ssii.demogeofences2;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssii.demogeofences2.Objects.Concept;
import com.ssii.demogeofences2.Objects.OrderedConcept;
import com.ssii.demogeofences2.Objects.ShownConcept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;


public class VocabularyDataManager extends Observable {

    public static HashMap<String, Concept> conceptsCurrentPlace; // All concepts of current place

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public  void getVocabulary(String currentCategory) {

        db.collection("concepts")
                .whereEqualTo("category", currentCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TEST", document.getId() + " => " + document.getData());
                                String name = document.getData().get("name").toString();
                                String imageRoute = document.getData().get("image").toString();
                                Concept concept = new Concept(name, imageRoute);
                                conceptsCurrentPlace.put(name, concept);
                            }
                            setChanged();
                            notifyObservers();

                        } else {
                            Log.w("TEST", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getOrderedConcepts(String currentCategory) { // Tiene que ser los conceptos con el orden y la fuerza
        Log.d("TEST", "en getOrderedConcepts");
        final List<OrderedConcept> conceptsToEvaluate = new ArrayList<>();
        db.collection("users/prueba/actions/taughtConceptsInOrder/" + currentCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TEST", document.getId() + " => " + document.getData());
                                String name = document.getId().toString();
                                int strength = (int)document.getData().get("strength");
                                int position = (int)document.getData().get("position");
                                OrderedConcept orderedConcept = new OrderedConcept(name, strength, position);
                                conceptsToEvaluate.add(orderedConcept);
                                Log.d("TEST", "Añadidios los conceptos ordenados");
                            }
                            setChanged();
                            notifyObservers(conceptsToEvaluate);

                        } else {
                            Log.w("TEST", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void sendTaughtConcepts(HashMap<String, ShownConcept> concepts, String currentPlace) {
        CollectionReference users = db.collection("users").document("prueba").collection("actions").document("taughtConcepts").collection(currentPlace);
        for (ShownConcept sc: concepts.values()) {
            users
                    .add(sc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
        }
        setChanged();
        notifyObservers();

    }


}