package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import com.example.labmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AutoCompleteTextView roleDropdown = findViewById(R.id.actRole);
        String[] roles = getResources().getStringArray(R.array.user_roles);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, roles);
        roleDropdown.setAdapter(adapter);

        roleDropdown.setOnItemClickListener((parent, view, position, id) -> {
            roleDropdown.clearFocus();
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText registerName, registerPassword, confirmPassword, registerEmail, registerMobile, registerDOB;
        registerName = findViewById(R.id.registerName);
        registerPassword = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerEmail = findViewById(R.id.registerEmail);
        registerMobile = findViewById(R.id.registerMobile);
        registerDOB = findViewById(R.id.registerDOB);

        Button button_register = (Button) findViewById(R.id.btn_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = registerName.getText().toString().trim().replaceAll("\\s+", " ");
                String password = registerPassword.getText().toString().trim();
                String reEnterPassword = confirmPassword.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String mobile = registerMobile.getText().toString().trim();
                String DOB = registerDOB.getText().toString().trim();
                String selectedRole = roleDropdown.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || reEnterPassword.isEmpty() || mobile.isEmpty() || DOB.isEmpty() || selectedRole.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(reEnterPassword)){
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!mobile.matches("07[0-9]{8}")) {
                    Toast.makeText(getApplicationContext(), "Enter valid SL mobile number (07XXXXXXXX)", Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);

                try {
                    sdf.parse(DOB);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Enter a valid DOB (dd/mm/yyyy)", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getApplicationContext(), "Enter a valid email address", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()){

                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                if (currentUser != null) {
                                    currentUser.sendEmailVerification()
                                            .addOnCompleteListener(emailTask -> {
                                                if(emailTask.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "Verification email sent.", Toast.LENGTH_SHORT).show();
                                                }
                                                if (!emailTask.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "Filed to send Verification Email", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                String uid = mAuth.getCurrentUser().getUid();
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);
                                user.put("mobile", mobile);
                                user.put("dob", DOB);
                                user.put("createdAt", System.currentTimeMillis());
                                user.put("role", selectedRole);

                                db.collection("users").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(getApplicationContext(), "Registration successful!\nPlease verify your email before logging in.", Toast.LENGTH_LONG).show();

                                            registerName.setText("");
                                            registerPassword.setText("");
                                            confirmPassword.setText("");
                                            registerEmail.setText("");
                                            registerMobile.setText("");
                                            registerDOB.setText("");
                                            roleDropdown.setText(null);

                                            FirebaseAuth.getInstance().signOut();

                                            Intent redirectIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(redirectIntent);
                                            finish();

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        Button btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(RegisterActivity.this)
                        .setTitle("Clear Form")
                        .setMessage("Do you want to clear the form?")
                        .setCancelable(false)
                        .setPositiveButton("Clear", (dialog, which) -> {

                            registerName.setText("");
                            registerPassword.setText("");
                            confirmPassword.setText("");
                            registerEmail.setText("");
                            registerMobile.setText("");
                            registerDOB.setText("");
                            roleDropdown.setText(null);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }
}