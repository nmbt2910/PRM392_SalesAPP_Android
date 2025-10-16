package com.prm392.salesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prm392.salesapp.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class ProductListFragment extends Fragment {

    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button reloadButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.product_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.error_layout);
        reloadButton = view.findViewById(R.id.reload_button);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_product_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductListAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ProductListAdapter(products);
            recyclerView.setAdapter(adapter);
        });

        productViewModel.getProductError().observe(getViewLifecycleOwner(), error -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        });

        reloadButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            productViewModel.fetchProducts();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> productViewModel.fetchProducts());

        productViewModel.fetchProducts();

        return view;
    }
}