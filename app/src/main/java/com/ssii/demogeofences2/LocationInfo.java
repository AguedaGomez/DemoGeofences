package com.ssii.demogeofences2;

import android.util.Log;

import com.ssii.demogeofences2.Objects.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ague on 06/08/2018.
 */

public class LocationInfo {

    private final int MIN_METRES = 100;

    private List<Place> cuencaPlaces;

    public LocationInfo () {
        cuencaPlaces = new ArrayList<>();
        cuencaPlaces.add(new Place("Bar/Restaurante",40.0713042 ,-2.1374043));
        cuencaPlaces.add(new Place("Hospital", 40.0537183, -2.1245353));
        cuencaPlaces.add(new Place("Estación", 40.067376, -2.1371418));
        cuencaPlaces.add(new Place("Universidad", 40.0713038, -2.1439972));
        cuencaPlaces.add(new Place("Biblioteca", 40.0713031, -2.1440401));
        cuencaPlaces.add(new Place("Biblioteca", 40.0715659, -2.1441689));
        cuencaPlaces.add(new Place("Centro Comercial", 40.0537142, -2.1245406));
        cuencaPlaces.add(new Place("Parque", 40.0740179, -2.1429382));
        cuencaPlaces.add(new Place("Parque", 40.0737158, -2.1370342));


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
        Log.d("TEST", cuencaPlaces.size() + "");
        for (Place p: cuencaPlaces) {
            double d = calculateDistance(mlat, mlong, p.getLat(), p.getLng());
            if (d < minDistance) {
                minDistance = d;
                nearestPlace = p.getName();
            }
        }
        if (minDistance < MIN_METRES) {
            nearestPlace = "Calle";
        }
        Log.d("TEST", nearestPlace);

        return nearestPlace;

    }

    public String translatePlace2English(String s) {
        String place = "";
        switch (s) {
            case "Casa":
                place = "home";
                break;
            case "Parque":
                place = "park";
                break;
            case "Universidad":
                place = "university";
                break;
            case "Estación":
                place = "station";
                break;
            case "Bar/Restaurante":
                place = "restaurant";
                break;
            case "Auditorio":
                place = "audience";
                break;
            case "Calle":
                place = "street";
                break;
            case "Biblioteca":
                place = "library";
                break;
            case "Hospital":
                place = "hospital";
                break;
            case "Centro comercial":
                place = "mall";
                break;
        }
        return place;
    }

    public String translatePlace2Spanish(String s) {
        String place = "";
        switch (s) {
            case "home":
                place = "Casa";
                break;
            case "park":
                place = "Parque";
                break;
            case "university":
                place = "Universidad";
                break;
            case "station":
                place = "Estación";
                break;
            case "restaurant":
                place = "Restaurante";
                break;
            case "audience":
                place = "Auditorio";
                break;
            case "street":
                place = "Calle";
                break;
            case "library":
                place = "Biblioteca";
                break;
            case "hospital":
                place = "Hospital";
                break;
            case "mall":
                place = "Centro Comercial";
                break;
        }
        return place;

    }
}
