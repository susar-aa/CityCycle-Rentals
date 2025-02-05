package com.example.citycyclerentals;

public class Promotion {
    private String promoCode;
    private String discountPercentage;
    private String startDate;
    private String endDate;

    // Constructor with parameters
    public Promotion(String promoCode, String discountPercentage, String endDate, String startDate) {
        this.promoCode = promoCode;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter methods
    public String getPromoCode() {
        return promoCode;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
