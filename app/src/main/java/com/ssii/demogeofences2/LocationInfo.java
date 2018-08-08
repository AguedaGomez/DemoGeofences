package com.ssii.demogeofences2;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ague on 06/08/2018.
 */

public class LocationInfo {

    public static String currentPlace = "ningún sitio";

    private List<Place> places;

    public LocationInfo () {
        places = new ArrayList<>();
        places.add(new Place("Plaza de toros",40.0665974 ,-2.142336));
        places.add(new Place("Hospital Virgen de la luz", 40.0537183, -2.1245353));
        places.add(new Place("Estación de tren", 40.067376, -2.1371418));


        Log.d("TEST", "Añadidos places");
    }

    public double calculateDistance (double lat1, double lng1, double lat2, double lng2) {
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }

    public String nearestPlace2Me( double mlat, double mlong) {
        Log.d("TEST", "NearestPlace2Me");
        double minDistance = Double.MAX_VALUE;
        String nearestPlace = "";
        Log.d("TEST", places.size() + "");
        for (Place p: places) {
            double d = calculateDistance(mlat, mlong, p.getLat(), p.getLng());
            if (d < minDistance) {
                minDistance = d;
                nearestPlace = p.getName();
            }
        }

        return nearestPlace;

    }
}
