package com.prm392.salesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.Product;
import com.prm392.salesapp.api.ProductApiService;
import com.prm392.salesapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {
    private MutableLiveData<List<Product>> productList = new MutableLiveData<>();
    private MutableLiveData<String> productError = new MutableLiveData<>();
    private MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private List<Product> originalProductList = new ArrayList<>();

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

    public LiveData<String> getProductError() {
        return productError;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public void fetchProducts() {
        ProductApiService productApi = RetrofitClient.getProductApi();
        productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalProductList = response.body();
                    productList.setValue(originalProductList);

                    Set<String> categorySet = new HashSet<>();
                    for (Product product : originalProductList) {
                        categorySet.add(product.getCategory());
                    }
                    categories.setValue(new ArrayList<>(categorySet));

                } else {
                    productError.setValue("Product fetch failed. Please try again later.");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                productError.setValue("Product fetch failed. Please try again later.");
            }
        });
    }

    public void applyFiltersAndSort(String category, int sortOption) {
        List<Product> filteredList;

        // Filter by category
        if (category != null && !category.equals("All")) {
            filteredList = new ArrayList<>(originalProductList);
            filteredList = filteredList.stream().filter(p -> p.getCategory().equals(category)).collect(Collectors.toList());
        } else {
            filteredList = new ArrayList<>(originalProductList);
        }

        // Sort the filtered list
        sortList(filteredList, sortOption);

        productList.setValue(filteredList);
    }

    public void clearFilters() {
        productList.setValue(originalProductList);
    }

    private void sortList(List<Product> list, int sortOption) {
        switch (sortOption) {
            case 0: // Price: Low to High
                Collections.sort(list, (p1, p2) -> Float.compare(p1.getPrice(), p2.getPrice()));
                break;
            case 1: // Price: High to Low
                Collections.sort(list, (p1, p2) -> Float.compare(p2.getPrice(), p1.getPrice()));
                break;
        }
    }
}
