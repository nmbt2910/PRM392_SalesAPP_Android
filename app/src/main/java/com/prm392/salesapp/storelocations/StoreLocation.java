package com.prm392.salesapp.storelocations;

import com.google.gson.annotations.SerializedName;

public class StoreLocation {

    @SerializedName("LocationID")
    private int locationID;

    @SerializedName("Latitude")
    private double latitude;

    @SerializedName("Longitude")
    private double longitude;

    @SerializedName("Address")
    private String address;

    // Getters
    public int getLocationID() { return locationID; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
}