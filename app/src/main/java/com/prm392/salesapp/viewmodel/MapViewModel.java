package com.prm392.salesapp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prm392.salesapp.storelocations.StoreLocation;
import com.prm392.salesapp.api.LocationApiService;
import com.prm392.salesapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends AndroidViewModel {

    private final MutableLiveData<List<StoreLocation>> locations = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private Application application;

    public MapViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<List<StoreLocation>> getLocations() {
        return locations;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchLocations() {
        SharedPreferences sharedPreferences = application.getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            error.setValue("Authentication Error. Please log in again.");
            return;
        }

        String fullAuthToken = "Bearer " + authToken;

        LocationApiService api = RetrofitClient.getLocationApi();

        api.getLocations(fullAuthToken).enqueue(new Callback<List<StoreLocation>>() {
            @Override
            public void onResponse(Call<List<StoreLocation>> call, Response<List<StoreLocation>> response) {
                if (response.isSuccessful()) {
                    locations.setValue(response.body());
                } else {
                    error.setValue("Failed to load locations. (Error: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<StoreLocation>> call, Throwable t) {
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }
}