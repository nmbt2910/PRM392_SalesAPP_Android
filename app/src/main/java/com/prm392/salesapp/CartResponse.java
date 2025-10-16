package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class CartResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}