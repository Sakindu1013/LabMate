package com.example.labmate.repositories;

import com.example.labmate.models.Borrowing;
import com.example.labmate.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BorrowingRepository {

    private final FirebaseFirestore db;

    public BorrowingRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // ============================================================
    // CREATE
    // ============================================================

    public void add(
            Borrowing borrowing,
            EquipmentRepository.OnSuccess<String> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .add(borrowing)
                .addOnSuccessListener(documentReference ->
                        onSuccess.onSuccess(
                                documentReference.getId()
                        )
                )
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    public void getById(
            String borrowingId,
            EquipmentRepository.OnSuccess<DocumentSnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .document(borrowingId)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure
                );
    }

    // ============================================================
    // GET ACTIVE BORROWING BY EQUIPMENT
    // ============================================================

    public void getActiveBorrowingByEquipmentId(
            String equipmentId,
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo(
                        "equipmentId",
                        equipmentId
                )
                .whereEqualTo(
                        "status",
                        Constants.BORROWING_ACTIVE
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // GET USER ACTIVE BORROWINGS
    // ============================================================

    public void getActiveBorrowingsByUserId(
            String userId,
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .whereEqualTo(
                        "status",
                        Constants.BORROWING_ACTIVE
                )
                .orderBy(
                        "createdAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // GET ALL ACTIVE BORROWINGS
    // ============================================================

    public void getActiveBorrowings(
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo(
                        "status",
                        Constants.BORROWING_ACTIVE
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // GET ALL USER BORROWINGS
    // ============================================================

    public void getBorrowingsByUserId(
            String userId,
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .orderBy(
                        "createdAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }


    // ============================================================
    // GET ALL BORROWINGS
    // ============================================================

    public void getAllBorrowings(
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .orderBy(
                        "createdAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // GET USER BORROWED EQUIPMENT
    // ============================================================

    public void getBorrowedByUserId(
            String userId,
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .whereEqualTo(
                        "status",
                        Constants.BORROWING_BORROWED
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // GET BORROWED BORROWING BY EQUIPMENT
    // ============================================================

    public void getBorrowedBorrowingByEquipmentId(
            String equipmentId,
            EquipmentRepository.OnSuccess<QuerySnapshot> onSuccess,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .whereEqualTo(
                        "equipmentId",
                        equipmentId
                )
                .whereEqualTo(
                        "status",
                        Constants.BORROWING_BORROWED
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }


    // ============================================================
    // UPDATE STATUS
    // ============================================================

    public void updateStatus(
            String borrowingId,
            String status,
            EquipmentRepository.OnComplete onComplete,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .document(borrowingId)
                .update("status", status)
                .addOnSuccessListener(
                        unused -> onComplete.onComplete()
                )
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // CHECKOUT
    // ============================================================

    /**
     * Changes:
     *
     * status:
     *      Active -> Borrowed
     *
     * borrowedAt:
     *      null -> Timestamp.now()
     *
     * createdAt:
     *      unchanged
     *
     * returnedAt:
     *      unchanged
     */
    public void checkout(
            String borrowingId,
            EquipmentRepository.OnComplete onComplete,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .document(borrowingId)
                .update(
                        "status",
                        Constants.BORROWING_BORROWED,

                        "borrowedAt",
                        Timestamp.now()
                )
                .addOnSuccessListener(
                        unused -> onComplete.onComplete()
                )
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    // ============================================================
    // RETURN
    // ============================================================

    public void markAsReturned(
            String borrowingId,
            EquipmentRepository.OnComplete onComplete,
            EquipmentRepository.OnFailure onFailure
    ) {

        db.collection("borrowings")
                .document(borrowingId)
                .update(
                        "status",
                        Constants.BORROWING_RETURNED,

                        "returnedAt",
                        Timestamp.now()
                )
                .addOnSuccessListener(
                        unused -> onComplete.onComplete()
                )
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }
}