package com.ssii.demogeofences2;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssii.demogeofences2.Objects.Concept;

import java.util.HashMap;
import java.util.Observable;


public class VocabularyData extends Observable {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String, Concept> conceptMap;
    final int DEFAULT_WEIGHT = -1;

    public  void getVocabulary(String currentCategory) {
        conceptMap = new HashMap<String, Concept>();
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
                                Concept concept = new Concept(name, imageRoute,DEFAULT_WEIGHT);
                                conceptMap.put(name, concept);
                            }
                            setChanged();
                            notifyObservers(conceptMap);

                        } else {
                            Log.w("TEST", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
