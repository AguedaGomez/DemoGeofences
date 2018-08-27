package com.ssii.demogeofences2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ssii.demogeofences2.Account.LogInActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int GEOFENCE_RADIUS = 150;
    static final int SDK_THRESHOLD = 23;

    GeofencingClient geofencingClient;

    FusedLocationProviderClient mFusedLocationClient;

    LocationInfo locationInfo;
    double currentLat, currentLong;
    String user_email;
    String user_name;
    String preActivity;


    TextView tv, localizationInfo;
    Button testButton, learnButton, localizeButton;
    ImageButton locationButton;
    android.support.v7.widget.Toolbar toolbar;
    List<Geofence> geofences;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VocabularyDManager.currentPlace = "street";
        getVariablesExtras();

        geofences = new ArrayList<>();
        localizationInfo = (TextView)findViewById(R.id.localizationTextInfo);

        locationInfo = new LocationInfo();
        geofencingClient = new GeofencingClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentPlace();
        InitButtons();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocalizationInfo();
        Log.d("TEST", "on resume");
    }

    private void getVariablesExtras() {
        Bundle bundle = getIntent().getExtras();
        preActivity = bundle.getString("preActivity");
        Log.d("TEST", "actividad anterior: " + preActivity);
        if (preActivity == "LogInActivity") {
            user_email = bundle.getString("user_email");
            user_name= user_email.substring(0, user_email.indexOf('@'));
        }
    }


    private void updateLocalizationInfo() {
        localizationInfo.setText("Tu ubicación actual: " +  locationInfo.translatePlace2Spanish(VocabularyDManager.currentPlace));
    }

    private void InitButtons() {
        Log.d("TEST", "en initbuttons");
        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        locationButton = (ImageButton)findViewById(R.id.locationButton);
        learnButton = (Button)findViewById(R.id.learnButton);
        testButton = (Button)findViewById(R.id.testButton);
        localizeButton = findViewById(R.id.localizeButton);
        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeLearnActivity();
            }
        });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeEvaluationActivity();
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeLocationActivity();
            }
        });
        localizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentPlace();
            }
        });
        Log.d("TEST", "antes de la toolbar");
       // getSupportActionBar().setTitle(user_name.substring(0,1).toUpperCase() + user_name.substring(1));
        Log.d("TEST", "después de la toolbar");
    }

    private void initializeLocationActivity() {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        initializeLogInActivity();
    }

    private void initializeLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    private void initializeEvaluationActivity() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        intent.putExtra("user", user_email);
        startActivity(intent);
    }

    private void initializeLearnActivity() {
        Intent intent = new Intent(this, LearnActivity.class);
        intent.putExtra("user", user_email);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentPlace() {
        Log.d("TEST", "En initGeofences");
        if (isLocationAccessPermitted()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("TEST", location.getLatitude() + "," + location.getLongitude());
                                currentLat = location.getLatitude();
                                currentLong = location.getLongitude();
                                VocabularyDManager.currentPlace = locationInfo.translatePlace2English(locationInfo.nearestPlace2Me(currentLat, currentLong));
                                updateLocalizationInfo();

                            } else {
                                Log.d("TEST", "La localización es nula");
                            }
                        }
                    });

           //addGeofences();

        } else {
            Log.d("TEST", "no localizacion");
            askPermission();

        }

    }


   @Override
    protected void onStart() {
        super.onStart();
        //getCurrentPlace();

    }

   @SuppressLint("MissingPermission")
   private  void addGeofences () {
       geofencingClient.removeGeofences(getGeofencePendingIntent());

       geofences.add(getGeofence(40.0665974, -2.142336, "Plaza de toros"));
       geofences.add(getGeofence(40.0703178, -2.1398082, "Cuatro caminos"));
       geofences.add(getGeofence(40.0700921, -2.1383116, "Mango, Calle Cervantes"));
       geofences.add(getGeofence(40.0616557, -2.1386667, "Quinientas-Reyes Catolicos"));
       geofences.add(getGeofence(40.0732046, -2.1389133, "Carreteria"));
       geofences.add(getGeofence(40.0675682, -2.1398859, "Casa"));
       geofences.add(getGeofence(40.0612614, -2.1432617, "Guardia Civil"));
       geofences.add(getGeofence(40.0537183, -2.1245353, "El Mirador"));
       geofences.add(getGeofence(40.0537183, -2.1245353, "Hospital"));
       geofences.add(getGeofence(40.0759322, -2.1465609, "Facultad ciencias sociales"));
       geofences.add(getGeofence(40.074022, -2.1429329, "Carrero"));
       geofences.add(getGeofence(37.9117953, -4.7918234, "Casa Nacho"));
       geofences.add(getGeofence(40.067376, -2.1371418, "Estación"));
       geofences.add(getGeofence(39.7274187, -1.9011215, "Almodóvar Casa"));
       geofences.add(getGeofence(39.7256353, -1.9015827, "Almodóvar Plaza"));
       geofences.add(getGeofence(39.72846, -1.9003411, "Almodóvar Coche"));
       geofences.add(getGeofence(39.7246181, -1.9011311, "Almodóvar Colegio"));
       geofences.add(getGeofence(39.7249874, -1.9005061, "Almodóvar Justino"));
       geofences.add(getGeofence(39.7262083, -1.8995498, "Almodóvar Boni"));
       for (Geofence g: geofences) {
           geofencingClient.addGeofences(getGeofencingRequest(g), getGeofencePendingIntent())
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()) {


                               Log.d("TEST", "Geofence añadida");

                           } else {
                               Log.d("TEST", getErrorString(task.getException().hashCode()));

                       }
                       }
                   });
       }
   }
    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "geofence too many_geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "geofence too many pending_intents";
            default:

                Log.d("TEST",GeofenceStatusCodes.getStatusCodeString(errorCode) );
                return "geofence error";
        }
    }


    private void askPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }

    private boolean isLocationAccessPermitted(){
        if (Build.VERSION.SDK_INT < SDK_THRESHOLD) return true;
        return (ContextCompat.checkSelfPermission(this,
               Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();

    }

    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(10000)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent getGeofencePendingIntent() {
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, LocationAlertIntentService.class);

        return geoFencePendingIntent = PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }



}
