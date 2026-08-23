package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.Borrowing;
import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.repositories.BorrowingRepository;
import com.example.labmate.repositories.BorrowingRequestRepository;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class EquipmentViewModel extends ViewModel {

    private final EquipmentRepository equipmentRepository;
    private final BorrowingRepository borrowingRepository;
    private final BorrowingRequestRepository requestRepository;

    private final MutableLiveData<String> message =
            new MutableLiveData<>();

    public EquipmentViewModel() {

        equipmentRepository =
                new EquipmentRepository();

        borrowingRepository =
                new BorrowingRepository();

        requestRepository =
                new BorrowingRequestRepository();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    /**
     * Directly borrows equipment.
     *
     * Used by Admin and Non-Academic Staff.
     */
    public void borrowEquipment(
            String qrId,
            String userId
    ) {

        if (qrId == null || qrId.trim().isEmpty()) {
            message.setValue("Equipment ID is missing.");
            return;
        }

        if (userId == null || userId.trim().isEmpty()) {
            message.setValue("User information is missing.");
            return;
        }

        equipmentRepository.findByQrId(
                qrId.trim(),

                snapshot -> {

                    if (snapshot.isEmpty()) {
                        message.setValue("Equipment Not Found");
                        return;
                    }

                    DocumentSnapshot doc =
                            snapshot.getDocuments().get(0);

                    String state =
                            doc.getString("state");

                    if (Constants.STATE_BORROWED.equals(state)) {

                        message.setValue(
                                "This equipment is already borrowed."
                        );

                        return;
                    }

                    if (Constants.STATE_MAINTENANCE.equals(state)
                            || Constants.STATE_REMOVED.equals(state)) {

                        message.setValue(
                                "This equipment cannot be borrowed."
                        );

                        return;
                    }

                    String equipmentId =
                            doc.getId();

                    Timestamp borrowedAt =
                            Timestamp.now();

                    Timestamp now = Timestamp.now();

                    Borrowing borrowing =
                            new Borrowing(
                                    equipmentId,
                                    userId,
                                    now,
                                    now,
                                    null,
                                    Constants.BORROWING_BORROWED
                            );

                    borrowingRepository.add(
                            borrowing,

                            borrowingId -> {

                                equipmentRepository.updateState(
                                        equipmentId,
                                        Constants.STATE_BORROWED,

                                        () -> message.setValue(
                                                "Equipment Successfully Borrowed"
                                        ),

                                        e -> message.setValue(
                                                e.getMessage()
                                        )
                                );
                            },

                            e -> message.setValue(
                                    e.getMessage()
                            )
                    );
                },

                e -> message.setValue(
                        e.getMessage()
                )
        );
    }

    /**
     * Creates a borrowing request.
     *
     * Used by students.
     */
    public void requestEquipment(
            String qrId,
            String userId
    ) {

        if (qrId == null || qrId.trim().isEmpty()) {
            message.setValue("Equipment ID is missing.");
            return;
        }

        if (userId == null || userId.trim().isEmpty()) {
            message.setValue("User information is missing.");
            return;
        }

        equipmentRepository.findByQrId(
                qrId.trim(),

                snapshot -> {

                    if (snapshot.isEmpty()) {
                        message.setValue("Equipment Not Found");
                        return;
                    }

                    DocumentSnapshot doc =
                            snapshot.getDocuments().get(0);

                    String state =
                            doc.getString("state");

                    if (Constants.STATE_BORROWED.equals(state)) {

                        message.setValue(
                                "This equipment is already borrowed."
                        );

                        return;
                    }

                    if (Constants.STATE_MAINTENANCE.equals(state)
                            || Constants.STATE_REMOVED.equals(state)) {

                        message.setValue(
                                "This equipment cannot be requested."
                        );

                        return;
                    }

                    String equipmentId =
                            doc.getId();

                    BorrowingRequest request =
                            new BorrowingRequest(
                                    equipmentId,
                                    userId,
                                    Timestamp.now(),
                                    "Pending"
                            );

                    requestRepository.add(
                            request,

                            requestId -> message.setValue(
                                    "Borrowing request submitted successfully."
                            ),

                            e -> message.setValue(
                                    e.getMessage()
                            )
                    );
                },

                e -> message.setValue(
                        e.getMessage()
                )
        );
    }
}