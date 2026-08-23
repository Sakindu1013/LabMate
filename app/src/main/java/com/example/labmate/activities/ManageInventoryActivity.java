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
import com.example.labmate.repositories.EquipmentRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageInventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ArrayList<Equipment> equipmentList;
    private EquipmentAdapter adapter;

    private EquipmentRepository equipmentRepository;

    private TextInputEditText equipmentID;

    private Button searchEquipment;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_inventory);

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

        initializeViews();
        initializeRecyclerView();

        equipmentRepository =
                new EquipmentRepository();

        loadAllEquipment();

        setupListeners();
    }

    private void initializeViews() {

        equipmentID =
                findViewById(R.id.equipmentQR);

        searchEquipment =
                findViewById(R.id.btn_search_equipment);

        btnClear =
                findViewById(R.id.btn_clear);
    }

    private void initializeRecyclerView() {

        equipmentList =
                new ArrayList<>();

        adapter =
                new EquipmentAdapter(
                        this,
                        equipmentList
                );

        recyclerView =
                findViewById(R.id.recyclerEquipments);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {

        searchEquipment.setOnClickListener(v -> {

            String qrId =
                    equipmentID
                            .getText()
                            .toString()
                            .trim();

            if (qrId.isEmpty()) {

                equipmentID.setError(
                        "Enter Equipment ID"
                );

                return;
            }

            searchEquipment(qrId);
        });

        btnClear.setOnClickListener(v -> {

            equipmentID.setText("");

            loadAllEquipment();
        });
    }

    /**
     * Loads all equipment.
     */
    private void loadAllEquipment() {

        equipmentRepository.getAll(

                this::displayEquipment,

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    /**
     * Searches equipment using QR ID.
     */
    private void searchEquipment(String qrId) {

        equipmentRepository.findByQrId(
                qrId,

                snapshot -> {

                    if (snapshot.isEmpty()) {

                        equipmentList.clear();

                        adapter.notifyDataSetChanged();

                        Toast.makeText(
                                this,
                                "Equipment Not Found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    displayEquipment(snapshot);
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    /**
     * Converts Firestore documents into Equipment objects
     * and displays them in the RecyclerView.
     */
    private void displayEquipment(
            QuerySnapshot snapshot
    ) {

        equipmentList.clear();

        for (DocumentSnapshot doc :
                snapshot.getDocuments()) {

            Equipment equipment =
                    doc.toObject(Equipment.class);

            if (equipment != null) {
                equipmentList.add(equipment);
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

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAllEquipment();
    }
}