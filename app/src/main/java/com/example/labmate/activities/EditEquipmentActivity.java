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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditEquipmentActivity extends AppCompatActivity {

    private EditText equipmentName;
    private EditText equipmentModel;
    private EditText equipmentQR;

    private AutoCompleteTextView actLab;
    private AutoCompleteTextView stateDropdown;

    private Button editEquipment;
    private Button deleteEquipment;

    private ArrayList<String> labNames;
    private ArrayAdapter<String> labAdapter;

    private FirebaseFirestore db;

    private String qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_equipment);

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

        db = FirebaseFirestore.getInstance();

        // Get existing equipment data
        qrId = getIntent().getStringExtra("QR_ID");

        String model =
                getIntent().getStringExtra(
                        "EQUIPMENT_MODEL"
                );

        String name =
                getIntent().getStringExtra(
                        "EQUIPMENT_NAME"
                );

        String laboratory =
                getIntent().getStringExtra(
                        "LABORATORY"
                );

        String state =
                getIntent().getStringExtra(
                        "STATE"
                );

        initializeViews();

        // Display existing values
        equipmentQR.setText(qrId);
        equipmentName.setText(name);
        equipmentModel.setText(model);
        actLab.setText(laboratory, false);
        stateDropdown.setText(state, false);

        setupLabDropdown();
        setupStateDropdown();
        setupButtons();

        loadLabs();
    }

    private void initializeViews() {

        equipmentQR = findViewById(R.id.qrId);

        equipmentName =
                findViewById(R.id.equipmentName);

        equipmentModel =
                findViewById(R.id.equipmentModel);

        actLab =
                findViewById(R.id.actLab);

        stateDropdown =
                findViewById(R.id.actState);

        editEquipment =
                findViewById(R.id.btn_edit_equipment);

        deleteEquipment =
                findViewById(R.id.btn_delete_equipment);
    }

    private void setupLabDropdown() {

        labNames = new ArrayList<>();

        labAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                labNames
        );

        actLab.setAdapter(labAdapter);

        actLab.setOnItemClickListener(
                (parent, view, position, id) ->
                        actLab.clearFocus()
        );
    }

    private void setupStateDropdown() {

        String[] states =
                getResources().getStringArray(
                        R.array.equipment_states
                );

        ArrayAdapter<String> stateAdapter =
                new ArrayAdapter<>(
                        this,
                        com.google.android.material.R.layout
                                .mtrl_auto_complete_simple_item,
                        states
                );

        stateDropdown.setAdapter(stateAdapter);

        stateDropdown.setOnItemClickListener(
                (parent, view, position, id) ->
                        stateDropdown.clearFocus()
        );
    }

    private void setupButtons() {

        editEquipment.setOnClickListener(
                v -> updateEquipment()
        );

        deleteEquipment.setOnClickListener(
                v -> confirmDelete()
        );
    }

    private void updateEquipment() {

        String name =
                equipmentName
                        .getText()
                        .toString()
                        .trim()
                        .replaceAll("\\s+", " ");

        String model =
                equipmentModel
                        .getText()
                        .toString()
                        .trim()
                        .replaceAll("\\s+", " ");

        String lab =
                actLab
                        .getText()
                        .toString()
                        .trim();

        String state =
                stateDropdown
                        .getText()
                        .toString()
                        .trim();

        if (name.isEmpty()
                || model.isEmpty()
                || lab.isEmpty()
                || state.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all details",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (qrId == null || qrId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Equipment ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        db.collection("equipment")
                .whereEqualTo("qrId", qrId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Equipment not found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    DocumentSnapshot document =
                            snapshot.getDocuments().get(0);

                    String documentId =
                            document.getId();

                    db.collection("equipment")
                            .document(documentId)
                            .update(
                                    "equipmentName",
                                    name,
                                    "equipmentModel",
                                    model,
                                    "lab",
                                    lab,
                                    "state",
                                    state
                            )
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Equipment Successfully Updated",
                                        Toast.LENGTH_LONG
                                ).show();

                                finish();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void confirmDelete() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Delete")
                .setMessage(
                        "Do you want to remove this equipment?"
                )
                .setCancelable(false)

                .setPositiveButton(
                        "Remove",
                        (dialog, which) ->
                                removeEquipment()
                )

                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                dialog.dismiss()
                )

                .show();
    }

    private void removeEquipment() {

        if (qrId == null || qrId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Equipment ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        db.collection("equipment")
                .whereEqualTo("qrId", qrId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Equipment not found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    DocumentSnapshot document =
                            snapshot.getDocuments().get(0);

                    String documentId =
                            document.getId();

                    String state =
                            document.getString("state");

                    // Don't remove borrowed equipment.
                    if ("Borrowed".equals(state)) {

                        Toast.makeText(
                                this,
                                "Borrowed equipment cannot be removed.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Already removed
                    if ("Removed".equals(state)) {

                        Toast.makeText(
                                this,
                                "This equipment is already removed.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    db.collection("equipment")
                            .document(documentId)
                            .update(
                                    "state",
                                    "Removed"
                            )
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Equipment Successfully Removed",
                                        Toast.LENGTH_LONG
                                ).show();

                                finish();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadLabs() {

        db.collection("labs")
                .get()
                .addOnSuccessListener(snapshot -> {

                    labNames.clear();

                    for (DocumentSnapshot doc : snapshot) {

                        String labName =
                                doc.getString("labName");

                        if (labName != null
                                && !labName.isEmpty()) {

                            labNames.add(labName);
                        }
                    }

                    labNames.sort(
                            String::compareToIgnoreCase
                    );

                    labAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}