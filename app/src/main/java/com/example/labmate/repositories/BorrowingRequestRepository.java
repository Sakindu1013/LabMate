package com.example.labmate.repositories;

import com.example.labmate.models.BorrowingRequest;
import com.example.labmate.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BorrowingRequestRepository {

    private final FirebaseFirestore db;

    public BorrowingRequestRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new borrowing request.
     */
    public void add(
            BorrowingRequest request,
            OnSuccess<String> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .add(request)
                .addOnSuccessListener(
                        documentReference ->
                                onSuccess.onSuccess(
                                        documentReference.getId()
                                )
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Gets all pending borrowing requests.
     */
    public void getPendingRequests(
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .whereEqualTo("status", Constants.REQUEST_PENDING)
                .orderBy(
                        "requestedAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Gets borrowing requests made by a specific user.
     */
    public void getRequestsByUser(
            String userId,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .whereEqualTo("userId", userId)
                .orderBy(
                        "requestedAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Updates the status of a borrowing request.
     */
    public void updateStatus(
            String requestId,
            String status,
            OnComplete onComplete,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .document(requestId)
                .update(
                        "status",
                        status
                )
                .addOnSuccessListener(
                        unused -> onComplete.onComplete()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Show all requests
     */
    public void getAllRequests(
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .orderBy(
                        "requestedAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Checks whether a user already has a pending
     * request for a specific equipment item.
     */
    public void getPendingRequest(
            String equipmentId,
            String userId,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .whereEqualTo("equipmentId", equipmentId)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", Constants.REQUEST_PENDING)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Gets a borrowing request by its Firestore document ID.
     */
    public void getById(
            String requestId,
            OnSuccess<BorrowingRequest> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .document(requestId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        onSuccess.onSuccess(null);
                        return;
                    }

                    BorrowingRequest request =
                            document.toObject(
                                    BorrowingRequest.class
                            );

                    if (request != null) {
                        request.setId(document.getId());
                    }

                    onSuccess.onSuccess(request);
                })
                .addOnFailureListener(onFailure::onFailure);
    }

    public void rejectOtherPendingRequests(
            String equipmentId,
            String acceptedRequestId,
            OnComplete onComplete,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .whereEqualTo("equipmentId", equipmentId)
                .whereEqualTo("status", Constants.REQUEST_PENDING)
                .get()
                .addOnSuccessListener(snapshot -> {

                    com.google.firebase.firestore.WriteBatch batch =
                            db.batch();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : snapshot.getDocuments()) {

                        if (!document.getId().equals(acceptedRequestId)) {

                            batch.update(
                                    document.getReference(),
                                    "status",
                                    Constants.REQUEST_REJECTED
                            );
                        }
                    }

                    batch.commit()
                            .addOnSuccessListener(
                                    unused -> onComplete.onComplete()
                            )
                            .addOnFailureListener(onFailure::onFailure);
                })
                .addOnFailureListener(onFailure::onFailure);
    }

    /**
     * Gets all pending requests for a specific equipment item.
     */
    public void getPendingRequestsForEquipment(
            String equipmentId,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("borrowingRequests")
                .whereEqualTo("equipmentId", equipmentId)
                .whereEqualTo("status", Constants.REQUEST_PENDING)
                .orderBy(
                        "requestedAt",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
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