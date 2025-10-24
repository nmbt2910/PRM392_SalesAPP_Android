package com.prm392.salesapp.api;

import com.prm392.salesapp.CreateOrderRequest;
import com.prm392.salesapp.CreateOrderResponse;
import com.prm392.salesapp.OrderSummary;
import com.prm392.salesapp.OrderDetail;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderApiService {
	@POST("api/orders")
	Call<CreateOrderResponse> createOrder(@Header("Authorization") String authToken, @Body CreateOrderRequest body);

	// Try alternate path in case router is mounted at /api/orders on the server
	@GET("api/orders/getAllOrders")
	Call<java.util.List<OrderSummary>> getAllOrders(@Header("Authorization") String authToken);

	@GET("api/orders/getOrdersById/{orderId}")
	Call<OrderDetail> getOrderDetails(@Header("Authorization") String authToken, @Path("orderId") int orderId);
}
