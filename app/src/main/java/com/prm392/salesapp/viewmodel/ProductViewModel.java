package com.prm392.salesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.Product;
import com.prm392.salesapp.api.ProductApiService;
import com.prm392.salesapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {
    private MutableLiveData<List<Product>> productList = new MutableLiveData<>();
    private MutableLiveData<String> productError = new MutableLiveData<>();

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

    public LiveData<String> getProductError() {
        return productError;
    }

    public void fetchProducts() {
        ProductApiService productApi = RetrofitClient.getProductApi();
        productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList.setValue(response.body());
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
}