package com.example.labmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.labmate.R;
import com.example.labmate.dialogs.EquipmentPreviewDialog;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.utils.UserSession;
import com.example.labmate.viewmodels.EquipmentViewModel;
import com.google.firebase.firestore.DocumentSnapshot;

public class BorrowEquipmentActivity extends AppCompatActivity {

    private EquipmentViewModel viewModel;
    private EquipmentRepository equipmentRepository;
    private UserSession userSession;

    private EditText equipmentQR;
    private Button borrowEquipment;
    private Button btnClear;
    private Button btnQR;

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

        viewModel =
                new ViewModelProvider(this)
                        .get(EquipmentViewModel.class);

        equipmentRepository =
                new EquipmentRepository();

        userSession =
                new UserSession(this);

        equipmentQR =
                findViewById(R.id.equipmentQR);

        borrowEquipment =
                findViewById(R.id.btn_borrow_equipment);

        btnClear =
                findViewById(R.id.btn_clear);

        btnQR =
                findViewById(R.id.btn_scan_qr);

        observeViewModel();

        setupListeners();
    }

    private void observeViewModel() {

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

        // Borrow equipment
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

            viewModel.borrowEquipment(
                    qrId,
                    userId
            );
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

                    EquipmentPreviewDialog.show(
                            this,
                            name,
                            model,
                            lab,
                            state,
                            "Borrow",
                            () -> {

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

                                viewModel.borrowEquipment(
                                        qrId,
                                        userId
                                );
                            }
                    );
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }
}