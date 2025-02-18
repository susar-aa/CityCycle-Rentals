package com.example.citycyclerentals.models;

public class Feedback {
    private String user;
    private String feedbackText;

    public Feedback(String user, String feedbackText) {
        this.user = user;
        this.feedbackText = feedbackText;
    }

    public String getUser() {
        return user;
    }

    public String getFeedbackText() {
        return feedbackText;
    }
}