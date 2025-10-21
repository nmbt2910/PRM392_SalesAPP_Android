package com.prm392.salesapp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prm392.salesapp.UpdateProfileRequest;
import com.prm392.salesapp.UpdateProfileResponse;
import com.prm392.salesapp.UserProfile;
import com.prm392.salesapp.api.AuthApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileViewModel extends AndroidViewModel {

    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<UpdateProfileResponse> updateResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<UpdateProfileResponse> getUpdateResult() {
        return updateResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchProfile() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            error.setValue("Authentication error.");
            return;
        }

        AuthApiService authApi = RetrofitClient.getAuthApi();
        authApi.getProfile("Bearer " + authToken).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    userProfile.setValue(response.body());
                } else {
                    error.setValue("Failed to load current profile.");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                error.setValue("Network error. Please try again.");
            }
        });
    }

    public void updateProfile(UpdateProfileRequest request) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            error.setValue("Authentication error.");
            return;
        }

        AuthApiService authApi = RetrofitClient.getAuthApi();
        authApi.updateProfile("Bearer " + authToken, request).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful()) {
                    updateResult.setValue(response.body());
                } else {
                    error.setValue("Failed to update profile. Please check your input.");
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                error.setValue("Network error. Please try again.");
            }
        });
    }
}
