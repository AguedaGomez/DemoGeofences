package com.ssii.demogeofences2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationActivity extends AppCompatActivity {

    ListView placesListView;
    String currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        placesListView = findViewById(R.id.placesList);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.placesCategories,
                android.R.layout.simple_list_item_1);

        placesListView.setAdapter(adapter);

        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentPlace = adapterView.getItemAtPosition(i).toString();
                initializeMainActivity();
            }
        });
    }

    private void initializeMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentPlace", currentPlace);
        intent.putExtra("preActivity", "LocationActivity");
        startActivity(intent);
    }
}
