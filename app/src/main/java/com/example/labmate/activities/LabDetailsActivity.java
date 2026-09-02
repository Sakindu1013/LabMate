package com.example.labmate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.adapters.EquipmentSummaryAdapter;
import com.example.labmate.models.EquipmentSummary;
import com.example.labmate.states.LabDetailsState;
import com.example.labmate.viewmodels.LabDetailsViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class LabDetailsActivity extends AppCompatActivity {

    private TextView viewName;
    private TextView viewInCharge;
    private TextView viewLocation;
    private TextView equipmentTotal;

    private RecyclerView recyclerView;

    private EquipmentSummaryAdapter adapter;

    private ArrayList<EquipmentSummary> equipmentSummaryList;

    private LabDetailsViewModel viewModel;

    private FrameLayout loadingOverlay;
    private String labName;
    private String labInCharge;
    private String labLocation;

    private boolean initialLoadCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getLabData();
        initializeViews();
        setupRecyclerView();
        setupViewModel();
    }

    private void getLabData() {

        labName = getIntent().getStringExtra("LAB_NAME");
        labInCharge = getIntent().getStringExtra("LAB_IN_CHARGE");
        labLocation = getIntent().getStringExtra("LAB_LOCATION");
    }

    private void initializeViews() {

        viewName = findViewById(R.id.labName);
        viewInCharge = findViewById(R.id.personInCharge);
        viewLocation = findViewById(R.id.actLocation);
        equipmentTotal = findViewById(R.id.equipmentTotal);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        viewName.setText(labName);
        viewInCharge.setText("In Charge: " + labInCharge);
        viewLocation.setText("Location: " + labLocation);
    }

    private void setupRecyclerView() {

        recyclerView = findViewById(R.id.recyclerEquipments);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        equipmentSummaryList = new ArrayList<>();

        adapter = new EquipmentSummaryAdapter(
                this,
                equipmentSummaryList,
                labName
        );

        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {

        viewModel = new ViewModelProvider(this)
                .get(LabDetailsViewModel.class);

        viewModel.getState().observe(
                this,
                this::handleState
        );
    }

    private void handleState(LabDetailsState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:
                showLoading();
                break;

            case SUCCESS:
                hideLoading();
                initialLoadCompleted = true;

                equipmentTotal.setText(
                        "Total Equipment: " + state.getTotalEquipment()
                );

                equipmentSummaryList.clear();

                if (state.getSummaries() != null) {
                    equipmentSummaryList.addAll(state.getSummaries());
                }

                adapter.notifyDataSetChanged();
                break;

            case ERROR:
                hideLoading();
                initialLoadCompleted = true;

                Toast.makeText(
                        this,
                        state.getMessage() != null ? state.getMessage() : "Failed to load equipment.",
                        Toast.LENGTH_LONG
                ).show();
                break;

            case IDLE:
            default:
                break;
        }
    }

    private void showLoading() {

        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {

        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (viewModel != null && labName != null) {
            viewModel.loadEquipmentSummary(labName, false);
        }
    }
}