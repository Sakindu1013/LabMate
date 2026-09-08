package com.example.labmate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.activities.AddLabActivity;
import com.example.labmate.adapters.LabAdapter;
import com.example.labmate.models.Lab;
import com.example.labmate.states.LabsState;
import com.example.labmate.viewmodels.LabsViewModel;

import java.util.ArrayList;

public class LabsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Lab> labList;
    private LabAdapter adapter;
    private Button buttonAddLab;
    private FrameLayout loadingOverlay;
    private LabsViewModel labsViewModel;

    public LabsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_labs,
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

        labsViewModel.loadLabs();
    }

    private void initializeViews(View view) {

        recyclerView = view.findViewById(R.id.labRecyclerView);
        buttonAddLab = view.findViewById(R.id.manage_labs);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private void setupRecyclerView() {

        labList = new ArrayList<>();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // Initially false.
        // It will be updated when the ViewModel
        // returns the user's role.
        adapter = new LabAdapter(
                requireContext(),
                labList,
                false,
                false
        );

        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {

        labsViewModel = new ViewModelProvider(this)
                .get(LabsViewModel.class);

        labsViewModel.getLabsState()
                .observe(
                        getViewLifecycleOwner(),
                        this::handleLabsState
                );
    }

    private void setupListeners() {

        buttonAddLab.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    AddLabActivity.class
            );

            startActivity(intent);
        });
    }

    private void handleLabsState(LabsState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:
                handleLoading();
                break;

            case SUCCESS:
                displayLabs(state);
                break;

            case ERROR:
                loadingOverlay.setVisibility(View.GONE);
                showError(state.getMessage());
                break;

            case IDLE:
            default:
                break;
        }
    }

    private void handleLoading() {

        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void displayLabs(LabsState state) {

        loadingOverlay.setVisibility(View.GONE);

        boolean isAdmin = state.isAdmin();
        boolean isStaff = state.isStaff();

        // Show/hide Add Lab button.
        buttonAddLab.setVisibility(
                isAdmin || isStaff
                        ? View.VISIBLE
                        : View.GONE
        );

        // Update adapter permission.
        adapter.setAdmin(isAdmin);
        adapter.setStaff(isStaff);

        // Update list.
        labList.clear();

        if (state.getLabs() != null) {
            labList.addAll(state.getLabs());
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
                        : "Failed to load labs.",
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (labsViewModel != null) {
            labsViewModel.loadLabs();
        }
    }
}