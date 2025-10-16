package com.prm392.salesapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private TextView emailTextView;
    private Button logoutButton;
    private Button testNotificationButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.username_textview);
        emailTextView = view.findViewById(R.id.email_textview);
        logoutButton = view.findViewById(R.id.logout_button);
        testNotificationButton = view.findViewById(R.id.test_notification_button);

        logoutButton.setOnClickListener(v -> {
            // Cancel background work
            WorkManager.getInstance(getContext()).cancelUniqueWork("CartNotificationWork");

            // Clear any existing notifications and the badge
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            // Clear the auth token
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("AUTH_TOKEN");
            editor.apply();

            // Redirect to LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        testNotificationButton.setOnClickListener(v -> {
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CartNotificationWorker.class).build();
            WorkManager.getInstance(getContext()).enqueue(oneTimeWorkRequest);
        });

        // TODO: Fetch and display user information

        return view;
    }
}