package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.models.LoginRequest;
import com.example.labmate.states.LoginState;
import com.example.labmate.viewmodels.LoginViewModel;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private CredentialManager credentialManager;
    private EditText email;
    private EditText password;
    private FrameLayout loadingOverlay;
    private ProgressBar loginProgressBar;

    private Button loginButton;
    private Button googleButton;
    private Button registerButton;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        credentialManager = CredentialManager.create(this);

        initializeViews();
        setupListeners();
        observeViewModel();
    }

    private void initializeViews() {

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.password);

        loadingOverlay = findViewById(R.id.loadingOverlay);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        loginButton = findViewById(R.id.btn_login);
        googleButton = findViewById(R.id.google_button);
        registerButton = findViewById(R.id.btn_register);
        forgotPassword = findViewById(R.id.forgotPassword);
    }

    private void setupListeners() {

        googleButton.setOnClickListener(v -> signInWithGoogle());

        forgotPassword.setOnClickListener(v -> showPasswordResetDialog());

        loginButton.setOnClickListener(v -> performLogin());

        registerButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        setupKeyboardDismissListener();
    }

    private void signInWithGoogle() {

        String webClientId = getString(R.string.default_web_client_id);

        GetSignInWithGoogleOption googleOption =
                new GetSignInWithGoogleOption.Builder(webClientId)
                        .build();

        GetCredentialRequest request =
                new GetCredentialRequest.Builder()
                        .addCredentialOption(googleOption)
                        .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                getMainExecutor(),
                new androidx.credentials.CredentialManagerCallback<
                        GetCredentialResponse,
                        GetCredentialException
                        >() {

                    @Override
                    public void onResult(@NonNull GetCredentialResponse result) {

                        handleGoogleCredential(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {

                        runOnUiThread(() ->
                                showError("Google Sign-In failed: " + e.getMessage())
                        );
                    }
                }
        );
    }

    private void handleGoogleCredential(GetCredentialResponse result) {

        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {

            CustomCredential customCredential =
                    (CustomCredential) credential;

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    .equals(credential.getType())) {

                GoogleIdTokenCredential googleCredential =
                        GoogleIdTokenCredential.createFrom(
                                customCredential.getData()
                        );

                String idToken = googleCredential.getIdToken();

                runOnUiThread(() ->
                        loginViewModel.loginWithGoogle(idToken)
                );

            } else {

                runOnUiThread(() ->
                        showError("Unexpected Google credential.")
                );
            }

        } else {

            runOnUiThread(() ->
                    showError("Unexpected credential type.")
            );
        }
    }

    private void performLogin() {

        String emailAddress = email.getText()
                .toString()
                .trim();

        String passwordText = password.getText()
                .toString();

        if (emailAddress.isEmpty() || passwordText.isEmpty()) {

            Toast.makeText(this, "Fill all details", Toast.LENGTH_LONG).show();

            return;
        }

        LoginRequest request = new LoginRequest(
                emailAddress,
                passwordText
        );

        loginViewModel.login(request);
    }

    private void showPasswordResetDialog() {

        EditText resetEmail = new EditText(this);

        resetEmail.setHint("Enter Your Email");
        resetEmail.setInputType(
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        resetEmail.setPadding(50, 20, 50, 20);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Reset Password")
                .setMessage(
                        "Enter your email address and we will " +
                                "send you a password reset link."
                )
                .setView(resetEmail)
                .setPositiveButton("Send", (dialog, which) -> {

                    String emailAddress = resetEmail.getText()
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

                    loginViewModel.resetPassword(emailAddress);
                })
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss()
                )
                .show();
    }

    private void observeViewModel() {

        loginViewModel.getLoginState()
                .observe(this, this::handleLoginState);

        loginViewModel.getPasswordResetState()
                .observe(this, this::handlePasswordResetState);

        loginViewModel.getVerificationEmailState()
                .observe(this, this::handleVerificationEmailState);
    }

    private void handleLoginState(LoginState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:
                showLoading();
                break;

            case SUCCESS:
                hideLoading();
                handleLoginSuccess();
                break;

            case EMAIL_NOT_VERIFIED:
                hideLoading();
                showEmailVerificationDialog(state.getUser());
                break;

            case ERROR:
                hideLoading();
                showError(state.getMessage());
                break;

            case IDLE:
            default:
                hideLoading();
                break;
        }
    }

    private void showLoading() {

        loadingOverlay.setVisibility(View.VISIBLE);

        loginButton.setEnabled(false);
        googleButton.setEnabled(false);
        registerButton.setEnabled(false);
        forgotPassword.setEnabled(false);
    }

    private void hideLoading() {

        loadingOverlay.setVisibility(View.GONE);

        loginButton.setEnabled(true);
        googleButton.setEnabled(true);
        registerButton.setEnabled(true);
        forgotPassword.setEnabled(true);
    }

    private void handleLoginSuccess() {

        email.setText("");
        password.setText("");

        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(
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
                                loginViewModel.resendVerificationEmail(user)
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                loginViewModel.logout()
                )
                .show();
    }

    private void handlePasswordResetState(LoginState state) {

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

    private void handleVerificationEmailState(LoginState state) {

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

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void setupKeyboardDismissListener() {

        View loginView = findViewById(R.id.main);

        loginView.setOnClickListener(v -> {

            View focusedView = getCurrentFocus();

            loginView.clearFocus();

            if (focusedView != null) {

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(
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