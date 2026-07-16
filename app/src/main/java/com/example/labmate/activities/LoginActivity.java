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

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button_google = (Button) findViewById(R.id.google_button);
        button_google.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_LONG).show();
        });

        mAuth = FirebaseAuth.getInstance();

        EditText username = findViewById(R.id.loginEmail);
        EditText password = findViewById(R.id.password);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(v -> {

            EditText resetEmail = new EditText(LoginActivity.this);

            resetEmail.setHint("Enter Your Email");
            resetEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            resetEmail.setPadding(50,20,50,20);

            new MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle("Reset Password")
                    .setMessage("Enter your email address and we will send you a password reset link.")
                    .setView(resetEmail)
                    .setPositiveButton("Send", (dialog, which) -> {
                        String email = resetEmail.getText().toString().trim();

                        if (email.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Enter Your Email Address", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Password Reset Email Sent", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();


        });

        Button button_login = findViewById(R.id.btn_login);
        button_login.setOnClickListener(v -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()){

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (currentUser != null && currentUser.isEmailVerified()){

                                username.setText("");
                                password.setText("");

                                Intent loginIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                Toast.makeText(getApplicationContext(), "Successfully Logged In", Toast.LENGTH_LONG).show();
                                startActivity(loginIntent);

                            } else {
                                new MaterialAlertDialogBuilder(LoginActivity.this)
                                        .setTitle("Email Verification Required")
                                        .setMessage("Your email address has not been verified.\n\nWould you like us to send another verification email?")
                                        .setCancelable(false)
                                        .setPositiveButton("Resend", (dialog, which) -> {

                                            if (currentUser != null){
                                                currentUser.sendEmailVerification()
                                                        .addOnSuccessListener(unused -> {
                                                            Toast.makeText(getApplicationContext(), "Verificaion Email Sent", Toast.LENGTH_LONG).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        });
                                            }
                                        })
                                        .setNegativeButton("Cancel", (dialog, which) -> {
                                            FirebaseAuth.getInstance().signOut();
                                            dialog.dismiss();
                                        })
                                        .show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username or Password is incorrect", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        Button button_register = findViewById(R.id.btn_register);
        button_register.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        ScrollView loginView = findViewById(R.id.main);
        loginView.setOnClickListener(v -> {
            View focusedView = getCurrentFocus();

            loginView.clearFocus();

            if (focusedView != null){
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }

        });
    }
}