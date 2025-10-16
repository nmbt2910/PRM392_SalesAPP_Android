package com.prm392.salesapp;

public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;

    public SignupRequest(String username, String password, String email, String phoneNumber, String address, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    // Getters and setters
}