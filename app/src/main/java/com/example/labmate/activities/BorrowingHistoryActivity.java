package com.example.labmate.activities;

import android.os.Bundle;
import android.view.View;
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
import com.example.labmate.adapters.BorrowingAdapter;
import com.example.labmate.models.Borrowing;
import com.example.labmate.utils.Constants;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.BorrowingViewModel;

import java.util.ArrayList;

public class BorrowingHistoryActivity
        extends AppCompatActivity {

    private BorrowingViewModel borrowingViewModel;
    private UserSession userSession;

    private RecyclerView recyclerBorrowingHistory;
    private TextView tvNoBorrowingHistory;

    private BorrowingAdapter borrowingAdapter;
    private ArrayList<Borrowing> borrowingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_borrowing_history
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

        borrowingViewModel =
                new ViewModelProvider(this)
                        .get(BorrowingViewModel.class);

        userSession =
                new UserSession(this);

        recyclerBorrowingHistory =
                findViewById(
                        R.id.recyclerBorrowingHistory
                );

        tvNoBorrowingHistory =
                findViewById(
                        R.id.tvNoBorrowingHistory
                );

        borrowingList =
                new ArrayList<>();

        observeViewModel();

        setupRecyclerView();

        loadBorrowingHistory();
    }

    // ============================================================
    // OBSERVE VIEWMODEL
    // ============================================================

    private void observeViewModel() {

        borrowingViewModel.getMessage()
                .observe(
                        this,
                        message -> {

                            if (message == null
                                    || message.trim().isEmpty()) {

                                return;
                            }

                            Toast.makeText(
                                    this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // ============================================================
    // SETUP RECYCLER VIEW
    // ============================================================

    private void setupRecyclerView() {

        boolean showUserName =
                !Constants.ROLE_STUDENT.equalsIgnoreCase(
                        userSession.getRole()
                );

        borrowingAdapter =
                new BorrowingAdapter(
                        borrowingList,
                        false,
                        showUserName,
                        null
                );

        recyclerBorrowingHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerBorrowingHistory.setAdapter(
                borrowingAdapter
        );
    }

    // ============================================================
    // LOAD BORROWING HISTORY
    // ============================================================

    private void loadBorrowingHistory() {

        String userId =
                userSession.getUserId();

        String role =
                userSession.getRole();

        if (userId == null
                || userId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "User information is missing. Please log in again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (role == null
                || role.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "User role information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        borrowingViewModel.loadBorrowingHistory(
                userId,
                role,

                borrowings -> {

                    borrowingList.clear();

                    borrowingList.addAll(
                            borrowings
                    );

                    borrowingAdapter.notifyDataSetChanged();

                    updateEmptyState(
                            borrowings
                    );
                }
        );
    }

    // ============================================================
    // UPDATE EMPTY STATE
    // ============================================================

    private void updateEmptyState(
            ArrayList<Borrowing> borrowings
    ) {

        if (borrowings.isEmpty()) {

            tvNoBorrowingHistory.setVisibility(
                    View.VISIBLE
            );

            recyclerBorrowingHistory.setVisibility(
                    View.GONE
            );

        } else {

            tvNoBorrowingHistory.setVisibility(
                    View.GONE
            );

            recyclerBorrowingHistory.setVisibility(
                    View.VISIBLE
            );
        }
    }
}