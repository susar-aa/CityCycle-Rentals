package com.example.citycyclerentals.models;

import java.io.Serializable;

public class Bike implements Serializable {
    private int id;
    private String name;
    private boolean isAvailable;  // Availability is now a boolean
    private String type;
    private int stationId;  // stationId is an integer
    private String imageUrl;
    private double priceHourly;  // Hourly price
    private double priceDaily;   // Daily price
    private double priceMonthly; // Monthly price
    private String stationName;
    private String status; // Status of the bike (e.g., "available", "reserved", "rented")

    // Constructor
    public Bike(int id, String name, boolean isAvailable, String type, int stationId, String imageUrl,
                double priceHourly, double priceDaily, double priceMonthly, String stationName, String status) {
        this.id = id;
        this.name = name;
        this.isAvailable = isAvailable;
        this.type = type;
        this.stationId = stationId;
        this.imageUrl = imageUrl;
        this.priceHourly = priceHourly;
        this.priceDaily = priceDaily;
        this.priceMonthly = priceMonthly;
        this.stationName = stationName;
        this.status = status;
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPriceHourly() {
        return priceHourly;
    }

    public void setPriceHourly(double priceHourly) {
        this.priceHourly = priceHourly;
    }

    public double getPriceDaily() {
        return priceDaily;
    }

    public void setPriceDaily(double priceDaily) {
        this.priceDaily = priceDaily;
    }

    public double getPriceMonthly() {
        return priceMonthly;
    }

    public void setPriceMonthly(double priceMonthly) {
        this.priceMonthly = priceMonthly;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}