package com.prm392.salesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm392.salesapp.viewmodel.EditProfileViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private EditProfileViewModel viewModel;
    private TextInputLayout emailLayout, phoneLayout, addressLayout;
    private TextInputEditText emailInput, phoneInput, addressInput;
    private Button saveButton;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        toolbar = findViewById(R.id.toolbar_edit_profile);
        emailLayout = findViewById(R.id.email_input_layout_edit);
        phoneLayout = findViewById(R.id.phone_input_layout_edit);
        addressLayout = findViewById(R.id.address_input_layout_edit);
        emailInput = findViewById(R.id.email_input_edit);
        phoneInput = findViewById(R.id.phone_input_edit);
        addressInput = findViewById(R.id.address_input_edit);
        saveButton = findViewById(R.id.save_profile_button);
        progressBar = findViewById(R.id.progress_bar_edit_profile);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        // Observe LiveData
        observeViewModel();

        // Setup Listeners
        toolbar.setNavigationOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveProfileChanges());

        // Fetch initial data
        progressBar.setVisibility(View.VISIBLE);
        viewModel.fetchProfile();
    }

    private void observeViewModel() {
        viewModel.getUserProfile().observe(this, userProfile -> {
            if (userProfile != null) {
                progressBar.setVisibility(View.GONE);
                emailLayout.setHint(userProfile.getEmail());
                phoneLayout.setHint(userProfile.getPhoneNumber());
                addressLayout.setHint(userProfile.getAddress());
            }
        });

        viewModel.getUpdateResult().observe(this, updateProfileResponse -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the profile screen
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProfileChanges() {
        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String address = addressInput.getText().toString();

        // Create the request object, only including fields the user has changed
        UpdateProfileRequest request = new UpdateProfileRequest(
            email.isEmpty() ? null : email,
            phone.isEmpty() ? null : phone,
            address.isEmpty() ? null : address,
            null, // currentPassword
            null  // newPassword
        );

        progressBar.setVisibility(View.VISIBLE);
        viewModel.updateProfile(request);
    }
}
