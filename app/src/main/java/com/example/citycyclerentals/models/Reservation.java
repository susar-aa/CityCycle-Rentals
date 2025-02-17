package com.example.citycyclerentals.models;

public class Reservation {
    private int reservationId;
    private String name;
    private String startDate;
    private String endDate;
    private double totalPrice;
    private String status;
    private String bikeImageUrl;
    private String bikeName;
    private int bikeId;
    private double discount;

    // Updated constructor with bikeName and discount
    public Reservation(int reservationId, String name, String startDate, String endDate, double totalPrice, String status, String bikeImageUrl, String bikeName, int bikeId, double discount) {
        this.reservationId = reservationId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bikeImageUrl = bikeImageUrl;
        this.bikeName = bikeName;
        this.bikeId = bikeId;
        this.discount = discount;
    }

    // Getters
    public int getReservationId() {
        return reservationId;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getBikeImageUrl() {
        return bikeImageUrl;
    }

    public double getDiscount() {
        return discount;
    }

    public String getBikeName() {
        return bikeName; // Getter for bikeName
    }

    public int getBikeId() {
        return bikeId;
    }

    // Setters
    public void setBikeImageUrl(String bikeImageUrl) {
        this.bikeImageUrl = bikeImageUrl;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }
}