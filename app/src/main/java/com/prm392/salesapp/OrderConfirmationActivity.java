package com.prm392.salesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392.salesapp.api.OrderApiService;
import com.prm392.salesapp.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationActivity extends AppCompatActivity {

    private TextView orderIdTextView;
    private TextView orderStatusTextView;
    private TextView paymentMethodTextView;
    private TextView billingAddressTextView;
    private TextView totalAmountTextView;
    private TextView paymentStatusTextView;
    private TextView orderDateTextView;
    private Button viewOrderDetailsButton;
    private Button continueShoppingButton;
    private View loadingView;
    private View contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Initialize views
        orderIdTextView = findViewById(R.id.text_order_id);
        orderStatusTextView = findViewById(R.id.text_order_status);
        paymentMethodTextView = findViewById(R.id.text_payment_method);
        billingAddressTextView = findViewById(R.id.text_billing_address);
        totalAmountTextView = findViewById(R.id.text_total_amount);
        paymentStatusTextView = findViewById(R.id.text_payment_status);
        orderDateTextView = findViewById(R.id.text_order_date);
        viewOrderDetailsButton = findViewById(R.id.button_view_order_details);
        continueShoppingButton = findViewById(R.id.button_continue_shopping);
        loadingView = findViewById(R.id.loading_view);
        contentView = findViewById(R.id.content_view);

        // Get order ID from intent
        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch order details
        fetchOrderConfirmation(orderId);

        // Set up button listeners
        viewOrderDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderConfirmationActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", orderId);
            startActivity(intent);
            finish();
        });

        continueShoppingButton.setOnClickListener(v -> {
            // Navigate back to home
            Intent intent = new Intent(OrderConfirmationActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchOrderConfirmation(int orderId) {
        loadingView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);
        
        if (authToken == null) {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        OrderApiService orderApi = RetrofitClient.getOrderApi();
        orderApi.getOrderDetails("Bearer " + authToken, orderId).enqueue(new Callback<OrderDetail>() {
            @Override
            public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                loadingView.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    contentView.setVisibility(View.VISIBLE);
                    displayOrderConfirmation(response.body());
                } else {
                    Toast.makeText(OrderConfirmationActivity.this, 
                        "Failed to load order details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<OrderDetail> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(OrderConfirmationActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayOrderConfirmation(OrderDetail orderDetail) {
        // Display order information
        orderIdTextView.setText("Order #" + orderDetail.getOrderID());
        orderStatusTextView.setText(orderDetail.getOrderStatus());
        paymentMethodTextView.setText(orderDetail.getPaymentMethod());
        billingAddressTextView.setText(orderDetail.getBillingAddress());
        
        // Format amount
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        totalAmountTextView.setText(currencyFormatter.format(orderDetail.getPaymentAmount()));
        
        // Display payment status with color coding
        String paymentStatus = orderDetail.getPaymentStatus();
        paymentStatusTextView.setText(paymentStatus);
        
        if ("Completed".equalsIgnoreCase(paymentStatus) || "Success".equalsIgnoreCase(paymentStatus)) {
            paymentStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if ("Pending".equalsIgnoreCase(paymentStatus)) {
            paymentStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("Failed".equalsIgnoreCase(paymentStatus)) {
            paymentStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        
        // Display order date
        if (orderDetail.getOrderDate() != null && !orderDetail.getOrderDate().isEmpty()) {
            orderDateTextView.setText(orderDetail.getOrderDate());
        }
    }
}
