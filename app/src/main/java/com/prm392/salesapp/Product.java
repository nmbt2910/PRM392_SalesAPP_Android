package com.prm392.salesapp;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("ProductID")
    private int productID;

    @SerializedName("ProductName")
    private String productName;

    @SerializedName("BriefDescription")
    private String briefDescription;

    @SerializedName("FullDescription")
    private String fullDescription;

    @SerializedName("TechnicalSpecifications")
    private String technicalSpecifications;

    @SerializedName("Price")
    private double price;

    @SerializedName("ImageURL")
    private String imageURL;

    @SerializedName("CategoryID")
    private int categoryID;

    @SerializedName("CategoryName")
    private String categoryName;

    // Getters and Setters

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String getTechnicalSpecifications() {
        return technicalSpecifications;
    }

    public double getPrice() {
        return price;
    }

    public String getImageURL() {
        return imageURL;
    }
}