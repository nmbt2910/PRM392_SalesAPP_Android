package com.prm392.salesapp.api;

import com.prm392.salesapp.storelocations.StoreLocation;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header; // <-- Thêm import này

public interface LocationApiService {
    @GET("api/locations")
    Call<List<StoreLocation>> getLocations(@Header("Authorization") String authToken); // <-- Thêm tham số này
}