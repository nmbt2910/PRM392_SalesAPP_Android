package com.prm392.salesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prm392.salesapp.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class ProductListFragment extends Fragment implements ProductListAdapter.FilterListener {

    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private LinearLayout emptyLayout;
    private LinearLayout noSearchResultsLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.product_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.error_layout);
        emptyLayout = view.findViewById(R.id.empty_layout);
        noSearchResultsLayout = view.findViewById(R.id.no_search_results_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_product_list);
        Button reloadButton = view.findViewById(R.id.reload_button);
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Setup Toolbar
        toolbar.inflateMenu(R.menu.search_menu);
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductListAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        // Setup Listeners
        swipeRefreshLayout.setOnRefreshListener(() -> productViewModel.fetchProducts());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

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
            swipeRefreshLayout.setRefreshing(false);
            noSearchResultsLayout.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                adapter.updateProducts(products);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }
        });

        productViewModel.getProductError().observe(getViewLifecycleOwner(), error -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            noSearchResultsLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
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
