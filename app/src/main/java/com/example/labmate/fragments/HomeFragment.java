package com.example.labmate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.activities.BorrowEquipmentActivity;
import com.example.labmate.activities.ManageInventoryActivity;
import com.example.labmate.activities.ManageUserActivity;
import com.example.labmate.activities.ReturnEquipmentActivity;
import com.example.labmate.states.HomeState;
import com.example.labmate.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {

    private TextView txtName;
    private TextView txtRole;

    private Button buttonBorrow;
    private Button buttonReturn;
    private Button buttonManageInventory;
    private Button buttonManageUsers;

    private HomeViewModel homeViewModel;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        initializeViews(view);
        setupViewModel();
        setupListeners();

        return view;
    }

    @Override
    public void onViewCreated(
            View view,
            Bundle savedInstanceState
    ) {
        super.onViewCreated(
                view,
                savedInstanceState
        );

        homeViewModel.loadUserData();
    }

    private void initializeViews(View view) {

        txtName =
                view.findViewById(R.id.tvName);

        txtRole =
                view.findViewById(R.id.tvRole);

        buttonBorrow =
                view.findViewById(
                        R.id.equipment_request
                );

        buttonReturn =
                view.findViewById(
                        R.id.equipment_return
                );

        buttonManageInventory =
                view.findViewById(
                        R.id.manage_inventory
                );

        buttonManageUsers =
                view.findViewById(
                        R.id.manage_users
                );

        buttonManageInventory.setVisibility(
                View.GONE
        );

        buttonManageUsers.setVisibility(
                View.GONE
        );
    }

    private void setupViewModel() {

        homeViewModel =
                new ViewModelProvider(this)
                        .get(HomeViewModel.class);

        homeViewModel.getHomeState()
                .observe(
                        getViewLifecycleOwner(),
                        this::handleHomeState
                );
    }

    private void setupListeners() {

        buttonBorrow.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            BorrowEquipmentActivity.class
                    );

            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ReturnEquipmentActivity.class
                    );

            startActivity(intent);
        });

        buttonManageInventory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ManageInventoryActivity.class
                    );

            startActivity(intent);
        });

        buttonManageUsers.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            ManageUserActivity.class
                    );

            startActivity(intent);
        });
    }

    private void handleHomeState(
            HomeState state
    ) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:
                // ProgressBar can be added here later.
                break;

            case SUCCESS:
                displayUserData(state);
                break;

            case ERROR:
                showError(state.getMessage());
                break;

            case IDLE:
            default:
                break;
        }
    }

    private void displayUserData(
            HomeState state
    ) {

        txtName.setText(
                state.getName()
        );

        txtRole.setText(
                state.getRole()
        );

        if (state.canManageInventory()) {

            buttonManageInventory.setVisibility(
                    View.VISIBLE
            );
        }

        if (state.isAdmin()) {

            buttonManageUsers.setVisibility(
                    View.VISIBLE
            );
        }
    }

    private void showError(String message) {

        if (!isAdded()) {
            return;
        }

        Toast.makeText(
                requireContext(),
                message != null
                        ? message
                        : "Failed to load data.",
                Toast.LENGTH_LONG
        ).show();
    }
}