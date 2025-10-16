package com.prm392.salesapp.api;

import com.prm392.salesapp.Cart;
import com.prm392.salesapp.CartItem;
import com.prm392.salesapp.CartResponse;
import com.prm392.salesapp.UpdateCartItemRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApiService {
    @POST("api/cart/items")
    Call<CartResponse> addToCart(@Header("Authorization") String authToken, @Body CartItem cartItem);

    @GET("api/cart")
    Call<Cart> getCart(@Header("Authorization") String authToken);

    @PUT("api/cart/items/{itemId}")
    Call<CartResponse> updateCartItem(@Header("Authorization") String authToken, @Path("itemId") int itemId, @Body UpdateCartItemRequest body);

    @DELETE("api/cart/items/{itemId}")
    Call<CartResponse> removeCartItem(@Header("Authorization") String authToken, @Path("itemId") int itemId);
}