package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("productID")
    private int productID;

    @SerializedName("quantity")
    private int quantity;

    public CartItem(int productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }
}