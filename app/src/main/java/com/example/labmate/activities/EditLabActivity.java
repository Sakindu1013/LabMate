package com.example.labmate.activities;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditLabActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editInCharge;
    private AutoCompleteTextView editLocation;
    private Button editLab;
    private Button deleteLab;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_lab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        String labId = getIntent().getStringExtra("LAB_ID");
        String labName = getIntent().getStringExtra("LAB_NAME");
        String labInCharge = getIntent().getStringExtra("LAB_IN_CHARGE");
        String labLocation = getIntent().getStringExtra("LAB_LOCATION");

        AutoCompleteTextView locationDropdown = findViewById(R.id.actLocation);
        String[] roles = getResources().getStringArray(R.array.lab_locations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, roles);
        locationDropdown.setAdapter(adapter);
        locationDropdown.setText(labLocation, false);

        locationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            locationDropdown.clearFocus();
        });

        editName = findViewById(R.id.labName);
        editInCharge = findViewById(R.id.personInCharge);

        editName.setText(labName);
        editInCharge.setText(labInCharge);
        editLocation = locationDropdown;

        editLab = findViewById(R.id.btn_edit_lab);
        deleteLab = findViewById(R.id.btn_delete_lab);

        editLab.setOnClickListener(v -> {

            String updateName = editName.getText().toString().trim();
            String updateInCharge = editInCharge.getText().toString().trim();
            String updateLocation = editLocation.getText().toString().trim();

            if (updateName.isEmpty() || updateInCharge.isEmpty() || updateLocation.isEmpty()){
                Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (labId == null) {
                Toast.makeText(getApplicationContext(), "Lab ID missing", Toast.LENGTH_LONG).show();
                return;
            }

            DocumentReference labRef = db.collection("labs").document(labId);

            labRef.update("labName", updateName, "personInCharge", updateInCharge, "location", updateLocation)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Lab Updated Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        });

        deleteLab.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(EditLabActivity.this)
                    .setTitle("Confirm Delete")
                    .setMessage("Do you want to delete this laboratory?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", (dialog, which) -> {

                        if (labId == null) {
                            Toast.makeText(getApplicationContext(), "Lab ID missing", Toast.LENGTH_LONG).show();
                            return;
                        }

                        db.collection("labs")
                                .document(labId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getApplicationContext(), "Lab Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }
}