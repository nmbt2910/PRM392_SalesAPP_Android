package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class CartItemDetail {

    @SerializedName("CartItemID")
    private int cartItemID;

    @SerializedName("ProductID")
    private int productID;

    @SerializedName("ProductName")
    private String productName;

    @SerializedName("ImageURL")
    private String imageURL;

    @SerializedName("Quantity")
    private int quantity;

    @SerializedName("Price")
    private double price;

    public int getCartItemID() {
        return cartItemID;
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}