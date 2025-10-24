package com.prm392.salesapp.network;

import com.prm392.salesapp.api.AuthApiService;
import com.prm392.salesapp.api.CartApiService;
import com.prm392.salesapp.api.ChatApiService;
import com.prm392.salesapp.api.OrderApiService;
import com.prm392.salesapp.api.ProductApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthApiService getAuthApi() {
        return getClient().create(AuthApiService.class);
    }

    public static ProductApiService getProductApi() {
        return getClient().create(ProductApiService.class);
    }

    public static CartApiService getCartApi() {
        return getClient().create(CartApiService.class);
    }

    public static ChatApiService getChatApi() {
        return getClient().create(ChatApiService.class);
    }

    public static OrderApiService getOrderApi() {
        return getClient().create(OrderApiService.class);
    }
}