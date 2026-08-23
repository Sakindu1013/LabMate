package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labmate.R;
import com.example.labmate.repositories.BorrowingRepository;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.utils.Constants;
import com.example.labmate.utils.UserSession;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.DocumentSnapshot;

public class ReturnEquipmentActivity extends AppCompatActivity {

    private android.widget.EditText equipmentQR;

    private Button returnEquipment;
    private Button btnClear;

    private BorrowingRepository borrowingRepository;
    private EquipmentRepository equipmentRepository;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_return_equipment
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

        borrowingRepository =
                new BorrowingRepository();

        equipmentRepository =
                new EquipmentRepository();

        equipmentQR =
                findViewById(R.id.actEquipmentQR);

        returnEquipment =
                findViewById(R.id.btn_return_equipment);

        btnClear =
                findViewById(R.id.btn_clear);

        userSession =
                new UserSession(this);

        if (!userSession.canManageInventory()) {

            Toast.makeText(
                    this,
                    "You do not have permission to return equipment.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // ========================================================
        // RETURN EQUIPMENT
        // ========================================================

        returnEquipment.setOnClickListener(v -> {

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

            returnEquipment(qrId);
        });

        // ========================================================
        // CLEAR
        // ========================================================

        btnClear.setOnClickListener(v ->
                equipmentQR.setText("")
        );
    }

    // ============================================================
    // RETURN EQUIPMENT
    // ============================================================

    private void returnEquipment(String qrId) {

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

                    DocumentSnapshot equipmentDoc =
                            snapshot.getDocuments().get(0);

                    String equipmentId =
                            equipmentDoc.getId();

                    String state =
                            equipmentDoc.getString("state");

                    if (!Constants.STATE_BORROWED.equals(state)) {

                        Toast.makeText(
                                this,
                                "This equipment is not currently borrowed.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    findBorrowing(
                            equipmentId
                    );
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to find equipment.",
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    // ============================================================
    // FIND CURRENT BORROWING
    // ============================================================

    private void findBorrowing(
            String equipmentId
    ) {

        borrowingRepository.getBorrowedBorrowingByEquipmentId(
                equipmentId,

                snapshot -> {

                    if (snapshot.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No active borrowing found for this equipment.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    DocumentSnapshot borrowingDoc =
                            snapshot.getDocuments().get(0);

                    String borrowingId =
                            borrowingDoc.getId();

                    completeReturn(
                            equipmentId,
                            borrowingId
                    );
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to find borrowing.",
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    // ============================================================
    // COMPLETE RETURN
    // ============================================================

    private void completeReturn(
            String equipmentId,
            String borrowingId
    ) {

        WriteBatch batch =
                FirebaseFirestore
                        .getInstance()
                        .batch();

        DocumentReference borrowingRef =
                FirebaseFirestore
                        .getInstance()
                        .collection("borrowings")
                        .document(borrowingId);

        DocumentReference equipmentRef =
                FirebaseFirestore
                        .getInstance()
                        .collection("equipment")
                        .document(equipmentId);

        batch.update(
                borrowingRef,
                "status",
                Constants.BORROWING_RETURNED,

                "returnedAt",
                Timestamp.now()
        );

        batch.update(
                equipmentRef,
                "state",
                Constants.STATE_IN_LAB
        );

        batch.commit()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Equipment Successfully Returned",
                            Toast.LENGTH_LONG
                    ).show();

                    equipmentQR.setText("");
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Failed to return equipment.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}