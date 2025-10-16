package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {

    @SerializedName("items")
    private List<CartItemDetail> items;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("itemCount")
    private int itemCount;

    public List<CartItemDetail> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }
}