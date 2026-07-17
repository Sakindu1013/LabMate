package com.example.labmate.activities;

import android.content.SharedPreferences;
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

import com.example.labmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddLabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_lab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("UserPrefs", 0);
        String role = prefs.getString("role", "");
        String username = prefs.getString("name","");

        AutoCompleteTextView locationDropdown = findViewById(R.id.actLocation);
        String[] roles = getResources().getStringArray(R.array.lab_locations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, roles);
        locationDropdown.setAdapter(adapter);

        locationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            locationDropdown.clearFocus();
        });

        EditText labName, personInCharge;
        labName = findViewById(R.id.labName);
        personInCharge = findViewById(R.id.personInCharge);

        Button btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(AddLabActivity.this)
                        .setTitle("Clear Form")
                        .setMessage("Do you want to clear the form?")
                        .setCancelable(false)
                        .setPositiveButton("Clear", (dialog, which) -> {

                            labName.setText("");
                            personInCharge.setText("");
                            locationDropdown.setText(null);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button button_add_lab = (Button) findViewById(R.id.btn_add_lab);
        button_add_lab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = labName.getText().toString().trim().replaceAll("\\s+", " ");
                String inCharge = personInCharge.getText().toString().trim().replaceAll("\\s+", " ");
                String location = locationDropdown.getText().toString();

                if (name.isEmpty() || inCharge.isEmpty() || location.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> lab = new HashMap<>();
                lab.put("labName", name);
                lab.put("personInCharge", inCharge);
                lab.put("location", location);
                lab.put("createdAt", System.currentTimeMillis());
                lab.put("createdBy", username);
                lab.put("createdByRole", role);

                db.collection("labs")
                        .add(lab)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getApplicationContext(), "Laboratory Added Successfully", Toast.LENGTH_LONG).show();

                            labName.setText("");
                            personInCharge.setText("");
                            locationDropdown.setText(null);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            }
        });
    }
}