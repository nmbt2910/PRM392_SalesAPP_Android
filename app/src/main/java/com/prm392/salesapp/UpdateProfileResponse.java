package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private UserProfile user;

    public String getMessage() {
        return message;
    }

    public UserProfile getUser() {
        return user;
    }
}
