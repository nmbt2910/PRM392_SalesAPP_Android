package com.prm392.salesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392.salesapp.api.OrderApiService;
import com.prm392.salesapp.network.RetrofitClient;
import com.prm392.salesapp.payment.VNPayHelper;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private WebView webView;
    private int orderId;
    private String paymentMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webview_payment);
        
        // Setup toolbar back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_payment);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                // User cancelled payment
                updatePaymentStatus("Cancelled", "Cancelled");
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
        
        // Get payment details from intent
        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        double amount = getIntent().getDoubleExtra("AMOUNT", 0.0);
        String orderInfo = getIntent().getStringExtra("ORDER_INFO");

        Log.d("PaymentActivity", "Order ID: " + orderId);
        Log.d("PaymentActivity", "Amount: " + amount);
        Log.d("PaymentActivity", "Payment Method: " + paymentMethod);
        Log.d("PaymentActivity", "Order Info: " + orderInfo);

        if (orderId == -1 || amount <= 0) {
            Toast.makeText(this, "Invalid payment details - Order ID: " + orderId + ", Amount: " + amount, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup WebView
        setupWebView();

        // Generate payment URL based on method
        String paymentUrl = null;
        if ("VNPay".equalsIgnoreCase(paymentMethod)) {
            // Return URL should be your app's deep link
            String returnUrl = "salesapp://payment/return";
            paymentUrl = VNPayHelper.generatePaymentUrl(orderId, amount, orderInfo, returnUrl);
        } else if ("ZaloPay".equalsIgnoreCase(paymentMethod)) {
            // TODO: Implement ZaloPay integration
            Toast.makeText(this, "ZaloPay integration coming soon", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if ("PayPal".equalsIgnoreCase(paymentMethod)) {
            // TODO: Implement PayPal integration
            Toast.makeText(this, "PayPal integration coming soon", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(this, "Failed to generate payment URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d("PaymentActivity", "URL: " + url);
                
                // Check if this is a return URL
                if (url.startsWith("salesapp://payment/return")) {
                    handlePaymentReturn(request.getUrl());
                    return true;
                }
                
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("PaymentActivity", "Page finished: " + url);
            }
        });
    }

    private void handlePaymentReturn(Uri uri) {
        try {
            // Parse query parameters
            Map<String, String> params = new HashMap<>();
            for (String param : uri.getQueryParameterNames()) {
                params.put(param, uri.getQueryParameter(param));
            }

            if ("VNPay".equalsIgnoreCase(paymentMethod)) {
                handleVNPayReturn(params);
            }
            
        } catch (Exception e) {
            Log.e("PaymentActivity", "Error handling payment return", e);
            Toast.makeText(this, "Payment verification failed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleVNPayReturn(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        
        Log.d("PaymentActivity", "VNPay response code: " + responseCode);
        
        // Verify signature
        if (!VNPayHelper.verifySignature(params)) {
            Toast.makeText(this, "Invalid payment signature", Toast.LENGTH_LONG).show();
            navigateToOrderConfirmation(false);
            return;
        }

        // Check response code
        if ("00".equals(responseCode)) {
            // Payment successful
            updatePaymentStatus("Completed", "Processing");
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
            navigateToOrderConfirmation(true);
        } else {
            // Payment failed
            String message = VNPayHelper.getResponseMessage(responseCode);
            updatePaymentStatus("Failed", "Cancelled");
            Toast.makeText(this, "Payment failed: " + message, Toast.LENGTH_LONG).show();
            navigateToOrderConfirmation(false);
        }
    }

    private void updatePaymentStatus(String paymentStatus, String orderStatus) {
        SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);
        
        if (authToken == null) {
            Log.e("PaymentActivity", "No auth token found");
            return;
        }

        OrderApiService orderApi = RetrofitClient.getOrderApi();
        
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("paymentStatus", paymentStatus);
        statusUpdate.put("orderStatus", orderStatus);
        
        orderApi.updatePaymentStatus("Bearer " + authToken, orderId, statusUpdate)
            .enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Log.d("PaymentActivity", "Payment status updated successfully");
                    } else {
                        Log.e("PaymentActivity", "Failed to update payment status");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e("PaymentActivity", "Error updating payment status", t);
                }
            });
    }

    private void navigateToOrderConfirmation(boolean success) {
        Intent intent = new Intent(PaymentActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        intent.putExtra("PAYMENT_SUCCESS", success);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // User cancelled payment
            updatePaymentStatus("Cancelled", "Cancelled");
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}
