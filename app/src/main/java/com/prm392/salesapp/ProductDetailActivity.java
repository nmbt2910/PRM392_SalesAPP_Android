package com.prm392.salesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.prm392.salesapp.viewmodel.CartViewModel;
import com.prm392.salesapp.viewmodel.ProductDetailViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductDetailViewModel productDetailViewModel;
    private CartViewModel cartViewModel;
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView productSpecsTextView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button reloadButton;
    private TextInputEditText quantityInput;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImageView = findViewById(R.id.product_image_detail);
        productNameTextView = findViewById(R.id.product_name_detail);
        productPriceTextView = findViewById(R.id.product_price_detail);
        productDescriptionTextView = findViewById(R.id.product_description_full);
        productSpecsTextView = findViewById(R.id.product_specs);
        progressBar = findViewById(R.id.progress_bar_detail);
        errorLayout = findViewById(R.id.error_layout_detail);
        reloadButton = findViewById(R.id.reload_button_detail);
        quantityInput = findViewById(R.id.quantity_input);
        addToCartButton = findViewById(R.id.add_to_cart_button);

        productDetailViewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        productDetailViewModel.getProduct().observe(this, product -> {
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            updateProductDetails(product);
        });

        productDetailViewModel.getProductError().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        });

        cartViewModel.getCartResponse().observe(this, cartResponse -> {
            Toast.makeText(this, "Cart added successfully", Toast.LENGTH_SHORT).show();
        });

        cartViewModel.getCartError().observe(this, error -> {
            Toast.makeText(this, "Cart adding failed.", Toast.LENGTH_SHORT).show();
        });

        reloadButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            productDetailViewModel.fetchProductById(productId);
        });

        addToCartButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();
            if (!quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                cartViewModel.addToCart(this, productId, quantity);
            } else {
                Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
            }
        });

        if (productId != -1) {
            productDetailViewModel.fetchProductById(productId);
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateProductDetails(Product product) {
        getSupportActionBar().setTitle(product.getProductName());
        productNameTextView.setText(product.getProductName());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        productPriceTextView.setText(currencyFormatter.format(product.getPrice()));
        productDescriptionTextView.setText(product.getFullDescription());
        productSpecsTextView.setText(product.getTechnicalSpecifications());

        Glide.with(this)
                .load(product.getImageURL())
                .into(productImageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
