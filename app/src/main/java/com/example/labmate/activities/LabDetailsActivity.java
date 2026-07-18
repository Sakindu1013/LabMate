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
import com.example.labmate.adapters.EquipmentSummaryAdapter;
import com.example.labmate.models.EquipmentSummary;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class LabDetailsActivity extends AppCompatActivity {

    private TextView viewName;
    private TextView viewInCharge;
    private TextView viewlocation;
    private TextView equipmentTotal;
    private RecyclerView recyclerView;
    private EquipmentSummaryAdapter adapter;
    private ArrayList<EquipmentSummary> equipmentSummaryList;
    private FirebaseFirestore db;
    private String labName;
    private String labInCharge;
    private String labLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        labName = getIntent().getStringExtra("LAB_NAME");
        labInCharge = getIntent().getStringExtra("LAB_IN_CHARGE");
        labLocation = getIntent().getStringExtra("LAB_LOCATION");

        viewName = findViewById(R.id.labName);
        viewInCharge = findViewById(R.id.personInCharge);
        viewlocation = findViewById(R.id.actLocation);
        equipmentTotal = findViewById(R.id.equipmentTotal);

        viewName.setText(labName);
        viewInCharge.setText("In Charge: " + labInCharge);
        viewlocation.setText("Location: " + labLocation);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerEquipments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        equipmentSummaryList = new ArrayList<>();
        adapter = new EquipmentSummaryAdapter(this, equipmentSummaryList, labName);

        recyclerView.setAdapter(adapter);
        loadEquipmentSummary(labName);
    }

    public void loadEquipmentSummary(String labName){

        db.collection("equipment")
                .whereEqualTo("lab", labName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    equipmentSummaryList.clear();

                    int totalEquipment = queryDocumentSnapshots.size();
                    equipmentTotal.setText("Total Equipment: " + totalEquipment);

                    HashMap<String, EquipmentSummary> map = new HashMap<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots){

                        String type = doc.getString("type");
                        String state = doc.getString("state");

                        if (type == null) continue;

                        EquipmentSummary summary = map.get(type);

                        if (summary == null){
                            summary = new EquipmentSummary(type);
                            map.put(type, summary);
                        }

                        summary.increaseTotal();

                        if (state == null) continue;

                        switch (state){
                            case "In Lab":
                                summary.increaseInLab();
                                break;

                            case "Borrowed":
                                summary.increaseBorrowed();
                                break;

                            case "Under Maintenance":
                                summary.increaseMaintenance();
                                break;

                            case "Removed":
                                summary.increaseRemoved();
                                break;
                        }
                    }

                    equipmentSummaryList.addAll(map.values());
                    equipmentSummaryList.sort((a,b) ->
                            a.getType().compareToIgnoreCase(b.getType()));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (labName != null) {
            loadEquipmentSummary(labName);
        }
    }
}