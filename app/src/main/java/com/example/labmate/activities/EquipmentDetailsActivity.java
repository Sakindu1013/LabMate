package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class EquipmentDetailsActivity extends AppCompatActivity {

    private TextView viewType;
    private TextView viewTotal;
    private TextView viewInLab;
    private TextView viewBorrowed;
    private TextView viewMaintenance;
    private TextView viewRemoved;
    private TextView viewReserved;

    private RecyclerView recyclerView;

    private ArrayList<Equipment> equipmentList;
    private EquipmentAdapter adapter;

    private FirebaseFirestore db;

    private String equipmentType;
    private String labName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_equipment_details);

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

        equipmentType =
                getIntent().getStringExtra("TYPE");

        labName =
                getIntent().getStringExtra("LAB_NAME");

        initializeViews();
        initializeRecyclerView();

        loadEquipmentData();
    }

    private void initializeViews() {

        viewType = findViewById(R.id.equipmentType);
        viewTotal = findViewById(R.id.equipmentTotal);
        viewInLab = findViewById(R.id.equipmentInLab);
        viewBorrowed = findViewById(R.id.equipmentBorrowed);
        viewMaintenance = findViewById(R.id.equipmentMaintenance);
        viewRemoved = findViewById(R.id.equipmentRemoved);
        viewReserved = findViewById(R.id.equipmentReserved);

        viewType.setText(
                equipmentType != null
                        ? equipmentType
                        : "Unknown"
        );
    }

    private void initializeRecyclerView() {

        equipmentList = new ArrayList<>();

        adapter = new EquipmentAdapter(
                this,
                equipmentList
        );

        recyclerView = findViewById(
                R.id.recyclerEquipments
        );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);
    }

    private void loadEquipmentData() {

        if (equipmentType == null || equipmentType.isEmpty()) {
            Toast.makeText(
                    this,
                    "Equipment type is missing",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (labName != null && !labName.isEmpty()) {

            loadEquipmentForLab();

        } else {

            loadAllEquipmentOfType();
        }
    }

    private void loadEquipmentForLab() {

        db.collection("equipment")
                .whereEqualTo("type", equipmentType)
                .whereEqualTo("lab", labName)
                .get()
                .addOnSuccessListener(snapshot -> {

                    processEquipmentData(snapshot);
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadAllEquipmentOfType() {

        db.collection("equipment")
                .whereEqualTo("type", equipmentType)
                .get()
                .addOnSuccessListener(snapshot -> {

                    processEquipmentData(snapshot);
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void processEquipmentData(QuerySnapshot snapshot) {

        equipmentList.clear();

        int total = 0;
        int inLab = 0;
        int borrowed = 0;
        int maintenance = 0;
        int removed = 0;
        int reserved = 0;

        for (DocumentSnapshot doc : snapshot.getDocuments()) {

            Equipment equipment =
                    doc.toObject(Equipment.class);

            if (equipment == null) {
                continue;
            }

            equipmentList.add(equipment);

            total++;

            String state = equipment.getState();

            if (state == null) {
                continue;
            }

            switch (state) {

                case "In Lab":
                    inLab++;
                    break;

                case "Borrowed":
                    borrowed++;
                    break;

                case "Under Maintenance":
                    maintenance++;
                    break;

                case "Removed":
                    removed++;
                    break;

                case "Reserved":
                    reserved++;
                    break;
            }
        }

        equipmentList.sort((a, b) -> {

            String qrA = a.getQrId();
            String qrB = b.getQrId();

            if (qrA == null) {
                return 1;
            }

            if (qrB == null) {
                return -1;
            }

            return qrA.compareToIgnoreCase(qrB);
        });

        updateSummary(
                total,
                inLab,
                borrowed,
                maintenance,
                removed,
                reserved
        );

        adapter.notifyDataSetChanged();
    }

    private void updateSummary(
            int total,
            int inLab,
            int borrowed,
            int maintenance,
            int removed,
            int reserved) {

        viewTotal.setText(
                total + " Equipment"
        );

        viewInLab.setText(
                String.valueOf(inLab)
        );

        viewBorrowed.setText(
                String.valueOf(borrowed)
        );

        viewMaintenance.setText(
                String.valueOf(maintenance)
        );

        viewRemoved.setText(
                String.valueOf(removed)
        );

        viewReserved.setText(
                String.valueOf(reserved)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && equipmentType != null) {
            loadEquipmentData();
        }
    }
}