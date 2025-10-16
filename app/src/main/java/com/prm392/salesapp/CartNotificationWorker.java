package com.prm392.salesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.prm392.salesapp.api.CartApiService;
import com.prm392.salesapp.network.RetrofitClient;

import retrofit2.Response;

public class CartNotificationWorker extends Worker {

    private static final String CHANNEL_ID = "cart_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    public CartNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            cancelNotification();
            return Result.success();
        }

        try {
            CartApiService cartApi = RetrofitClient.getCartApi();
            Response<Cart> response = cartApi.getCart("Bearer " + authToken).execute();

            if (response.isSuccessful() && response.body() != null) {
                int itemCount = response.body().getItemCount();
                if (itemCount > 0) {
                    showNotification(itemCount);
                } else {
                    cancelNotification();
                }
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (Exception e) {
            return Result.retry();
        }
    }

    private void showNotification(int itemCount) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Cart Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // We no longer need to set setShowBadge(true)
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle("Items in your cart")
                .setContentText("You have " + itemCount + " items in your cart.")
                // We no longer need to set the badge number with .setNumber()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}