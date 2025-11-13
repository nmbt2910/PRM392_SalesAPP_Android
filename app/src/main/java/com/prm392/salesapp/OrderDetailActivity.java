package com.prm392.salesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import java.math.BigDecimal;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392.salesapp.api.OrderApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView idView, statusView, paymentView, amountView, paymentStatusView, dateView;
    private TextView billingNameView, billingPhoneView, billingAddressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_order_detail);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        idView = findViewById(R.id.text_detail_order_id);
        statusView = findViewById(R.id.text_detail_status);
        paymentView = findViewById(R.id.text_detail_payment_method);
        billingNameView = findViewById(R.id.text_billing_name);
        billingPhoneView = findViewById(R.id.text_billing_phone);
        billingAddressView = findViewById(R.id.text_billing_address);
        amountView = findViewById(R.id.text_detail_amount);
    paymentStatusView = findViewById(R.id.text_detail_payment_status);
    dateView = findViewById(R.id.text_detail_order_date);

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchOrderDetails(orderId);
    }

    private void fetchOrderDetails(int orderId) {
        SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);
        if (authToken == null) {
            Toast.makeText(this, "You must be logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderApiService orderApi = RetrofitClient.getOrderApi();
        orderApi.getOrderDetails("Bearer " + authToken, orderId).enqueue(new Callback<OrderDetail>() {
            @Override
            public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderDetail d = response.body();
                    idView.setText("Order " + d.getOrderID());
                    statusView.setText("Status: " + d.getOrderStatus());
                    paymentView.setText("Payment: " + d.getPaymentMethod());

                    // Billing: expected as comma-separated: name, phone, address...
                    String billing = d.getBillingAddress();
                    if (billing != null && !billing.isEmpty()) {
                        String[] parts = billing.split(",");
                        String name = parts.length > 0 ? parts[0].trim() : "";
                        String phone = parts.length > 1 ? parts[1].trim() : "";
                        String address = "";
                        if (parts.length > 2) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < parts.length; i++) {
                                if (sb.length() > 0) sb.append(", ");
                                sb.append(parts[i].trim());
                            }
                            address = sb.toString();
                        }
                        billingNameView.setText("Name: " + name);
                        billingPhoneView.setText("Phone: " + phone);
                        billingAddressView.setText("Address: " + address);
                    } else {
                        billingNameView.setText("Name: -");
                        billingPhoneView.setText("Phone: -");
                        billingAddressView.setText("Address: -");
                    }

                    // Format amount to avoid scientific notation
                    String amountStr = BigDecimal.valueOf(d.getPaymentAmount()).toPlainString();
                    amountView.setText("Amount: " + amountStr);
                    paymentStatusView.setText("Payment status: " + d.getPaymentStatus());
                    // Display order date if available (formatted)
                    if (d.getOrderDate() != null && !d.getOrderDate().isEmpty()) {
                        dateView.setText("Order date: " + DateUtils.formatIsoToLocal(d.getOrderDate()));
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to load details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetail> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
