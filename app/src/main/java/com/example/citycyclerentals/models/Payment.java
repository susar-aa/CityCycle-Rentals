package com.example.citycyclerentals.models;

public class Payment {
    private int id;  // Changed from String to int
    private int userId;
    private double amount;
    private String date;
    private String status;
    private String paymentMethod;

    public Payment(int id, int userId, double amount, String date, String status, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
