package com.prm392.salesapp.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.CartItem;
import com.prm392.salesapp.CartResponse;
import com.prm392.salesapp.api.CartApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {
    private MutableLiveData<CartResponse> cartResponse = new MutableLiveData<>();
    private MutableLiveData<String> cartError = new MutableLiveData<>();

    public LiveData<CartResponse> getCartResponse() {
        return cartResponse;
    }

    public LiveData<String> getCartError() {
        return cartError;
    }

    public void addToCart(Context context, int productId, int quantity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            cartError.setValue("Authentication token not found.");
            return;
        }

        CartApiService cartApi = RetrofitClient.getCartApi();
        CartItem cartItem = new CartItem(productId, quantity);

        cartApi.addToCart("Bearer " + authToken, cartItem).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.code() == 201) {
                    cartResponse.setValue(response.body());
                } else {
                    cartError.setValue("Cart adding failed.");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                cartError.setValue("Cart adding failed.");
            }
        });
    }
}