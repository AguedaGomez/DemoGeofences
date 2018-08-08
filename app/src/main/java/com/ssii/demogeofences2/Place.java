package com.ssii.demogeofences2;

/**
 * Created by Ague on 07/08/2018.
 */

public class Place {
    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    private String name;
    private Double lat;
    private Double lng;

    public Place(String name, Double lat, Double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}
