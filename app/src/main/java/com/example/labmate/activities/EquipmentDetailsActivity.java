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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EquipmentDetailsActivity extends AppCompatActivity {

    private String equipmentType;
    private int total;
    private int inLab;
    private int borrowed;
    private int underMaintenance;
    private int removed;
    private TextView viewType;
    private TextView viewTotal;
    private TextView viewInLab;
    private TextView viewBorrowed;
    private TextView viewMaintenance;
    private TextView viewRemoved;
    private FirebaseFirestore db;
    private ArrayList<Equipment> equipmentList;
    private EquipmentAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_equipment_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        equipmentType = getIntent().getStringExtra("TYPE");
        total = getIntent().getIntExtra("TOTAL", 0);
        inLab = getIntent().getIntExtra("IN_LAB", 0);
        borrowed = getIntent().getIntExtra("BORROWED", 0);
        underMaintenance = getIntent().getIntExtra("MAINTENANCE", 0);
        removed = getIntent().getIntExtra("REMOVED", 0);

        viewType = findViewById(R.id.equipmentType);
        viewTotal = findViewById(R.id.equipmentTotal);
        viewInLab = findViewById(R.id.equipmentInLab);
        viewBorrowed = findViewById(R.id.equipmentBorrowed);
        viewMaintenance = findViewById(R.id.equipmentMaintenance);
        viewRemoved = findViewById(R.id.equipmentRemoved);

        viewType.setText(equipmentType);
        viewTotal.setText(total + " Equipment");
        viewInLab.setText(String.valueOf(inLab));
        viewBorrowed.setText(String.valueOf(borrowed));
        viewMaintenance.setText(String.valueOf(underMaintenance));
        viewRemoved.setText(String.valueOf(removed));

        equipmentList = new ArrayList<>();
        adapter = new EquipmentAdapter(EquipmentDetailsActivity.this, equipmentList);

        recyclerView = findViewById(R.id.recyclerEquipments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadEquipmentData(equipmentType);
    }

    private void loadEquipmentData(String equipmentType) {
        db.collection("equipments")
                .whereEqualTo("type", equipmentType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    equipmentList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){

                        Equipment equipment = doc.toObject(Equipment.class);
                        equipmentList.add(equipment);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateSummary() {

        db.collection("equipments")
                .whereEqualTo("type", equipmentType)
                .get()
                .addOnSuccessListener(snapshot -> {

                    int total = 0;
                    int inLab = 0;
                    int borrowed = 0;
                    int maintenance = 0;
                    int removed = 0;

                    for (DocumentSnapshot doc : snapshot) {

                        total++;
                        String state = doc.getString("state");

                        if (state == null)
                            continue;

                        switch(state){

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
                        }
                    }

                    viewTotal.setText(total + " Equipment");
                    viewInLab.setText(String.valueOf(inLab));
                    viewBorrowed.setText(String.valueOf(borrowed));
                    viewMaintenance.setText(String.valueOf(maintenance));
                    viewRemoved.setText(String.valueOf(removed));

                });
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (equipmentType != null){
            loadEquipmentData(equipmentType);
            updateSummary();
        }
    }
}