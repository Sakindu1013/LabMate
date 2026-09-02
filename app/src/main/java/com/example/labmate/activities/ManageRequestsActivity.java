package com.example.labmate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.adapters.BorrowingRequestAdapter;
import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.utils.Constants;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.ManageRequestsViewModel;

import java.util.ArrayList;

public class ManageRequestsActivity extends AppCompatActivity {

    private TextView tvRequestsTitle;
    private TextView tvRequestsDescription;

    private RecyclerView recyclerManageRequests;

    private ManageRequestsViewModel viewModel;

    private BorrowingRequestAdapter adapter;

    private ArrayList<BorrowingRequest> requestList;

    private UserSession userSession;
    private FrameLayout loadingOverlay;
    private boolean initialLoadCompleted = false;
    private boolean showLoadingOverlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        initializeViews();

        userSession = new UserSession(this);

        setupRecyclerView();

        setupViewModel();

        displayRoleBasedContent();

        viewModel.loadRequests();
    }

    private void initializeViews() {

        tvRequestsTitle = findViewById(R.id.tvRequestsTitle);
        tvRequestsDescription = findViewById(R.id.tvRequestsDescription);
        recyclerManageRequests = findViewById(R.id.recycler_manage_requests);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        requestList = new ArrayList<>();
    }

    private void setupRecyclerView() {

        boolean isStudent = Constants.ROLE_STUDENT.equalsIgnoreCase(
                userSession.getRole()
        );

        boolean showActions = !isStudent;

        boolean showUserName = !isStudent;

        adapter = new BorrowingRequestAdapter(
                requestList,
                showActions,
                showUserName,

                new BorrowingRequestAdapter.RequestActionListener() {

                    @Override
                    public void onApprove(BorrowingRequest request) {

                        approveRequest(request);
                    }

                    @Override
                    public void onReject(BorrowingRequest request) {

                        rejectRequest(request);
                    }
                }
        );

        recyclerManageRequests.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerManageRequests.setAdapter(adapter);
    }

    private void setupViewModel() {

        viewModel = new ViewModelProvider(this)
                .get(ManageRequestsViewModel.class);

        viewModel.getLoading().observe(
                this,
                isLoading -> {

                    if (isLoading == null) {
                        return;
                    }

                    if (isLoading) {

                        if (showLoadingOverlay) {
                            loadingOverlay.setVisibility(View.VISIBLE);
                        }

                    } else {

                        loadingOverlay.setVisibility(View.GONE);

                        if (!initialLoadCompleted) {
                            initialLoadCompleted = true;
                        }
                    }
                }
        );

        viewModel.getRequests().observe(
                this,
                requests -> {

                    if (requests == null) {
                        return;
                    }

                    requestList.clear();

                    requestList.addAll(requests);

                    adapter.notifyDataSetChanged();
                }
        );

        viewModel.getErrorMessage().observe(
                this,
                message -> {

                    if (message == null || message.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
        );
    }

    private void displayRoleBasedContent() {

        String role = userSession.getRole();

        if (Constants.ROLE_STUDENT.equalsIgnoreCase(role)) {

            tvRequestsTitle.setText("My Borrowing Requests");

            tvRequestsDescription.setText(
                    "View your equipment borrowing requests."
            );

        } else if (Constants.ROLE_STAFF.equalsIgnoreCase(role)
                || Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {

            tvRequestsTitle.setText("Manage Borrowing Requests");

            tvRequestsDescription.setText(
                    "View and manage equipment borrowing requests."
            );
        }
    }

    private void approveRequest(BorrowingRequest request) {

        if (request == null || request.getId() == null) {
            return;
        }

        showLoadingOverlay = true;
        loadingOverlay.setVisibility(View.VISIBLE);

        viewModel.approveRequest(request.getId());
    }

    private void rejectRequest(BorrowingRequest request) {

        if (request == null || request.getId() == null) {
            return;
        }

        showLoadingOverlay = true;
        loadingOverlay.setVisibility(View.VISIBLE);

        viewModel.rejectRequest(request.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (viewModel != null && initialLoadCompleted) {

            showLoadingOverlay = false;
            viewModel.loadRequests();
        }
    }
}