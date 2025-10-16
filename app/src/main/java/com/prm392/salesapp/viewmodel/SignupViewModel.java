package com.prm392.salesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.SignupRequest;
import com.prm392.salesapp.SignupResponse;
import com.prm392.salesapp.api.AuthApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupViewModel extends ViewModel {
    private MutableLiveData<SignupResponse> signupResponse = new MutableLiveData<>();
    private MutableLiveData<String> signupError = new MutableLiveData<>();

    public LiveData<SignupResponse> getSignupResponse() {
        return signupResponse;
    }

    public LiveData<String> getSignupError() {
        return signupError;
    }

    public void signup(String username, String password, String email, String phoneNumber, String address, String role) {
        AuthApiService authApi = RetrofitClient.getAuthApi();
        SignupRequest signupRequest = new SignupRequest(username, password, email, phoneNumber, address, role);

        authApi.signup(signupRequest).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.code() == 201) {
                    signupResponse.setValue(response.body());
                } else {
                    signupError.setValue("Signup failed");
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                signupError.setValue(t.getMessage());
            }
        });
    }
}