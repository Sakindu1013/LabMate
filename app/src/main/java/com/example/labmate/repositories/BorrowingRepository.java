package com.example.labmate.repositories;

import com.example.labmate.models.Borrowing;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class BorrowingRepository {

    private final FirebaseFirestore db;

    public BorrowingRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void add(
            Borrowing borrowing,
            OnSuccess<String> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowings")
                .add(borrowing)
                .addOnSuccessListener(documentReference ->
                        onSuccess.onSuccess(documentReference.getId())
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    public void getActiveBorrowingByEquipmentId(
            String equipmentId,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo("equipmentId", equipmentId)
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    public void returnBorrowing(
            String equipmentId,
            String userId,
            OnComplete onComplete,
            OnFailure onFailure
    ) {

        getActiveBorrowingByEquipmentAndUser(
                equipmentId,
                userId,

                snapshot -> {

                    if (snapshot.isEmpty()) {

                        onFailure.onFailure(
                                new Exception(
                                        "You did not borrow this equipment."
                                )
                        );

                        return;
                    }

                    String documentId =
                            snapshot.getDocuments()
                                    .get(0)
                                    .getId();

                    db.collection("borrowings")
                            .document(documentId)
                            .update(
                                    "status", "Returned",
                                    "returnedAt", Timestamp.now()
                            )
                            .addOnSuccessListener(
                                    unused ->
                                            onComplete.onComplete()
                            )
                            .addOnFailureListener(
                                    onFailure::onFailure
                            );
                },

                onFailure
        );
    }

    public void getActiveBorrowingByEquipmentAndUser(
            String equipmentId,
            String userId,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo("equipmentId", equipmentId)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(
                        onSuccess::onSuccess
                )
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    public void getActiveBorrowingsByUserId(
            String userId,
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    public void markAsReturned(
            String borrowingDocumentId,
            EquipmentRepository.OnComplete onComplete,
            EquipmentRepository.OnFailure onFailure
    ) {

        Map<String, Object> updates = new HashMap<>();

        updates.put("status", "Returned");
        updates.put("returnedAt", Timestamp.now());

        db.collection("borrowings")
                .document(borrowingDocumentId)
                .update(updates)
                .addOnSuccessListener(unused ->
                        onComplete.onComplete()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    public interface OnSuccess<T> {
        void onSuccess(T result);
    }

    public interface OnFailure {
        void onFailure(Exception e);
    }

    public interface OnComplete {
        void onComplete();
    }
}