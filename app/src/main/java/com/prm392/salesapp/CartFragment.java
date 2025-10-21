package com.prm392.salesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.salesapp.api.CartApiService;
import com.prm392.salesapp.network.RetrofitClient;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button reloadButton;
    private TextView totalPriceTextView;
    private Button checkoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cart_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar_cart);
        errorLayout = view.findViewById(R.id.error_layout_cart);
        reloadButton = view.findViewById(R.id.reload_button_cart);
        totalPriceTextView = view.findViewById(R.id.total_price_text_view);
        checkoutButton = view.findViewById(R.id.checkout_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reloadButton.setOnClickListener(v -> fetchCart());

        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Checkout not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCart();
    }

    private void fetchCart() {
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            return;
        }

        CartApiService cartApi = RetrofitClient.getCartApi();
        cartApi.getCart("Bearer " + authToken).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    errorLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new CartAdapter(response.body().getItems(), CartFragment.this);
                    recyclerView.setAdapter(adapter);

                    DecimalFormat formatter = new DecimalFormat("$#,##0.##");
                    totalPriceTextView.setText(formatter.format(response.body().getTotalPrice()));

                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onIncreaseQuantity(int cartItemId, int currentQuantity) {
        updateQuantity(cartItemId, currentQuantity + 1);
    }

    @Override
    public void onDecreaseQuantity(int cartItemId, int currentQuantity) {
        if (currentQuantity > 1) {
            updateQuantity(cartItemId, currentQuantity - 1);
        }
    }

    @Override
    public void onRemoveItem(int cartItemId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item from your cart?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeItemFromCart(cartItemId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeItemFromCart(int cartItemId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            Toast.makeText(getContext(), "Authentication token not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        CartApiService cartApi = RetrofitClient.getCartApi();
        cartApi.removeCartItem("Bearer " + authToken, cartItemId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                    fetchCart();
                } else {
                    Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuantity(int cartItemId, int newQuantity) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            Toast.makeText(getContext(), "Authentication token not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        CartApiService cartApi = RetrofitClient.getCartApi();
        UpdateCartItemRequest request = new UpdateCartItemRequest(newQuantity);
        cartApi.updateCartItem("Bearer " + authToken, cartItemId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cart item updated", Toast.LENGTH_SHORT).show();
                    fetchCart();
                } else {
                    Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
