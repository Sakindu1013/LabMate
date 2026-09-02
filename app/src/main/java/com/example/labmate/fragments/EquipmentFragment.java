package com.example.labmate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.activities.AddEquipmentActivity;
import com.example.labmate.activities.RemoveEquipmentActivity;
import com.example.labmate.adapters.EquipmentSummaryAdapter;
import com.example.labmate.models.EquipmentSummary;
import com.example.labmate.states.EquipmentSummaryState;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.EquipmentViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class EquipmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<EquipmentSummary> equipmentSummaryList;
    private EquipmentSummaryAdapter adapter;

    private Button buttonAddEquip;
    private Button buttonRemoveEquip;
    private TextView equipmentTotal;

    private FrameLayout loadingOverlay;
    private CircularProgressIndicator progressBar;

    private EquipmentViewModel equipmentViewModel;

    public EquipmentFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_equipment,
                container,
                false
        );

        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        setupListeners();

        return view;
    }

    @Override
    public void onViewCreated(
            View view,
            Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        equipmentViewModel.loadEquipmentSummary();
    }

    private void initializeViews(View view) {

        buttonAddEquip = view.findViewById(R.id.manage_equipment);
        buttonRemoveEquip = view.findViewById(R.id.remove_equipment);
        equipmentTotal = view.findViewById(R.id.equipmentTotal);
        recyclerView = view.findViewById(R.id.labRecyclerView);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        progressBar = view.findViewById(R.id.equipmentProgressBar);

        progressBar.setIndeterminate(true);
    }

    private void setupRecyclerView() {

        equipmentSummaryList = new ArrayList<>();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // null labName = show equipment
        // from all laboratories.
        adapter = new EquipmentSummaryAdapter(
                requireContext(),
                equipmentSummaryList,
                null
        );

        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {

        equipmentViewModel = new ViewModelProvider(this)
                .get(EquipmentViewModel.class);

        equipmentViewModel
                .getEquipmentSummaryState()
                .observe(
                        getViewLifecycleOwner(),
                        this::handleEquipmentState
                );
    }

    private void setupListeners() {

        UserSession session = new UserSession(requireContext());

        boolean isAdmin = session.isAdmin();

        buttonAddEquip.setVisibility(
                isAdmin
                        ? View.VISIBLE
                        : View.GONE
        );

        buttonRemoveEquip.setVisibility(
                isAdmin
                        ? View.VISIBLE
                        : View.GONE
        );

        buttonAddEquip.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    AddEquipmentActivity.class
            );

            startActivity(intent);
        });

        buttonRemoveEquip.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    RemoveEquipmentActivity.class
            );

            startActivity(intent);
        });
    }

    private void handleEquipmentState(EquipmentSummaryState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:
                showLoading();
                break;

            case SUCCESS:
                hideLoading();
                displayEquipment(state);
                break;

            case ERROR:
                hideLoading();
                showError(state.getMessage());
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

    private void displayEquipment(EquipmentSummaryState state) {

        equipmentTotal.setText(
                "Total Equipment: " + state.getTotalEquipment()
        );

        equipmentSummaryList.clear();

        if (state.getSummaries() != null) {
            equipmentSummaryList.addAll(state.getSummaries());
        }

        adapter.notifyDataSetChanged();
    }

    private void showError(String message) {

        if (!isAdded()) {
            return;
        }

        Toast.makeText(
                requireContext(),
                message != null
                        ? message
                        : "Failed to load equipment.",
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (equipmentViewModel != null) {
            equipmentViewModel.loadEquipmentSummary();
        }
    }
}