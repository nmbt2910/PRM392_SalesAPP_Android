package com.prm392.salesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.LoginRequest;
import com.prm392.salesapp.LoginResponse;
import com.prm392.salesapp.UserProfile;
import com.prm392.salesapp.api.AuthApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    private SingleLiveEvent<String> loginError = new SingleLiveEvent<>();
    private MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();

    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponse;
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public void login(String username, String password) {
        AuthApiService authApi = RetrofitClient.getAuthApi();
        LoginRequest loginRequest = new LoginRequest(username, password);

        authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    loginResponse.setValue(response.body());
                } else {
                    loginError.setValue("Login failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginError.setValue(t.getMessage());
            }
        });
    }

    public void getProfile(String authToken) {
        AuthApiService authApi = RetrofitClient.getAuthApi();
        authApi.getProfile("Bearer " + authToken).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    userProfile.setValue(response.body());
                } else {
                    loginError.setValue("Failed to get user profile");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                loginError.setValue(t.getMessage());
            }
        });
    }
}