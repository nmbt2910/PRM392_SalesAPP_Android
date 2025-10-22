package com.prm392.salesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.prm392.salesapp.api.CartApiService;
import com.prm392.salesapp.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener, CartAdapter.FilterListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private LinearLayout emptyCartLayout;
    private LinearLayout noSearchResultsLayout;
    private Button reloadButton;
    private TextView totalPriceTextView;
    private MaterialCardView checkoutSummaryCard;
    private Button checkoutButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cart_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar_cart);
        errorLayout = view.findViewById(R.id.error_layout_cart);
        emptyCartLayout = view.findViewById(R.id.empty_cart_layout);
        noSearchResultsLayout = view.findViewById(R.id.no_search_results_layout);
        reloadButton = view.findViewById(R.id.reload_button_cart);
        totalPriceTextView = view.findViewById(R.id.total_price_text_view);
        checkoutSummaryCard = view.findViewById(R.id.checkout_summary_card);
        checkoutButton = view.findViewById(R.id.checkout_button);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_cart);
        Toolbar toolbar = view.findViewById(R.id.toolbar_cart);

        // Setup Toolbar
        toolbar.inflateMenu(R.menu.search_menu);
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reloadButton.setOnClickListener(v -> fetchCart());
        swipeRefreshLayout.setOnRefreshListener(this::fetchCart);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter != null){
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

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
        emptyCartLayout.setVisibility(View.GONE);
        noSearchResultsLayout.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            errorLayout.setVisibility(View.VISIBLE);
            return;
        }

        CartApiService cartApi = RetrofitClient.getCartApi();
        cartApi.getCart("Bearer " + authToken).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().getItems() != null && !response.body().getItems().isEmpty()) {
                    errorLayout.setVisibility(View.GONE);
                    emptyCartLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    checkoutSummaryCard.setVisibility(View.VISIBLE);
                    adapter = new CartAdapter(response.body().getItems(), CartFragment.this, CartFragment.this);
                    recyclerView.setAdapter(adapter);

                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    totalPriceTextView.setText(currencyFormatter.format(response.body().getTotalPrice()));

                } else {
                    recyclerView.setVisibility(View.GONE);
                    checkoutSummaryCard.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        emptyCartLayout.setVisibility(View.VISIBLE);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                checkoutSummaryCard.setVisibility(View.GONE);
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

    @Override
    public void onFilterComplete(int count) {
        if (count == 0) {
            recyclerView.setVisibility(View.GONE);
            noSearchResultsLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noSearchResultsLayout.setVisibility(View.GONE);
        }
    }
}