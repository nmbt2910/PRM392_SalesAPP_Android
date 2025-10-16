package com.prm392.salesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.Product;
import com.prm392.salesapp.api.ProductApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailViewModel extends ViewModel {
    private MutableLiveData<Product> product = new MutableLiveData<>();
    private MutableLiveData<String> productError = new MutableLiveData<>();

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<String> getProductError() {
        return productError;
    }

    public void fetchProductById(int productId) {
        ProductApiService productApi = RetrofitClient.getProductApi();
        productApi.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    product.setValue(response.body());
                } else {
                    productError.setValue("Product fetch failed. Please try again later.");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                productError.setValue("Product fetch failed. Please try again later.");
            }
        });
    }
}