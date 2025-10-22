package com.prm392.salesapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

        if (authToken == null) {
            // User is not logged in, redirect to LoginActivity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Prevents the rest of the code from executing
        }

        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Home");
                    tab.setIcon(R.drawable.ic_home);
                    break;
                case 1:
                    tab.setText("Cart");
                    tab.setIcon(R.drawable.ic_shopping_cart);
                    break;
                case 2:
                    tab.setText("Chat");
                    tab.setIcon(R.drawable.ic_chat);
                    break;
                case 3:
                    tab.setText("Profile");
                    tab.setIcon(R.drawable.ic_person);
                    break;
            }
        }).attach();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Schedule a one-time notification check when the app is closed or backgrounded
        OneTimeWorkRequest cartWorkRequest = new OneTimeWorkRequest.Builder(CartNotificationWorker.class).build();
        WorkManager.getInstance(this).enqueue(cartWorkRequest);
    }
}
