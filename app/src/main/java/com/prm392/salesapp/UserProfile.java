package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class UserProfile {

    @SerializedName("UserID")
    private int userId;

    @SerializedName("Username")
    private String username;

    @SerializedName("Email")
    private String email;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("Address")
    private String address;

    @SerializedName("Role")
    private String role;

    // Getters for all fields
    public int getUserId() {
        return userId;
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

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }
}
