package com.ssii.demogeofences2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int GEOFENCE_RADIUS = 50;
    private static final int SDK_THRESHOLD = 23;

    private GeofencingClient geofencingClient;

    private FusedLocationProviderClient mFusedLocationClient;

    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.TextLog);

        InitGeofences();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }



    @SuppressLint("MissingPermission")
    private void InitGeofences() {
        Log.d("TEST", "En initGeofences");
        if (isLocationAccessPermitted()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d("TEST", location.getLatitude() + "," + location.getLongitude());
                            }
                        }
                    });
            List<Geofence> geofences = new ArrayList<Geofence>();
            geofences.add(getGeofence(40.0643055, -2.1393537, "Ahorramas, Paseo San Antonio"));
            geofences.add(getGeofence(40.0678358, -2.1376562, "Musical Ismael, Fermin Caballero"));
            geofences.add(getGeofence(40.0700921, -2.1383116, "Mango, Calle Cervantes"));
            geofences.add(getGeofence(40.067567, -2.1392448, "Casa, Paseo San Antonio"));
            //Geofence geofence = getGeofence(40.0672832, -2.137968, "Hispano Fermín Caballero");
            //Log.d("TEST", "Creando geofence");
           /* List<Geofence> geofences = new ArrayList<Geofence>();
            geofences.add(geofence);
            geofencingClient.removeGeofences(geofences);
            for (Geofence g: geofences) {

                geofencingClient.addGeofences(getGeofencingRequest(g), getGeofencePendingIntent())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {

                                    tv.setText(tv.getText() + " Geofence Añadida\n");
                                    Log.d("TEST", "Geofence añadida");

                                } else {
                                    tv.setText(tv.getText() + " No se ha añadido geofence\n");
                                }

                            }
                        });
            }*/

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
                .setLoiteringDelay(10000)
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
