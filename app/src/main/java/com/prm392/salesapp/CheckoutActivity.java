package com.prm392.salesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.NumberFormat;
import java.util.Locale;

import com.prm392.salesapp.api.OrderApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText cityEditText;
    private Spinner paymentMethodSpinner;
    private TextView orderTotalTextView;
    private Button placeOrderButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_checkout);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        fullNameEditText = findViewById(R.id.edit_text_full_name);
        phoneEditText = findViewById(R.id.edit_text_phone);
        addressEditText = findViewById(R.id.edit_text_address);
        cityEditText = findViewById(R.id.edit_text_city);
        paymentMethodSpinner = findViewById(R.id.spinner_payment_method);
        orderTotalTextView = findViewById(R.id.text_view_order_total);
        placeOrderButton = findViewById(R.id.button_place_order);

        // Payment method options (example)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        // If total passed via intent, show it
        double total = getIntent().getDoubleExtra("ORDER_TOTAL", 0.0);
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        orderTotalTextView.setText(currencyFormatter.format(total));

        placeOrderButton.setOnClickListener(v -> {
            // Basic validation
            if (fullNameEditText.getText().toString().trim().isEmpty() ||
                    phoneEditText.getText().toString().trim().isEmpty() ||
                    addressEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = paymentMethodSpinner.getSelectedItem().toString();

            // Build billing address string (simple concatenation)
            String billingAddress = fullNameEditText.getText().toString().trim() + ", " +
                    phoneEditText.getText().toString().trim() + ", " +
                    addressEditText.getText().toString().trim() + ", " +
                    cityEditText.getText().toString().trim();

            if (paymentMethod.equalsIgnoreCase("Cash on Delivery")) {
                // Call backend API to create order
                SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
                String authToken = sharedPreferences.getString("AUTH_TOKEN", null);
                if (authToken == null) {
                    Toast.makeText(this, "You must be logged in to place an order.", Toast.LENGTH_SHORT).show();
                    return;
                }

                CreateOrderRequest req = new CreateOrderRequest(paymentMethod, billingAddress);
                OrderApiService orderApi = RetrofitClient.getOrderApi();
                orderApi.createOrder("Bearer " + authToken, req).enqueue(new Callback<CreateOrderResponse>() {
                    @Override
                    public void onResponse(Call<CreateOrderResponse> call, Response<CreateOrderResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(CheckoutActivity.this, "Order created: " + response.body().getOrderId(), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(CheckoutActivity.this, "Failed to create order.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateOrderResponse> call, Throwable t) {
                        Toast.makeText(CheckoutActivity.this, "Network error while creating order.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // Other payment methods currently not implemented
                Toast.makeText(this, "Selected payment method is not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
