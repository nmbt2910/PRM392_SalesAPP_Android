package com.prm392.salesapp.api;

import com.prm392.salesapp.LoginRequest;
import com.prm392.salesapp.LoginResponse;
import com.prm392.salesapp.SignupRequest;
import com.prm392.salesapp.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);
}