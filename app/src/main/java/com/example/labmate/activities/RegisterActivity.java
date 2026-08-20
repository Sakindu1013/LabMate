package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.models.RegisterRequest;
import com.example.labmate.states.RegisterState;
import com.example.labmate.viewmodels.RegisterViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    private EditText registerName;
    private EditText registerPassword;
    private EditText confirmPassword;
    private EditText registerEmail;
    private EditText registerMobile;
    private EditText registerDOB;

    private AutoCompleteTextView roleDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

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

        registerViewModel =
                new ViewModelProvider(this)
                        .get(RegisterViewModel.class);

        initializeViews();
        setupRoleDropdown();
        setupListeners();
        observeViewModel();
    }

    private void initializeViews() {

        registerName =
                findViewById(R.id.registerName);

        registerPassword =
                findViewById(R.id.registerPassword);

        confirmPassword =
                findViewById(R.id.confirmPassword);

        registerEmail =
                findViewById(R.id.registerEmail);

        registerMobile =
                findViewById(R.id.registerMobile);

        registerDOB =
                findViewById(R.id.registerDOB);

        roleDropdown =
                findViewById(R.id.actRole);
    }

    private void setupRoleDropdown() {

        String[] roles =
                getResources()
                        .getStringArray(R.array.user_roles);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        com.google.android.material.R.layout
                                .mtrl_auto_complete_simple_item,
                        roles
                );

        roleDropdown.setAdapter(adapter);

        roleDropdown.setOnItemClickListener(
                (parent, view, position, id) ->
                        roleDropdown.clearFocus()
        );
    }

    private void setupListeners() {

        Button registerButton =
                findViewById(R.id.btn_register);

        registerButton.setOnClickListener(
                v -> performRegistration()
        );

        Button clearButton =
                findViewById(R.id.btn_clear);

        clearButton.setOnClickListener(
                v -> showClearFormDialog()
        );
    }

    private void performRegistration() {

        String name =
                registerName.getText()
                        .toString()
                        .trim()
                        .replaceAll("\\s+", " ");

        String password =
                registerPassword.getText()
                        .toString()
                        .trim();

        String confirmPasswordText =
                confirmPassword.getText()
                        .toString()
                        .trim();

        String email =
                registerEmail.getText()
                        .toString()
                        .trim();

        String mobile =
                registerMobile.getText()
                        .toString()
                        .trim();

        String dob =
                registerDOB.getText()
                        .toString()
                        .trim();

        String role =
                roleDropdown.getText()
                        .toString()
                        .trim();

        RegisterRequest request =
                new RegisterRequest(
                        name,
                        email,
                        password,
                        mobile,
                        dob,
                        role
                );

        registerViewModel.register(
                request,
                confirmPasswordText
        );
    }

    private void observeViewModel() {

        registerViewModel.getRegisterState()
                .observe(
                        this,
                        this::handleRegisterState
                );
    }

    private void handleRegisterState(
            RegisterState state
    ) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:

                // We can add a ProgressBar here later.
                break;

            case SUCCESS:

                handleRegistrationSuccess(
                        state.isVerificationEmailSent()
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

    private void handleRegistrationSuccess(
            boolean verificationEmailSent
    ) {

        if (verificationEmailSent) {

            Toast.makeText(
                    this,
                    "Registration successful!\n" +
                            "Please verify your email before logging in.",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Registration successful, but the " +
                            "verification email could not be sent.",
                    Toast.LENGTH_LONG
            ).show();
        }

        clearForm();

        registerViewModel.logout();

        Intent intent =
                new Intent(
                        RegisterActivity.this,
                        LoginActivity.class
                );

        startActivity(intent);
        finish();
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

    private void showClearFormDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear Form")
                .setMessage(
                        "Do you want to clear the form?"
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Clear",
                        (dialog, which) ->
                                clearForm()
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                dialog.dismiss()
                )
                .show();
    }

    private void clearForm() {

        registerName.setText("");
        registerPassword.setText("");
        confirmPassword.setText("");
        registerEmail.setText("");
        registerMobile.setText("");
        registerDOB.setText("");
        roleDropdown.setText(null);
    }
}