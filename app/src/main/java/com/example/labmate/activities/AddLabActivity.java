package com.example.labmate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.states.AddLabState;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.AddLabViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddLabActivity extends AppCompatActivity {

    private AddLabViewModel viewModel;
    private FrameLayout loadingOverlay;
    private EditText labName;
    private EditText personInCharge;
    private AutoCompleteTextView locationDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        viewModel = new ViewModelProvider(this).get(AddLabViewModel.class);

        initializeViews();
        setupLocationDropdown();
        setupListeners();
        observeViewModel();
    }

    private void initializeViews() {

        labName = findViewById(R.id.labName);
        personInCharge = findViewById(R.id.personInCharge);
        locationDropdown = findViewById(R.id.actLocation);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setupLocationDropdown() {

        String[] locations = getResources().getStringArray(R.array.lab_locations);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.mtrl_auto_complete_simple_item, locations);

        locationDropdown.setAdapter(adapter);
        locationDropdown.setOnItemClickListener((parent, view, position, id) -> locationDropdown.clearFocus());
    }

    private void setupListeners() {

        Button clearButton = findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(v -> showClearConfirmation());

        Button addButton = findViewById(R.id.btn_add_lab);
        addButton.setOnClickListener(v -> addLab());
    }

    private void addLab() {

        String name = labName.getText().toString().trim().replaceAll("\\s+", " ");
        String inCharge = personInCharge.getText().toString().trim().replaceAll("\\s+", " ");
        String location = locationDropdown.getText().toString().trim();

        if (name.isEmpty() || inCharge.isEmpty() || location.isEmpty()) {

            Toast.makeText(this, "Fill all details", Toast.LENGTH_LONG).show();
            return;
        }

        UserSession session = new UserSession(this);
        String username = session.getName();
        String role = session.getRole();

        viewModel.addLab(name, inCharge, location, username, role);
    }

    private void observeViewModel() {
        viewModel.getState().observe(this, this::handleState);
    }

    private void handleState(AddLabState state) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:

                loadingOverlay.setVisibility(View.VISIBLE);
                break;

            case SUCCESS:

                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(this, state.getMessage(), Toast.LENGTH_LONG).show();
                clearForm();
                finish();
                break;

            case ERROR:

                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(this, state.getMessage() != null ? state.getMessage() : "Failed to add laboratory.", Toast.LENGTH_LONG).show();
                break;

            case IDLE:
            default:
                break;
        }
    }

    private void showClearConfirmation() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear Form")
                .setMessage("Do you want to clear the form?")
                .setCancelable(false)
                .setPositiveButton(
                        "Clear",
                        (dialog, which) ->
                                clearForm()
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                dialog.dismiss()
                )
                .show();
    }

    private void clearForm() {

        labName.setText("");
        personInCharge.setText("");
        locationDropdown.setText(null);
    }
}