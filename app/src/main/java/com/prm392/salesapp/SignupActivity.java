package com.prm392.salesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.prm392.salesapp.viewmodel.SignupViewModel;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, emailInput, passwordInput, confirmPasswordInput, phoneInput, addressInput;
    private SignupViewModel signupViewModel;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameInput = findViewById(R.id.username_input_signup);
        emailInput = findViewById(R.id.email_input_signup);
        passwordInput = findViewById(R.id.password_input_signup);
        confirmPasswordInput = findViewById(R.id.confirm_password_input_signup);
        phoneInput = findViewById(R.id.phone_input_signup);
        addressInput = findViewById(R.id.address_input_signup);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginLink = findViewById(R.id.login_link);
        rootLayout = findViewById(R.id.root_layout_signup);

        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (usernameInput.isFocused() || emailInput.isFocused() || passwordInput.isFocused() || confirmPasswordInput.isFocused() || phoneInput.isFocused() || addressInput.isFocused()) {
                    usernameInput.clearFocus();
                    emailInput.clearFocus();
                    passwordInput.clearFocus();
                    confirmPasswordInput.clearFocus();
                    phoneInput.clearFocus();
                    addressInput.clearFocus();
                }
            }
            return false;
        });

        signupViewModel.getSignupResponse().observe(this, signupResponse -> {
            Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signupViewModel.getSignupError().observe(this, error -> {
            Toast.makeText(SignupActivity.this, "Signup failed: " + error, Toast.LENGTH_SHORT).show();
        });

        signupButton.setOnClickListener(v -> signupUser());

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void signupUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        signupViewModel.signup(username, password, email, phone, address, "customer");
    }
}