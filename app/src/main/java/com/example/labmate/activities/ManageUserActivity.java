package com.example.labmate.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
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
import com.example.labmate.adapters.ManageUserAdapter;
import com.example.labmate.models.User;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.ManageUserViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ManageUserActivity extends AppCompatActivity {

    private RecyclerView recyclerUsers;
    private View loadingOverlay;
    private boolean initialLoadCompleted = false;
    private TextInputEditText etSearchEmail;
    private TextView tvNoUsers;
    private ManageUserAdapter adapter;
    private ManageUserViewModel viewModel;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupRecyclerView();
        setupViewModel();
        setupSearch();
        loadUsers();
    }

    private void initializeViews() {

        recyclerUsers = findViewById(R.id.recyclerUsers);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        etSearchEmail = findViewById(R.id.etSearchEmail);
        tvNoUsers = findViewById(R.id.tvNoUsers);

        userSession = new UserSession(this);
    }

    private void setupRecyclerView() {

        adapter = new ManageUserAdapter(
                this::handleRoleChange
        );

        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerUsers.setAdapter(adapter);
    }

    private void setupViewModel() {

        viewModel = new ViewModelProvider(this)
                .get(ManageUserViewModel.class);

        viewModel.getUsers().observe(
                this,
                users -> {

                    adapter.setUsers(users);

                    if (users == null || users.isEmpty()) {

                        tvNoUsers.setVisibility(View.VISIBLE);

                    } else {

                        tvNoUsers.setVisibility(View.GONE);
                    }

                    // Initial load is complete
                    if (!initialLoadCompleted) {

                        hideLoadingOverlay();

                        initialLoadCompleted = true;
                    }
                }
        );

        viewModel.getError().observe(
                this,
                message -> {

                    if (message != null && !message.isEmpty()) {

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }

                    // Hide overlay if the initial load fails
                    if (!initialLoadCompleted) {

                        hideLoadingOverlay();

                        initialLoadCompleted = true;
                    }
                }
        );

        viewModel.getRoleUpdateSuccess().observe(
                this,
                success -> {

                    if (success == null) {
                        return;
                    }

                    if (success) {

                        Toast.makeText(
                                this,
                                "User role updated.",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Refresh silently
                        loadUsers();

                    } else {

                        Toast.makeText(
                                this,
                                "Failed to update user role.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void setupSearch() {

        etSearchEmail.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        viewModel.searchUsers(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                }
        );
    }

    private void loadUsers() {

        String currentAdminUid = userSession.getUserId();

        viewModel.loadUsers(currentAdminUid);
    }

    private void handleRoleChange(User user, String newRole) {

        viewModel.updateUserRole(
                user.getUid(),
                newRole
        );
    }

    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoadingOverlay() {
        loadingOverlay.setVisibility(View.GONE);
    }
}