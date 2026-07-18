package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReturnEquipmentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText equipmentQR;
    private Button returnEquipment;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return_equipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        equipmentQR = findViewById(R.id.equipmentQR);

        returnEquipment = findViewById(R.id.btn_return_equipment);
        btnClear = findViewById(R.id.btn_clear);

        returnEquipment.setOnClickListener(v -> {

            String qrId = equipmentQR.getText().toString().trim();

            db.collection("equipment")
                    .whereEqualTo("qrId", qrId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()){

                            String state = snapshot.getDocuments().get(0).getString("state");

                            if ("In Lab".equals(state)) {
                                Toast.makeText(this, "This equipment is already returned.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if ("Under Maintenance".equals(state) || "Removed".equals(state)) {
                                Toast.makeText(this, "This equipment cannot be returned.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String docID = snapshot.getDocuments().get(0).getId();

                            db.collection("equipment")
                                    .document(docID)
                                    .update("state", "In Lab")
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getApplicationContext(), "Equipment Successfully Returned", Toast.LENGTH_LONG).show();
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
}