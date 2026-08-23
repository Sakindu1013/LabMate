package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmate.R;
import com.example.labmate.adapters.BorrowingAdapter;
import com.example.labmate.dialogs.EquipmentPreviewDialog;
import com.example.labmate.models.Borrowing;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.utils.Constants;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.BorrowingRequestViewModel;
import com.example.labmate.viewmodels.BorrowingViewModel;
import com.example.labmate.viewmodels.EquipmentViewModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class BorrowEquipmentActivity extends AppCompatActivity {

    private EquipmentViewModel viewModel;
    private BorrowingRequestViewModel borrowingRequestViewModel;
    private BorrowingViewModel borrowingViewModel;
    private EquipmentRepository equipmentRepository;
    private UserSession userSession;
    private EditText equipmentQR;
    private Button borrowEquipment;
    private Button borrowHistory;
    private Button btnClear;
    private Button btnQR;
    private TextView tvNoBorrowings;
    private RecyclerView recyclerBorrowings;
    private BorrowingAdapter borrowingAdapter;
    private ArrayList<Borrowing> borrowingList;

    private final ActivityResultLauncher<Intent> scannerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            String qrId =
                                    result.getData()
                                            .getStringExtra("QR_ID");

                            if (qrId != null
                                    && !qrId.trim().isEmpty()) {

                                equipmentQR.setText(qrId);

                                loadEquipmentDetails(qrId);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_borrow_equipment);

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

        recyclerBorrowings =
                findViewById(R.id.recyclerBorrowings);

        tvNoBorrowings =
                findViewById(R.id.tvNoBorrowings);

        borrowingList =
                new ArrayList<>();

        viewModel =
                new ViewModelProvider(this)
                        .get(EquipmentViewModel.class);

        equipmentRepository =
                new EquipmentRepository();

        userSession =
                new UserSession(this);

        borrowingRequestViewModel =
                new ViewModelProvider(this)
                        .get(BorrowingRequestViewModel.class);

        equipmentQR =
                findViewById(R.id.equipmentQR);

        borrowEquipment =
                findViewById(R.id.btn_borrow_equipment);

        borrowHistory =
                findViewById(R.id.btn_borrow_history);

        btnClear =
                findViewById(R.id.btn_clear);

        btnQR =
                findViewById(R.id.btn_scan_qr);

        observeViewModels();

        updateBorrowButton();

        setupListeners();

        loadActiveBorrowings();

        setupBorrowingsRecyclerView();
    }

    private void observeViewModels() {

        // Direct borrowing
        viewModel.getMessage()
                .observe(this, message -> {

                    if (message == null
                            || message.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    if (message.equals(
                            "Equipment Successfully Borrowed")) {

                        finish();
                    }
                });

        // Borrowing requests
        borrowingRequestViewModel.getMessage()
                .observe(this, message -> {

                    if (message == null
                            || message.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    if (message.equals(
                            "Borrowing request submitted successfully.")) {

                        finish();
                    }
                });

        borrowingViewModel.getMessage()
                .observe(this, message -> {

                    if (message == null
                            || message.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    if (message.equals(
                            "Equipment checked out successfully."
                    )) {

                        loadActiveBorrowings();
                    }
                });
    }

    private void setupListeners() {

        // Scan QR code
        btnQR.setOnClickListener(v -> {

            Intent qrIntent =
                    new Intent(
                            BorrowEquipmentActivity.this,
                            QRScannerActivity.class
                    );

            scannerLauncher.launch(qrIntent);
        });

        borrowHistory.setOnClickListener(v -> {
            Intent historyIntent = new Intent(BorrowEquipmentActivity.this, BorrowingHistoryActivity.class);
            startActivity(historyIntent);
        });

        // Borrow / Request equipment
        borrowEquipment.setOnClickListener(v -> {

            String qrId =
                    equipmentQR
                            .getText()
                            .toString()
                            .trim();

            if (qrId.isEmpty()) {

                equipmentQR.setError(
                        "Enter Equipment ID"
                );

                return;
            }

            String userId =
                    userSession.getUserId();

            if (userId.isEmpty()) {

                Toast.makeText(
                        this,
                        "User information is missing. Please log in again.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            loadEquipmentDetails(qrId);
        });

        // Clear
        btnClear.setOnClickListener(v ->
                equipmentQR.setText("")
        );
    }

    private void loadEquipmentDetails(String qrId) {

        equipmentRepository.findByQrId(
                qrId,

                snapshot -> {

                    if (snapshot.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Equipment Not Found",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    DocumentSnapshot doc =
                            snapshot.getDocuments().get(0);

                    String name =
                            doc.getString("equipmentName");

                    String model =
                            doc.getString("equipmentModel");

                    String lab =
                            doc.getString("lab");

                    String state =
                            doc.getString("state");

                    String actionText;

                    if (userSession.canBorrowEquipmentDirectly()) {

                        actionText = "Borrow";

                    } else if (userSession.canRequestEquipment()) {

                        actionText = "Request";

                    } else {

                        actionText = "Unavailable";
                    }

                    EquipmentPreviewDialog.show(
                            this,
                            name,
                            model,
                            lab,
                            state,
                            actionText,
                            () -> performEquipmentAction(qrId)
                    );
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    private void updateBorrowButton() {

        if (userSession.canBorrowEquipmentDirectly()) {

            borrowEquipment.setText(
                    getString(R.string.borrow_equipment)
            );

        } else {

            borrowEquipment.setText(
                    "Request Equipment"
            );
        }
    }

    private void performEquipmentAction(String qrId) {

        String userId =
                userSession.getUserId();

        if (userId.isEmpty()) {

            Toast.makeText(
                    this,
                    "User information is missing. Please log in again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (userSession.canBorrowEquipmentDirectly()) {

            viewModel.borrowEquipment(
                    qrId,
                    userId
            );

        } else if (userSession.canRequestEquipment()) {

            borrowingRequestViewModel.requestEquipment(
                    qrId
            );

        } else {

            Toast.makeText(
                    this,
                    "You do not have permission to borrow or request equipment.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void setupBorrowingsRecyclerView() {

        boolean showCheckout =
                userSession.canBorrowEquipmentDirectly();

        boolean showUserName =
                !Constants.ROLE_STUDENT.equalsIgnoreCase(
                        userSession.getRole()
                );

        borrowingAdapter =
                new BorrowingAdapter(
                        borrowingList,
                        showCheckout,
                        showUserName,

                        borrowing -> {

                            if (borrowing == null) {
                                return;
                            }

                            borrowingViewModel.checkout(
                                    borrowing.getId(),
                                    borrowing.getEquipmentId()
                            );
                        }
                );

        recyclerBorrowings.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerBorrowings.setAdapter(
                borrowingAdapter
        );
    }

    private void loadActiveBorrowings() {

        String userId =
                userSession.getUserId();

        String role =
                userSession.getRole();

        if (userId == null
                || userId.trim().isEmpty()) {

            return;
        }

        if (role == null
                || role.trim().isEmpty()) {

            return;
        }

        borrowingViewModel.loadActiveBorrowings(
                userId,
                role,

                borrowings -> {

                    borrowingList.clear();

                    borrowingList.addAll(
                            borrowings
                    );

                    borrowingAdapter.notifyDataSetChanged();

                    if (borrowings.isEmpty()) {

                        tvNoBorrowings.setVisibility(
                                View.VISIBLE
                        );

                        recyclerBorrowings.setVisibility(
                                View.GONE
                        );

                    } else {

                        tvNoBorrowings.setVisibility(
                                View.GONE
                        );

                        recyclerBorrowings.setVisibility(
                                View.VISIBLE
                        );
                    }
                }
        );
    }
}