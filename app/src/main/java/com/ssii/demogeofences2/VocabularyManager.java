package com.ssii.demogeofences2;

import com.ssii.demogeofences2.Objects.Concept;

import java.util.HashMap;

/**
 * Created by Ague on 14/08/2018.
 */

public class VocabularyManager {


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
