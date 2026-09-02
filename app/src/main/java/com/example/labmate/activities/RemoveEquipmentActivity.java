package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;
import com.example.labmate.dialogs.EquipmentPreviewDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RemoveEquipmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText equipmentQR;
    private Button removeEquipment;
    private Button btnClear;
    private Button btnQR;
    private FrameLayout loadingOverlay;

    private final ActivityResultLauncher<Intent> scannerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String qrId = result.getData().getStringExtra("QR_ID");
                            loadEquipmentDetails(qrId);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_equipment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        equipmentQR = findViewById(R.id.equipmentQR);
        removeEquipment = findViewById(R.id.btn_remove_equipment);
        btnClear = findViewById(R.id.btn_clear);
        btnQR = findViewById(R.id.btn_scan_qr);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        btnQR.setOnClickListener(v -> {
            Intent qrIntent = new Intent(RemoveEquipmentActivity.this, QRScannerActivity.class);
            scannerLauncher.launch(qrIntent);
        });

        removeEquipment.setOnClickListener(v -> {

            String qrId = equipmentQR.getText().toString().trim();

            if (qrId.isEmpty()) {
                Toast.makeText(this, "Enter equipment ID", Toast.LENGTH_SHORT).show();
                return;
            }

            loadingOverlay.setVisibility(FrameLayout.VISIBLE);

            db.collection("equipment")
                    .whereEqualTo("qrId", qrId)
                    .get()
                    .addOnSuccessListener(snapshot -> {

                        if (!snapshot.isEmpty()) {

                            String state = snapshot.getDocuments().get(0).getString("state");

                            if ("Removed".equals(state)) {
                                loadingOverlay.setVisibility(FrameLayout.GONE);
                                Toast.makeText(this, "This equipment is already removed.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if ("Borrowed".equals(state)) {
                                loadingOverlay.setVisibility(FrameLayout.GONE);
                                Toast.makeText(this, "This equipment cannot be removed.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String docID = snapshot.getDocuments().get(0).getId();

                            db.collection("equipment")
                                    .document(docID)
                                    .update("state", "Removed")
                                    .addOnSuccessListener(unused -> {
                                        loadingOverlay.setVisibility(FrameLayout.GONE);
                                        Toast.makeText(this, "Equipment Successfully Removed", Toast.LENGTH_LONG).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        loadingOverlay.setVisibility(FrameLayout.GONE);
                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            loadingOverlay.setVisibility(FrameLayout.GONE);
                            Toast.makeText(this, "Equipment Not Found", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadingOverlay.setVisibility(FrameLayout.GONE);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        btnClear.setOnClickListener(v -> {
            equipmentQR.setText("");
        });
    }

    private void loadEquipmentDetails(String qrId) {

        if (qrId == null || qrId.trim().isEmpty()) {
            Toast.makeText(this, "Equipment ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingOverlay.setVisibility(FrameLayout.VISIBLE);

        db.collection("equipment")
                .whereEqualTo("qrId", qrId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    loadingOverlay.setVisibility(FrameLayout.GONE);

                    if (!snapshot.isEmpty()) {

                        DocumentSnapshot doc = snapshot.getDocuments().get(0);

                        String name = doc.getString("equipmentName");
                        String model = doc.getString("equipmentModel");
                        String lab = doc.getString("lab");
                        String state = doc.getString("state");

                        EquipmentPreviewDialog.show(
                                this,
                                name,
                                model,
                                lab,
                                state,
                                "Remove",
                                () -> removeEquipment(doc.getId())
                        );

                    } else {
                        Toast.makeText(this, "Equipment Not Found", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    loadingOverlay.setVisibility(FrameLayout.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void removeEquipment(String documentId) {

        loadingOverlay.setVisibility(FrameLayout.VISIBLE);

        db.collection("equipment")
                .document(documentId)
                .update("state", "Removed")
                .addOnSuccessListener(unused -> {

                    loadingOverlay.setVisibility(FrameLayout.GONE);
                    Toast.makeText(this, "Equipment Successfully Removed", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingOverlay.setVisibility(FrameLayout.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}