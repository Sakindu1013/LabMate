package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.Borrowing;
import com.example.labmate.repositories.BorrowingRepository;
import com.example.labmate.repositories.EquipmentRepository;
import com.example.labmate.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class EquipmentViewModel extends ViewModel {

    private final EquipmentRepository equipmentRepository;
    private final BorrowingRepository borrowingRepository;

    private final MutableLiveData<String> message =
            new MutableLiveData<>();

    public EquipmentViewModel() {

        equipmentRepository = new EquipmentRepository();
        borrowingRepository = new BorrowingRepository();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void borrowEquipment(
            String qrId,
            String userId
    ) {

        if (qrId == null || qrId.trim().isEmpty()) {
            message.setValue("Equipment QR ID is missing.");
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

                    DocumentSnapshot equipment =
                            snapshot.getDocuments().get(0);

                    String state =
                            equipment.getString("state");

                    // Already borrowed
                    if (Constants.STATE_BORROWED.equals(state)) {

                        message.setValue(
                                "This equipment is already borrowed."
                        );

                        return;
                    }

                    // Cannot be borrowed
                    if (Constants.STATE_MAINTENANCE.equals(state)
                            || Constants.STATE_REMOVED.equals(state)) {

                        message.setValue(
                                "This equipment cannot be borrowed."
                        );

                        return;
                    }

                    String equipmentId =
                            equipment.getId();

                    Borrowing borrowing =
                            new Borrowing(
                                    equipmentId,
                                    userId,
                                    Timestamp.now(),
                                    "Active"
                            );

                    // Create borrowing record first.
                    borrowingRepository.add(

                            borrowing,

                            borrowingId -> {

                                // Only update equipment state
                                // after borrowing record is created.
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
}