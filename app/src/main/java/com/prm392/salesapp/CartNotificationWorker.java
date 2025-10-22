package com.prm392.salesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.prm392.salesapp.api.CartApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Response;

public class CartNotificationWorker extends Worker {

    private static final String TAG = "CartNotificationWorker";
    private static final String CHANNEL_ID = "cart_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    public CartNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting cart notification work.");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            Log.d(TAG, "Auth token is null. Cancelling notification.");
            cancelNotification();
            return Result.success();
        }

        try {
            CartApiService cartApi = RetrofitClient.getCartApi();
            Response<Cart> response = cartApi.getCart("Bearer " + authToken).execute();

            if (response.isSuccessful() && response.body() != null) {
                int itemCount = response.body().getItemCount();
                Log.d(TAG, "Successfully fetched cart. Item count: " + itemCount);
                showNotification(itemCount);
                return Result.success();
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                Log.e(TAG, "API call failed with code: " + response.code() + ", error body: " + errorBody);
                return Result.retry();
            }
        } catch (Exception e) {
            Log.e(TAG, "An exception occurred during cart fetch", e);
            return Result.retry();
        }
    }

    private void showNotification(int itemCount) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Cart Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle("Items in your cart")
                .setContentText("You have " + itemCount + " items in your cart.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}