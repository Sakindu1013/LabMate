package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.repositories.BorrowingRequestRepository;
import com.example.labmate.utils.Constants;
import com.google.firebase.Timestamp;

public class RequestEquipmentViewModel extends ViewModel {

    private final BorrowingRequestRepository repository;

    private final MutableLiveData<String> message =
            new MutableLiveData<>();

    public RequestEquipmentViewModel() {
        repository = new BorrowingRequestRepository();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void requestEquipment(
            String equipmentId,
            String userId
    ) {

        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            message.setValue("Equipment information is missing.");
            return;
        }

        if (userId == null || userId.trim().isEmpty()) {
            message.setValue("User information is missing.");
            return;
        }

        BorrowingRequest request =
                new BorrowingRequest(
                        equipmentId,
                        userId,
                        Timestamp.now(),
                        Constants.REQUEST_PENDING
                );

        repository.add(
                request,

                requestId -> message.setValue(
                        "Equipment request submitted successfully."
                ),

                e -> message.setValue(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to submit equipment request."
                )
        );
    }
}