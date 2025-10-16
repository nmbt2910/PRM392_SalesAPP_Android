package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class UpdateCartItemRequest {
    @SerializedName("quantity")
    private int quantity;

    public UpdateCartItemRequest(int quantity) {
        this.quantity = quantity;
    }
}
