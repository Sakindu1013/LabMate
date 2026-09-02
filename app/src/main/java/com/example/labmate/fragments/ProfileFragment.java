package com.example.labmate.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText mobileEditText;
    private TextInputEditText dobEditText;
    private TextInputEditText roleEditText;

    private MaterialButton saveProfileButton;

    private FrameLayout loadingOverlay;

    private ProfileViewModel viewModel;
    private UserSession userSession;

    public ProfileFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        userSession = new UserSession(requireContext());

        viewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);

        setupDatePicker();
        observeViewModel();
        loadProfile();
    }

    private void initializeViews(View view) {

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        dobEditText = view.findViewById(R.id.dobEditText);
        roleEditText = view.findViewById(R.id.roleEditText);

        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);

        saveProfileButton.setOnClickListener(
                v -> saveProfile()
        );
    }

    private void loadProfile() {

        String userId = userSession.getUserId();

        if (TextUtils.isEmpty(userId)) {

            Toast.makeText(
                    requireContext(),
                    "User session not found.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        viewModel.loadUser(userId);
    }

    private void observeViewModel() {

        viewModel.getUserData()
                .observe(
                        getViewLifecycleOwner(),
                        this::displayUserData
                );

        viewModel.getLoading()
                .observe(
                        getViewLifecycleOwner(),
                        this::showLoading
                );

        viewModel.getUpdateSuccess()
                .observe(
                        getViewLifecycleOwner(),
                        success -> {

                            if (Boolean.TRUE.equals(success)) {

                                Toast.makeText(
                                        requireContext(),
                                        "Profile updated successfully.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Keep UserSession name in sync
                                userSession.saveUser(
                                        userSession.getUserId(),
                                        nameEditText.getText()
                                                .toString()
                                                .trim(),
                                        userSession.getRole()
                                );
                            }
                        }
                );

        viewModel.getError()
                .observe(
                        getViewLifecycleOwner(),
                        message -> {

                            if (!TextUtils.isEmpty(message)) {

                                Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void displayUserData(Map<String, Object> data) {

        if (data == null) {
            return;
        }

        Object name = data.get("name");
        Object email = data.get("email");
        Object mobile = data.get("mobile");
        Object dob = data.get("dob");
        Object role = data.get("role");

        nameEditText.setText(
                name != null ? name.toString() : ""
        );

        emailEditText.setText(
                email != null ? email.toString() : ""
        );

        mobileEditText.setText(
                mobile != null ? mobile.toString() : ""
        );

        dobEditText.setText(
                dob != null ? dob.toString() : ""
        );

        roleEditText.setText(
                role != null ? role.toString() : ""
        );
    }

    private void setupDatePicker() {

        dobEditText.setOnClickListener(
                v -> showDatePicker()
        );
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        requireContext(),
                        (view, year, month, dayOfMonth) -> {

                            String date = String.format(
                                    "%02d/%02d/%04d",
                                    dayOfMonth,
                                    month + 1,
                                    year
                            );

                            dobEditText.setText(date);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.getDatePicker()
                .setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private boolean validateProfile() {

        String name = nameEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        // Name
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }

        if (name.length() < 2) {
            nameEditText.setError("Name must contain at least 2 characters");
            nameEditText.requestFocus();
            return false;
        }

        // Mobile - optional
        if (!mobile.isEmpty()) {
            if (!mobile.matches("^07[0-9]{8}$")) {
                mobileEditText.setError(
                        "Enter a valid mobile number (07xxxxxxxx)"
                );
                mobileEditText.requestFocus();
                return false;
            }
        }

        // DOB - optional
        if (!dob.isEmpty()) {
            try {
                SimpleDateFormat format =
                        new SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                        );

                format.setLenient(false);

                Date selectedDate = format.parse(dob);

                if (selectedDate != null && selectedDate.after(new Date())) {

                    dobEditText.setError(
                            "Date of birth cannot be in the future"
                    );

                    dobEditText.requestFocus();
                    return false;
                }

            } catch (ParseException e) {

                dobEditText.setError(
                        "Enter date in dd/MM/yyyy format"
                );

                dobEditText.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void saveProfile() {

        if (!validateProfile()) {
            return;
        }

        String name = nameEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        viewModel.updateProfile(
                userSession.getUserId(),
                name,
                mobile,
                dob
        );
    }

    private void showLoading(boolean loading) {

        if (loading) {

            loadingOverlay.setVisibility(View.VISIBLE);
            saveProfileButton.setEnabled(false);

        } else {

            loadingOverlay.setVisibility(View.GONE);
            saveProfileButton.setEnabled(true);
        }
    }
}