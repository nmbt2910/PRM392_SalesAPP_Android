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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import com.google.android.material.textview.MaterialTextView;
import com.prm392.salesapp.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private TextView usernameTextView;
    private TextView emailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        usernameTextView = view.findViewById(R.id.username_textview);
        emailTextView = view.findViewById(R.id.email_textview);
        Button logoutButton = view.findViewById(R.id.logout_button);
        MaterialTextView editProfileButton = view.findViewById(R.id.edit_profile_button);
    MaterialTextView orderHistoryButton = view.findViewById(R.id.order_history_button);

        // Initialize ViewModel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Setup Listeners
        logoutButton.setOnClickListener(v -> logoutUser());
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        orderHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
            startActivity(intent);
        });

        // Observe ViewModel LiveData
        observeViewModel();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Re-fetch profile when the screen is shown
        profileViewModel.fetchProfile();
    }

    private void observeViewModel() {
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                usernameTextView.setText(userProfile.getUsername());
                emailTextView.setText(userProfile.getEmail());
            }
        });

        profileViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
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
    }
}
