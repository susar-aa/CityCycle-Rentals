package com.example.citycyclerentals.models;

public class ReservedTime {
    private String startDate;
    private String endDate;
    private String status;

    public ReservedTime(String startDate, String endDate, String status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }
}
