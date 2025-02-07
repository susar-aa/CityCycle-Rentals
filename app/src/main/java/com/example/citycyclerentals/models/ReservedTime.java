package com.example.citycyclerentals.models;

public class ReservedTime {
    private String startDate;
    private String endDate;

    public ReservedTime(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
