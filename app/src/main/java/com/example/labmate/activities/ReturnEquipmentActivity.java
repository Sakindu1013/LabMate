package com.example.labmate.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReturnEquipmentActivity extends AppCompatActivity {

    private AutoCompleteTextView actEquipmentQR;

    private Button returnEquipment;
    private Button btnClear;

    private BorrowingRepository borrowingRepository;
    private EquipmentRepository equipmentRepository;

    private ArrayList<String> qrIds;
    private ArrayAdapter<String> adapter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return_equipment);

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

        borrowingRepository = new BorrowingRepository();
        equipmentRepository = new EquipmentRepository();
        auth = FirebaseAuth.getInstance();

        actEquipmentQR = findViewById(R.id.actEquipmentQR);

        returnEquipment =
                findViewById(R.id.btn_return_equipment);

        btnClear =
                findViewById(R.id.btn_clear);

        qrIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                qrIds
        );

        actEquipmentQR.setAdapter(adapter);

        loadBorrowedEquipment();

        returnEquipment.setOnClickListener(v -> {

            String qrId =
                    actEquipmentQR
                            .getText()
                            .toString()
                            .trim();

            if (qrId.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select equipment.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            returnEquipment(qrId);
        });

        btnClear.setOnClickListener(v ->
                actEquipmentQR.setText("")
        );
    }

    /**
     * Loads equipment currently borrowed by the logged-in user.
     */
    private void loadBorrowedEquipment() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "User not found.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String userId = user.getUid();

        borrowingRepository.getActiveBorrowingsByUserId(
                userId,

                snapshot -> {

                    qrIds.clear();

                    for (DocumentSnapshot borrowingDoc :
                            snapshot.getDocuments()) {

                        String equipmentId =
                                borrowingDoc.getString("equipmentId");

                        if (equipmentId == null) {
                            continue;
                        }

                        loadEquipmentQR(equipmentId);
                    }

                    adapter.notifyDataSetChanged();
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    /**
     * Gets the QR ID of an equipment document.
     */
    private void loadEquipmentQR(String equipmentId) {

        equipmentRepository.getById(
                equipmentId,

                equipment -> {

                    if (equipment == null) {
                        return;
                    }

                    String qrId = equipment.getQrId();

                    if (qrId != null && !qrId.trim().isEmpty()) {

                        qrIds.add(qrId);
                        adapter.notifyDataSetChanged();
                    }
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    /**
     * Returns the selected equipment.
     */
    private void returnEquipment(String qrId) {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "User not found.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String userId = user.getUid();

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

                    verifyBorrowing(
                            equipmentId,
                            userId
                    );
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    /**
     * Makes sure the selected equipment was actually
     * borrowed by the currently logged-in user.
     */
    private void verifyBorrowing(
            String equipmentId,
            String userId
    ) {

        borrowingRepository.getActiveBorrowingByEquipmentId(
                equipmentId,

                snapshot -> {

                    DocumentSnapshot borrowingDoc = null;

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        String borrowingUserId =
                                doc.getString("userId");

                        if (userId.equals(borrowingUserId)) {

                            borrowingDoc = doc;
                            break;
                        }
                    }

                    if (borrowingDoc == null) {

                        Toast.makeText(
                                this,
                                "You did not borrow this equipment.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    completeReturn(
                            equipmentId,
                            borrowingDoc.getId()
                    );
                },

                e -> Toast.makeText(
                        this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    /**
     * Completes both parts of the return:
     *
     * 1. Equipment → In Lab
     * 2. Borrowing → Returned
     */
    private void completeReturn(
            String equipmentId,
            String borrowingDocumentId
    ) {

        equipmentRepository.updateState(
                equipmentId,
                Constants.STATE_IN_LAB,

                () -> {

                    borrowingRepository.markAsReturned(
                            borrowingDocumentId,

                            () -> {

                                Toast.makeText(
                                        this,
                                        "Equipment Successfully Returned",
                                        Toast.LENGTH_LONG
                                ).show();

                                finish();
                            },

                            e -> Toast.makeText(
                                    this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
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