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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditEquipmentActivity extends AppCompatActivity {

    private EditText equipmentName;
    private EditText equipmentModel;
    private EditText equipmentQR;
    private AutoCompleteTextView actLab;
    private Button editEquipment;
    private Button deleteEquipment;
    private ArrayList<String> labNames;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_equipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        String qrId = getIntent().getStringExtra("QR_ID");
        String model = getIntent().getStringExtra("EQUIPMENT_MODEL");
        String name = getIntent().getStringExtra("EQUIPMENT_NAME");
        String laboratory = getIntent().getStringExtra("LABORATORY");
        String state = getIntent().getStringExtra("STATE");

        equipmentQR = findViewById(R.id.qrId);
        equipmentName = findViewById(R.id.equipmentName);
        equipmentModel = findViewById(R.id.equipmentModel);
        actLab = findViewById(R.id.actLab);
        editEquipment = findViewById(R.id.btn_edit_equipment);
        deleteEquipment = findViewById(R.id.btn_delete_equipment);

        equipmentName.setText(name);
        equipmentModel.setText(model);
        actLab.setText(laboratory);
        equipmentQR.setText(qrId);

        labNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, labNames);
        actLab.setAdapter(adapter);

        AutoCompleteTextView equipmentLab;
        equipmentLab = actLab;

        AutoCompleteTextView stateDropdown = findViewById(R.id.actState);
        String[] states = getResources().getStringArray(R.array.equipment_states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, states);
        stateDropdown.setAdapter(stateAdapter);
        stateDropdown.setText(state, false);

        equipmentLab.setOnItemClickListener((parent, view, position, id) -> {
            equipmentLab.clearFocus();
        });

        stateDropdown.setOnItemClickListener((parent, view, position, id) -> {
            stateDropdown.clearFocus();
        });

        editEquipment.setOnClickListener(v -> {

            String updateLab = equipmentLab.getText().toString().trim();
            String updateState = stateDropdown.getText().toString().trim();

            if (updateLab.isEmpty() || updateState.isEmpty()){
                Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_LONG).show();
                return;
            }

            db.collection("equipment")
                    .whereEqualTo("qrId", qrId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()){

                            String docId = snapshot.getDocuments().get(0).getId();

                            db.collection("equipment")
                                    .document(docId)
                                    .update("lab", updateLab, "state", updateState)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getApplicationContext(), "Equipment Successfully Updated", Toast.LENGTH_LONG).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Equipment not found", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        });

        deleteEquipment.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(EditEquipmentActivity.this)
                    .setTitle("Confirm Delete")
                    .setMessage("Do you want to delete this equipment?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", (dialog, which) -> {

                        db.collection("equipment")
                                .whereEqualTo("qrId", qrId)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.isEmpty()){

                                        String docId = snapshot.getDocuments().get(0).getId();

                                        db.collection("equipment")
                                                .document(docId)
                                                .update("state", "Removed")
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(getApplicationContext(), "Equipment Successfully Deleted", Toast.LENGTH_LONG).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Equipment not found", Toast.LENGTH_LONG).show();
                                    }
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

        loadLabs();
    }

    public void loadLabs(){

        db.collection("labs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    labNames.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){
                        String labName = doc.getString("labName");

                        if (labName != null){
                            labNames.add(labName);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}