package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private Button btnScanQR;
    private boolean skipNextResumeRefresh = false;
    private View loadingOverlay;
    private boolean initialLoadCompleted = false;

    private final ActivityResultLauncher<Intent> scannerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            String qrId = result.getData()
                                    .getStringExtra("QR_ID");

                            if (qrId != null && !qrId.trim().isEmpty()) {

                                equipmentID.setText(qrId);

                                searchEquipment(qrId);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeRecyclerView();

        equipmentRepository = new EquipmentRepository();

        loadAllEquipment();

        setupListeners();
    }

    private void initializeViews() {

        equipmentID = findViewById(R.id.equipmentQR);
        searchEquipment = findViewById(R.id.btn_search_equipment);
        btnClear = findViewById(R.id.btn_clear);
        btnScanQR = findViewById(R.id.btn_scan_qr);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void initializeRecyclerView() {

        equipmentList = new ArrayList<>();

        adapter = new EquipmentAdapter(
                this,
                equipmentList
        );

        recyclerView = findViewById(R.id.recyclerEquipments);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {

        searchEquipment.setOnClickListener(v -> {

            String qrId = equipmentID
                    .getText()
                    .toString()
                    .trim();

            if (qrId.isEmpty()) {

                equipmentID.setError("Enter Equipment ID");

                return;
            }

            searchEquipment(qrId);
        });

        btnClear.setOnClickListener(v -> {

            equipmentID.setText("");

            loadAllEquipment();
        });

        btnScanQR.setOnClickListener(v -> {

            skipNextResumeRefresh = true;

            Intent qrIntent = new Intent(
                    ManageInventoryActivity.this,
                    QRScannerActivity.class
            );

            scannerLauncher.launch(qrIntent);
        });
    }

    /**
     * Loads all equipment.
     */
    private void loadAllEquipment() {

        equipmentRepository.getAll(

                snapshot -> {

                    displayEquipment(snapshot);

                    if (!initialLoadCompleted) {
                        hideLoadingOverlay();
                        initialLoadCompleted = true;
                    }
                },

                e -> {

                    if (!initialLoadCompleted) {
                        hideLoadingOverlay();
                        initialLoadCompleted = true;
                    }

                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
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

                        Toast.makeText(this, "Equipment Not Found", Toast.LENGTH_LONG).show();

                        return;
                    }

                    displayEquipment(snapshot);
                },

                e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

    /**
     * Converts Firestore documents into Equipment objects
     * and displays them in the RecyclerView.
     */
    private void displayEquipment(QuerySnapshot snapshot) {

        equipmentList.clear();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {

            Equipment equipment = doc.toObject(Equipment.class);

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

    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoadingOverlay() {
        loadingOverlay.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (skipNextResumeRefresh) {
            skipNextResumeRefresh = false;
            return;
        }

        loadAllEquipment();
    }
}