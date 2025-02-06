package com.example.citycyclerentals.models;
public class Card {
    private String cardNumber;
    private String expiryDate;
    private String cardHolder;

    // Constructor
    public Card(String cardNumber, String expiryDate, String cardHolder) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cardHolder = cardHolder;
    }

    // Getters and Setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }
}
