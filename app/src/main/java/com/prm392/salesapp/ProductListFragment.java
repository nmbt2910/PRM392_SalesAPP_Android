package com.prm392.salesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392.salesapp.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class ProductListFragment extends Fragment {

    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.product_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.error_layout);
        Button reloadButton = view.findViewById(R.id.reload_button);
        ImageButton filterButton = view.findViewById(R.id.filter_button);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductListAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Setup Listeners
        filterButton.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = FilterBottomSheetFragment.newInstance();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        reloadButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            productViewModel.fetchProducts();
        });

        // Observe LiveData
        observeViewModel();

        // Initial data fetch
        productViewModel.fetchProducts();

        return view;
    }

    private void observeViewModel() {
        productViewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            adapter.updateProducts(products);
        });

        productViewModel.getProductError().observe(getViewLifecycleOwner(), error -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        });
    }
}
