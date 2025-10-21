package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("address")
    private String address;

    @SerializedName("currentPassword")
    private String currentPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public UpdateProfileRequest(String email, String phoneNumber, String address, String currentPassword, String newPassword) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
