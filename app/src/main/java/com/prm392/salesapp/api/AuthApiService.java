package com.prm392.salesapp.api;

import com.prm392.salesapp.LoginRequest;
import com.prm392.salesapp.LoginResponse;
import com.prm392.salesapp.SignupRequest;
import com.prm392.salesapp.SignupResponse;
import com.prm392.salesapp.UpdateProfileRequest;
import com.prm392.salesapp.UpdateProfileResponse;
import com.prm392.salesapp.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @GET("api/auth/profile")
    Call<UserProfile> getProfile(@Header("Authorization") String authToken);

    @PUT("api/auth/profile")
    Call<UpdateProfileResponse> updateProfile(@Header("Authorization") String authToken, @Body UpdateProfileRequest request);
}
