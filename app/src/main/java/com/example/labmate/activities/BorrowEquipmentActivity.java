package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class BorrowEquipmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText equipmentQR;
    private Button borrowEquipment;
    private Button btnClear;
    private Button btnQR;

    private final ActivityResultLauncher<Intent> scannerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{

        if (result.getResultCode() == RESULT_OK && result.getData() != null){
            String qrId = result.getData().getStringExtra("QR_ID");
            loadEquipmentDetails(qrId);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_borrow_equipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        equipmentQR = findViewById(R.id.equipmentQR);

        borrowEquipment = findViewById(R.id.btn_borrow_equipment);
        btnClear = findViewById(R.id.btn_clear);
        btnQR = findViewById(R.id.btn_scan_qr);

        btnQR.setOnClickListener(v -> {
            Intent qrIntent = new Intent(BorrowEquipmentActivity.this, QRScannerActivity.class);
            scannerLauncher.launch(qrIntent);
        });

        borrowEquipment.setOnClickListener(v -> {

            String qrId = equipmentQR.getText().toString().trim();

            db.collection("equipment")
                    .whereEqualTo("qrId", qrId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()){

                            String state = snapshot.getDocuments().get(0).getString("state");

                            if ("Borrowed".equals(state)) {
                                Toast.makeText(this, "This equipment is already borrowed.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if ("Under Maintenance".equals(state) || "Removed".equals(state)) {
                                Toast.makeText(this, "This equipment cannot be borrowed.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String docID = snapshot.getDocuments().get(0).getId();

                            db.collection("equipment")
                                    .document(docID)
                                    .update("state", "Borrowed")
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getApplicationContext(), "Equipment Successfully Borrowed", Toast.LENGTH_LONG).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Equipment Not Found", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();;
                    });
        });

        btnClear.setOnClickListener(v -> {

            equipmentQR.setText("");
        });

    }

    private void loadEquipmentDetails(String qrId){

        db.collection("equipment")
                .whereEqualTo("qrId", qrId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()){

                        DocumentSnapshot doc = snapshot.getDocuments().get(0);

                        String name = doc.getString("equipmentName");
                        String model = doc.getString("equipmentModel");
                        String lab = doc.getString("lab");
                        String state = doc.getString("state");

                        EquipmentPreviewDialog.show(this, name, model, lab, state, "Borrow", () -> borrowEquipment(doc.getId()));

                    } else {
                        Toast.makeText(getApplicationContext(), "Equipment Not Found", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void borrowEquipment(String documentId){

        db.collection("equipment")
                .document(documentId)
                .update("state","Borrowed")
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "Equipment Successfully Borrowed", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}