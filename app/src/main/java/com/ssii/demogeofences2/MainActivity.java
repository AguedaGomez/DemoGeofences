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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int GEOFENCE_RADIUS = 100;
    private static final int SDK_THRESHOLD = 23;

    private GeofencingClient geofencingClient;

    private FusedLocationProviderClient mFusedLocationClient;


    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.TextLog);
        geofencingClient = new GeofencingClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        InitGeofences();



    }


    @SuppressLint("MissingPermission")
    private void InitGeofences() {
        Log.d("TEST", "En initGeofences");
        if (isLocationAccessPermitted()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("TEST", location.getLatitude() + "," + location.getLongitude());
                                // Logic to handle location object
                            }
                        }
                    });
            geofencingClient.removeGeofences(getGeofencePendingIntent());
            List<Geofence> geofences = new ArrayList<>();
            geofences.add(getGeofence(40.0665974, -2.142336, "Plaza de toros"));
            geofences.add(getGeofence(40.0703178, -2.1398082, "Cuatro caminos"));
            geofences.add(getGeofence(40.0700921, -2.1383116, "Mango, Calle Cervantes"));
            geofences.add(getGeofence(40.0616557, -2.1386667, "Quinientas-Reyes Catolicos"));
            geofences.add(getGeofence(40.0732046, -2.1389133, "Carreteria"));
            geofences.add(getGeofence(40.0675682, -2.1398859, "Casa"));
            geofences.add(getGeofence(40.0612614, -2.1432617, "Guardia Civil"));

            for (Geofence g: geofences) {
                geofencingClient.addGeofences(getGeofencingRequest(g), getGeofencePendingIntent())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {

                                    tv.setText(tv.getText() + " Geofence Añadida\n");
                                    Log.d("TEST", "Geofence añadida");

                                } else {
                                    Log.d("TEST", getErrorString(task.getException().hashCode()));
                                    tv.setText(tv.getText() + " No se ha añadido geofence\n");
                                }
                            }
                        });
            }

        } else {
            Log.d("TEST", "no localizacion");
            askPermission();

        }

    }


   /* @Override
    protected void onStart() {
        super.onStart();
        InitGeofences();

    }*/

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

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);
        return builder.build();

    }

    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(5000)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent getGeofencePendingIntent() {
        //Intent intent = new Intent(this, LocationAlertIntentService.class);
        //return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, LocationAlertIntentService.class);
        return geoFencePendingIntent = PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

}
