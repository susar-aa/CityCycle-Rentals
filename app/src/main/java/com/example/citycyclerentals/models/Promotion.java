package com.example.citycyclerentals.models;

public class Promotion {
    private int promoId;
    private String promoCode;
    private double discountPercentage;
    private String startDate;
    private String endDate;

    public Promotion(int promoId, String promoCode, double discountPercentage, String startDate, String endDate) {
        this.promoId = promoId;
        this.promoCode = promoCode;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getPromoId() {
        return promoId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}