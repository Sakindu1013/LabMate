package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.adapters.BorrowingRequestAdapter;
import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.ManageRequestsViewModel;

import java.util.ArrayList;

public class BorrowingRequestActivity extends AppCompatActivity {

    private ManageRequestsViewModel viewModel;
    private UserSession userSession;
    private RecyclerView recyclerView;
    private ArrayList<BorrowingRequest> requestList;
    private BorrowingRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_borrowing_requests);

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

        userSession = new UserSession(this);

        if (!userSession.canManageInventory()) {
            Toast.makeText(
                    this,
                    "You do not have permission to manage borrowing requests.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        viewModel =
                new ViewModelProvider(this)
                        .get(ManageRequestsViewModel.class);

        initializeRecyclerView();
        observeViewModel();
        loadRequests();
    }

    private void initializeRecyclerView() {

        recyclerView =
                findViewById(R.id.recyclerBorrowingRequests);

        requestList = new ArrayList<>();

        adapter =
                new BorrowingRequestAdapter(
                        requestList,
                        true,
                        true,
                        new BorrowingRequestAdapter.RequestActionListener() {

                            @Override
                            public void onApprove(BorrowingRequest request) {
                                Toast.makeText(
                                        BorrowingRequestActivity.this,
                                        "Activity received Approve",
                                        Toast.LENGTH_SHORT
                                ).show();
                                approveRequest(request);
                                android.util.Log.d(
                                        "REQUEST_BUTTON",
                                        "Activity received APPROVE"
                                );
                            }

                            @Override
                            public void onReject(BorrowingRequest request) {
                                rejectRequest(request);
                                android.util.Log.d(
                                        "REQUEST_BUTTON",
                                        "Activity received REJECT"
                                );
                            }
                        }
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {

        viewModel.getErrorMessage().observe(this, message -> {

            if (message == null || message.trim().isEmpty()) {
                return;
            }

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
            ).show();

            if (message.equals(
                    "Borrowing request approved successfully."
            ) || message.equals(
                    "Borrowing request rejected."
            )) {
                loadRequests();
            }
        });
    }

    private void loadRequests() {
        viewModel.loadRequests();
    }



    private void approveRequest(BorrowingRequest request) {

        if (request == null || request.getId() == null) {
            return;
        }

        viewModel.approveRequest(request.getId());
    }

    private void rejectRequest(BorrowingRequest request) {

        if (request == null || request.getId() == null) {
            return;
        }

        viewModel.rejectRequest(request.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (viewModel != null) {
            loadRequests();
        }
    }
}