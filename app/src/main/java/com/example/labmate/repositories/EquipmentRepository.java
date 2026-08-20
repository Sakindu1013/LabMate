package com.example.labmate.repositories;

import com.example.labmate.models.Equipment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class EquipmentRepository {

    private final FirebaseFirestore db;

    public EquipmentRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAll(
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(e -> onFailure.onFailure(e));
    }

    public void findByQrId(
            String qrId,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .whereEqualTo("qrId", qrId)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(e -> onFailure.onFailure(e));
    }

    public void getByType(
            String type,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(e -> onFailure.onFailure(e));
    }

    public void getByTypeAndLab(
            String type,
            String lab,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .whereEqualTo("type", type)
                .whereEqualTo("lab", lab)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(e -> onFailure.onFailure(e));
    }

    public void getByLab(
            String lab,
            OnSuccess<QuerySnapshot> onSuccess,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .whereEqualTo("lab", lab)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(e -> onFailure.onFailure(e));
    }

    public void add(
            Equipment equipment,
            OnSuccess<DocumentSnapshot> onSuccess,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .add(equipment)
                .addOnSuccessListener(documentReference ->
                        documentReference.get()
                                .addOnSuccessListener(onSuccess::onSuccess)
                                .addOnFailureListener(onFailure::onFailure)
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    public void updateState(
            String documentId,
            String state,
            OnComplete onComplete,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .document(documentId)
                .update("state", state)
                .addOnSuccessListener(unused -> onComplete.onComplete())
                .addOnFailureListener(onFailure::onFailure);
    }

    public void updateLabAndState(
            String documentId,
            String lab,
            String state,
            OnComplete onComplete,
            OnFailure onFailure
    ) {
        db.collection("equipment")
                .document(documentId)
                .update(
                        "lab", lab,
                        "state", state
                )
                .addOnSuccessListener(unused -> onComplete.onComplete())
                .addOnFailureListener(onFailure::onFailure);
    }

    public void getById(
            String documentId,
            OnSuccess<Equipment> onSuccess,
            OnFailure onFailure
    ) {

        db.collection("equipment")
                .document(documentId)
                .get()
                .addOnSuccessListener(document -> {

                    Equipment equipment =
                            document.toObject(Equipment.class);

                    onSuccess.onSuccess(equipment);
                })
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