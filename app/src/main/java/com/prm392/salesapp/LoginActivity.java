package com.prm392.salesapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.prm392.salesapp.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private LoginViewModel loginViewModel;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupLink = findViewById(R.id.signup_link);
        rootLayout = findViewById(R.id.root_layout_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (usernameInput.isFocused() || passwordInput.isFocused()) {
                    usernameInput.clearFocus();
                    passwordInput.clearFocus();
                }
            }
            return false;
        });

        loginViewModel.getLoginResponse().observe(this, loginResponse -> {
            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

            // Save the auth token
            SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("AUTH_TOKEN", loginResponse.getToken());
            editor.apply();

            // Fetch the user profile
            loginViewModel.getProfile(loginResponse.getToken());
        });

        loginViewModel.getUserProfile().observe(this, userProfile -> {
            // Save user details
            SharedPreferences sharedPreferences = getSharedPreferences("SalesAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("USER_ID", userProfile.getUserId());
            editor.putBoolean("IS_ADMIN", "admin".equals(userProfile.getRole()));
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        loginViewModel.getLoginError().observe(this, error -> {
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
        });

        loginButton.setOnClickListener(v -> loginUser());

        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        loginViewModel.login(username, password);
    }
}
