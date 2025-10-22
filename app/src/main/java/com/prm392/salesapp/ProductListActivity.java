package com.prm392.salesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.salesapp.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        errorLayout = findViewById(R.id.error_layout);
        reloadButton = findViewById(R.id.reload_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // The ProductListAdapter now requires a FilterListener. Since this activity doesn't have search, we pass null.
        adapter = new ProductListAdapter(new ArrayList<>(), null);
        recyclerView.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getProductList().observe(this, products -> {
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            // Update the existing adapter instead of creating a new one.
            adapter.updateProducts(products);
        });

        productViewModel.getProductError().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        });

        reloadButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            productViewModel.fetchProducts();
        });

        productViewModel.fetchProducts();
    }
}
