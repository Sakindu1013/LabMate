package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.states.EditLabState;
import com.example.labmate.viewmodels.EditLabViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditLabActivity extends AppCompatActivity {

    private EditLabViewModel viewModel;

    private EditText editName;
    private EditText editInCharge;
    private AutoCompleteTextView editLocation;

    private String labId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_edit_lab
        );

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

        viewModel =
                new ViewModelProvider(this)
                        .get(EditLabViewModel.class);

        getLabData();
        initializeViews();
        setupLocationDropdown();
        setupListeners();
        observeViewModel();
    }

    private void getLabData() {

        labId =
                getIntent().getStringExtra(
                        "LAB_ID"
                );
    }

    private void initializeViews() {

        editName =
                findViewById(R.id.labName);

        editInCharge =
                findViewById(R.id.personInCharge);

        editLocation =
                findViewById(R.id.actLocation);

        String labName =
                getIntent().getStringExtra(
                        "LAB_NAME"
                );

        String labInCharge =
                getIntent().getStringExtra(
                        "LAB_IN_CHARGE"
                );

        String labLocation =
                getIntent().getStringExtra(
                        "LAB_LOCATION"
                );

        editName.setText(labName);
        editInCharge.setText(labInCharge);
        editLocation.setText(
                labLocation,
                false
        );
    }

    private void setupLocationDropdown() {

        String[] locations =
                getResources()
                        .getStringArray(
                                R.array.lab_locations
                        );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        com.google.android.material.R.layout
                                .mtrl_auto_complete_simple_item,
                        locations
                );

        editLocation.setAdapter(adapter);

        editLocation.setOnItemClickListener(
                (parent, view, position, id) ->
                        editLocation.clearFocus()
        );
    }

    private void setupListeners() {

        Button editButton =
                findViewById(
                        R.id.btn_edit_lab
                );

        Button deleteButton =
                findViewById(
                        R.id.btn_delete_lab
                );

        editButton.setOnClickListener(
                v -> updateLab()
        );

        deleteButton.setOnClickListener(
                v -> showDeleteConfirmation()
        );
    }

    private void updateLab() {

        String name =
                editName.getText()
                        .toString()
                        .trim()
                        .replaceAll("\\s+", " ");

        String inCharge =
                editInCharge.getText()
                        .toString()
                        .trim()
                        .replaceAll("\\s+", " ");

        String location =
                editLocation.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()
                || inCharge.isEmpty()
                || location.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all details",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (labId == null
                || labId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Lab ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        viewModel.updateLab(
                labId,
                name,
                inCharge,
                location
        );
    }

    private void showDeleteConfirmation() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Delete")
                .setMessage(
                        "Do you want to delete this laboratory?"
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteLab()
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                dialog.dismiss()
                )
                .show();
    }

    private void deleteLab() {

        if (labId == null
                || labId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Lab ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        viewModel.deleteLab(
                labId
        );
    }

    private void observeViewModel() {

        viewModel.getState()
                .observe(
                        this,
                        this::handleState
                );
    }

    private void handleState(
            EditLabState state
    ) {

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {

            case LOADING:
                // Add ProgressBar later.
                break;

            case UPDATE_SUCCESS:
            case DELETE_SUCCESS:

                Toast.makeText(
                        this,
                        state.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                finish();

                break;

            case ERROR:

                Toast.makeText(
                        this,
                        state.getMessage() != null
                                ? state.getMessage()
                                : "Operation failed.",
                        Toast.LENGTH_LONG
                ).show();

                break;

            case IDLE:
            default:
                break;
        }
    }
}