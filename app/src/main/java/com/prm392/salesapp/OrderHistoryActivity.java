package com.prm392.salesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.salesapp.api.OrderApiService;
import com.prm392.salesapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import java.io.IOException;

public class OrderHistoryActivity extends AppCompatActivity implements OrdersAdapter.OrderClickListener {
    private RecyclerView recyclerView;
    private OrdersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_orders);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchOrders();
    }

    private void fetchOrders() {
        SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);
        if (authToken == null) {
            Toast.makeText(this, "You must be logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderApiService orderApi = RetrofitClient.getOrderApi();
        orderApi.getAllOrders("Bearer " + authToken).enqueue(new Callback<List<OrderSummary>>() {
            @Override
            public void onResponse(Call<List<OrderSummary>> call, Response<List<OrderSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        adapter = new OrdersAdapter(response.body(), OrderHistoryActivity.this);
                        recyclerView.setAdapter(adapter);
                    } catch (Exception ex) {
                        Log.e("OrderHistoryActivity", "Failed to set adapter", ex);
                        Toast.makeText(OrderHistoryActivity.this, "Error displaying orders: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    int code = response.code();
                    String errBody = "";
                    try {
                        if (response.errorBody() != null) errBody = response.errorBody().string();
                    } catch (IOException e) {
                        errBody = e.getMessage();
                    }
                    Log.d("OrderHistory", "getAllOrders failed: code=" + code + ", body=" + errBody);
                    Toast.makeText(OrderHistoryActivity.this, "Failed to load orders: HTTP " + code, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderSummary>> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClicked(int orderId) {
        // Open detail activity
        android.content.Intent intent = new android.content.Intent(this, OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        startActivity(intent);
    }
}
