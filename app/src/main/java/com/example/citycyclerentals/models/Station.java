package com.example.citycyclerentals.models;

import java.io.Serializable;

public class Station implements Serializable {
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public Station(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return name;
    }
}