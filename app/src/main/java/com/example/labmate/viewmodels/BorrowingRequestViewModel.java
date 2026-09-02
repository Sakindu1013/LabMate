package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.repositories.AuthRepository;
import com.example.labmate.repositories.BorrowingRequestRepository;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class BorrowingRequestViewModel extends ViewModel {

    private final BorrowingRequestRepository requestRepository;
    private final EquipmentRepository equipmentRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<String> message = new MutableLiveData<>();

    public BorrowingRequestViewModel() {

        requestRepository = new BorrowingRequestRepository();
        equipmentRepository = new EquipmentRepository();
        authRepository = new AuthRepository();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    // ============================================================
    // STUDENT - CREATE BORROWING REQUEST
    // ============================================================

    public void requestEquipment(String qrId) {

        if (qrId == null || qrId.trim().isEmpty()) {

            message.setValue(
                    "Please enter an equipment QR ID."
            );

            return;
        }

        FirebaseUser user = authRepository.getCurrentUser();

        if (user == null) {

            message.setValue(
                    "User not found."
            );

            return;
        }

        String userId = user.getUid();

        equipmentRepository.findByQrId(
                qrId.trim(),

                snapshot -> {

                    if (snapshot.isEmpty()) {

                        message.setValue(
                                "Equipment Not Found"
                        );

                        return;
                    }

                    DocumentSnapshot equipment =
                            snapshot.getDocuments().get(0);

                    String state = equipment.getString("state");

                    /*
                     * A student can only request equipment
                     * that is currently physically in the lab.
                     *
                     * Reserved equipment cannot be requested.
                     * Borrowed equipment cannot be requested.
                     * Equipment under maintenance cannot be requested.
                     * Removed equipment cannot be requested.
                     */
                    if (!Constants.STATE_IN_LAB.equals(state)) {

                        message.setValue(
                                "This equipment is not available for requests."
                        );

                        return;
                    }

                    validateExistingRequest(
                            equipment.getId(),
                            userId
                    );
                },

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to find equipment."
                )
        );
    }

    // ============================================================
    // VALIDATE EXISTING REQUEST
    // ============================================================

    private void validateExistingRequest(
            String equipmentId,
            String userId
    ) {

        requestRepository.getPendingRequest(
                equipmentId,
                userId,

                snapshot -> {

                    if (!snapshot.isEmpty()) {

                        message.setValue(
                                "You already have a pending request for this equipment."
                        );

                        return;
                    }

                    createBorrowingRequest(
                            equipmentId,
                            userId
                    );
                },

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to check existing requests."
                )
        );
    }

    // ============================================================
    // CREATE REQUEST
    // ============================================================

    private void createBorrowingRequest(
            String equipmentId,
            String userId
    ) {

        BorrowingRequest request =
                new BorrowingRequest(
                        equipmentId,
                        userId,
                        Timestamp.now(),
                        Constants.REQUEST_PENDING
                );

        requestRepository.add(
                request,

                requestId -> message.setValue(
                        "Borrowing request submitted successfully."
                ),

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to submit borrowing request."
                )
        );
    }
}