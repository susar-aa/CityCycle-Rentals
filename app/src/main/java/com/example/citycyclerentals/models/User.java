package com.example.citycyclerentals.admin;

public class User {
    private int id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private String profilePicture;

    public User(int id, String username, String email, String phoneNumber, String role, String profilePicture) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.profilePicture = profilePicture;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}