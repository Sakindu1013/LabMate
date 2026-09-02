package com.example.labmate.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    private EditText registerName;
    private EditText registerPassword;
    private EditText confirmPassword;
    private EditText registerEmail;
    private EditText registerMobile;
    private EditText registerDOB;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerViewModel = new ViewModelProvider(this)
                .get(RegisterViewModel.class);

        initializeViews();
        setupListeners();
        setupDatePicker();
        observeViewModel();
    }

    private void initializeViews() {

        registerName = findViewById(R.id.registerName);
        registerPassword = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerEmail = findViewById(R.id.registerEmail);
        registerMobile = findViewById(R.id.registerMobile);
        registerDOB = findViewById(R.id.registerDOB);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setupListeners() {

        Button registerButton = findViewById(R.id.btn_register);

        registerButton.setOnClickListener(
                v -> performRegistration()
        );

        Button clearButton = findViewById(R.id.btn_clear);

        clearButton.setOnClickListener(
                v -> showClearFormDialog()
        );
    }

    private void performRegistration() {

        String name = registerName.getText()
                .toString()
                .trim()
                .replaceAll("\\s+", " ");

        String password = registerPassword.getText()
                .toString()
                .trim();

        String confirmPasswordText = confirmPassword.getText()
                .toString()
                .trim();

        String email = registerEmail.getText()
                .toString()
                .trim();

        String mobile = registerMobile.getText()
                .toString()
                .trim();

        String dob = registerDOB.getText()
                .toString()
                .trim();

        RegisterRequest request = new RegisterRequest(
                name,
                email,
                password,
                mobile,
                dob
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

    private void handleRegisterState(RegisterState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:

                loadingOverlay.setVisibility(View.VISIBLE);

                break;

            case SUCCESS:

                loadingOverlay.setVisibility(View.GONE);

                handleRegistrationSuccess(
                        state.isVerificationEmailSent()
                );

                break;

            case ERROR:

                loadingOverlay.setVisibility(View.GONE);

                showError(state.getMessage());

                break;

            case IDLE:
            default:
                break;
        }
    }

    private void handleRegistrationSuccess(boolean verificationEmailSent) {

        if (verificationEmailSent) {

            Toast.makeText(
                    this,
                    "Registration successful!\n" +
                            "Please verify your email before logging in.\n" +
                            "Check your Spam/Junk folder if you don't see it.",
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

        Intent intent = new Intent(
                RegisterActivity.this,
                LoginActivity.class
        );

        startActivity(intent);
        finish();
    }

    private void showError(String message) {

        if (message != null && !message.isEmpty()) {

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void showClearFormDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear Form")
                .setMessage("Do you want to clear the form?")
                .setCancelable(false)
                .setPositiveButton(
                        "Clear",
                        (dialog, which) -> clearForm()
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss()
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
    }

    private void setupDatePicker() {

        registerDOB.setOnClickListener(
                v -> showDatePicker()
        );
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            String date = String.format(
                                    "%02d/%02d/%04d",
                                    dayOfMonth,
                                    month + 1,
                                    year
                            );

                            registerDOB.setText(date);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.getDatePicker()
                .setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}