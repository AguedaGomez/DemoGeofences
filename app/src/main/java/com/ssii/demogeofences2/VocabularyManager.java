package com.ssii.demogeofences2;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ssii.demogeofences2.Objects.Concept;
import com.ssii.demogeofences2.Objects.ShownConcept;

import java.util.HashMap;

/**
 * Created by Ague on 14/08/2018.
 */

public class VocabularyManager {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void sendTaughtConcepts(HashMap<String, ShownConcept> concepts) {
        DocumentReference users = db.collection("users").document("prueba").collection("actions").document("taughtConcepts");
        users.set(concepts)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TEST", "YEEEEEEEEEEEEEEEEEES");
                    }
                });

    }

    public Concept getRandomConcept(HashMap<String, Concept> concepts) {
        int random = (int)(Math.random() * concepts.size());
        Concept concept = concepts.get(random);
        return concept;
        /*Log.d("TEST", "getRandomConcept");
        Concept concept = concepts.get("Bagaglio");
        Log.d("TEST", "chooseConcept " + concept.getName());
        return concept;*/
    }
}
