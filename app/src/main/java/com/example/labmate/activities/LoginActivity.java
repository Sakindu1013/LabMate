package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.models.LoginRequest;
import com.example.labmate.states.LoginState;
import com.example.labmate.viewmodels.LoginViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        loginViewModel =
                new ViewModelProvider(this)
                        .get(LoginViewModel.class);

        initializeViews();
        setupListeners();
        observeViewModel();
    }

    private void initializeViews() {

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.password);
    }

    private void setupListeners() {

        Button googleButton = findViewById(R.id.google_button);

        googleButton.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Under Construction",
                        Toast.LENGTH_LONG
                ).show()
        );

        TextView forgotPassword =
                findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(v ->
                showPasswordResetDialog()
        );

        Button loginButton =
                findViewById(R.id.btn_login);

        loginButton.setOnClickListener(v ->
                performLogin()
        );

        Button registerButton =
                findViewById(R.id.btn_register);

        registerButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    );

            startActivity(intent);
        });

        setupKeyboardDismissListener();
    }

    private void performLogin() {

        String emailAddress =
                email.getText()
                        .toString()
                        .trim();

        String passwordText =
                password.getText()
                        .toString();

        if (emailAddress.isEmpty()
                || passwordText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all details",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        LoginRequest request =
                new LoginRequest(
                        emailAddress,
                        passwordText
                );

        loginViewModel.login(request);
    }

    private void showPasswordResetDialog() {

        EditText resetEmail =
                new EditText(this);

        resetEmail.setHint("Enter Your Email");
        resetEmail.setInputType(
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        resetEmail.setPadding(
                50,
                20,
                50,
                20
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle("Reset Password")
                .setMessage(
                        "Enter your email address and we will " +
                                "send you a password reset link."
                )
                .setView(resetEmail)
                .setPositiveButton(
                        "Send",
                        (dialog, which) -> {

                            String emailAddress =
                                    resetEmail.getText()
                                            .toString()
                                            .trim();

                            if (emailAddress.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Enter Your Email Address",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            loginViewModel.resetPassword(
                                    emailAddress
                            );
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                dialog.dismiss()
                )
                .show();
    }

    private void observeViewModel() {

        loginViewModel.getLoginState()
                .observe(this, this::handleLoginState);

        loginViewModel.getPasswordResetState()
                .observe(
                        this,
                        this::handlePasswordResetState
                );

        loginViewModel.getVerificationEmailState()
                .observe(
                        this,
                        this::handleVerificationEmailState
                );
    }

    private void handleLoginState(LoginState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:

                // We can add a ProgressBar here later.
                break;

            case SUCCESS:

                handleLoginSuccess();
                break;

            case EMAIL_NOT_VERIFIED:

                showEmailVerificationDialog(
                        state.getUser()
                );

                break;

            case ERROR:

                showError(state.getMessage());
                break;

            case IDLE:
            default:
                break;
        }
    }

    private void handleLoginSuccess() {

        email.setText("");
        password.setText("");

        Toast.makeText(
                this,
                "Successfully Logged In",
                Toast.LENGTH_LONG
        ).show();

        Intent intent =
                new Intent(
                        LoginActivity.this,
                        DashboardActivity.class
                );

        startActivity(intent);
        finish();
    }

    private void showEmailVerificationDialog(
            com.google.firebase.auth.FirebaseUser user
    ) {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Email Verification Required")
                .setMessage(
                        "Your email address has not been verified.\n\n" +
                                "Would you like us to send another " +
                                "verification email?"
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Resend",
                        (dialog, which) ->
                                loginViewModel
                                        .resendVerificationEmail(user)
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                loginViewModel.logout()
                )
                .show();
    }

    private void handlePasswordResetState(
            LoginState state
    ) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case SUCCESS:

                Toast.makeText(
                        this,
                        "Password Reset Email Sent",
                        Toast.LENGTH_LONG
                ).show();

                break;

            case ERROR:

                showError(state.getMessage());
                break;

            case LOADING:
            case IDLE:
            case EMAIL_NOT_VERIFIED:
            default:
                break;
        }
    }

    private void handleVerificationEmailState(
            LoginState state
    ) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case SUCCESS:

                Toast.makeText(
                        this,
                        "Verification email sent successfully.",
                        Toast.LENGTH_LONG
                ).show();

                break;

            case ERROR:

                showError(state.getMessage());
                break;

            case LOADING:
            case IDLE:
            case EMAIL_NOT_VERIFIED:
            default:
                break;
        }
    }

    private void showError(String message) {

        if (message != null && !message.isEmpty()) {

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void setupKeyboardDismissListener() {

        ScrollView loginView =
                findViewById(R.id.main);

        loginView.setOnClickListener(v -> {

            View focusedView =
                    getCurrentFocus();

            loginView.clearFocus();

            if (focusedView != null) {

                InputMethodManager imm =
                        (InputMethodManager)
                                getSystemService(
                                        INPUT_METHOD_SERVICE
                                );

                imm.hideSoftInputFromWindow(
                        focusedView.getWindowToken(),
                        0
                );
            }
        });
    }
}