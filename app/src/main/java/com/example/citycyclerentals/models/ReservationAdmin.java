package com.example.citycyclerentals.models;

import java.io.Serializable;

public class ReservationAdmin implements Serializable {
    private int reservationId;
    private String name;
    private String contactNumber;
    private String nic;
    private String startDate;
    private String endDate;
    private double totalPrice;
    private double discount;
    private String status;
    private String paymentMethod;
    private String bikeImageUrl;
    private String bikeName;
    private int bikeId;

    public ReservationAdmin(int reservationId, String name, String contactNumber, String nic, String startDate, String endDate, double totalPrice, double discount, String status, String paymentMethod, String bikeImageUrl, String bikeName, int bikeId) {
        this.reservationId = reservationId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.nic = nic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.bikeImageUrl = bikeImageUrl;
        this.bikeName = bikeName;
        this.bikeId = bikeId;
    }

    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBikeImageUrl() {
        return bikeImageUrl;
    }

    public void setBikeImageUrl(String bikeImageUrl) {
        this.bikeImageUrl = bikeImageUrl;
    }

    public String getBikeName() {
        return bikeName;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }

    public int getBikeId() {
        return bikeId;
    }

    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }
}