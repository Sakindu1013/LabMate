package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.adapters.EquipmentAdapter;
import com.example.labmate.models.Equipment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ManageInventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Equipment> equipmentList;
    private EquipmentAdapter adapter;
    private FirebaseFirestore db;
    private TextInputEditText equipmentID;
    private Button searchEquipment;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        searchEquipment = findViewById(R.id.btn_search_equipment);
        btnClear = findViewById(R.id.btn_clear);
        equipmentList = new ArrayList<>();
        adapter = new EquipmentAdapter(ManageInventoryActivity.this, equipmentList);

        equipmentID = findViewById(R.id.equipmentQR);

        recyclerView = findViewById(R.id.recyclerEquipments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchEquipment.setOnClickListener(v -> {
            String qrId = equipmentID.getText().toString().trim();
            if (qrId.isEmpty()) {
                equipmentID.setError("Enter Equipment ID");
                return;
            }
            loadEquipmentData(qrId);
        });

        btnClear.setOnClickListener(v -> {
            equipmentID.setText("");
        });
    }

    private void loadEquipmentData(String qrId) {

        db.collection("equipment")
                .whereEqualTo("qrId", qrId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    equipmentList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Equipment equipment = doc.toObject(Equipment.class);
                        equipmentList.add(equipment);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}