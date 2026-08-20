package com.example.labmate.repositories;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UserRepository {

    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void createUser(
            String uid,
            Map<String, Object> userData,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {
        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(unused -> onSuccess.onSuccess())
                .addOnFailureListener(onFailure::onFailure);
    }

    public void getUser(
            String uid,
            OnUserSuccess onSuccess,
            OnFailure onFailure
    ) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        onSuccess.onSuccess(
                                documentSnapshot.getData()
                        );

                    } else {

                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(
                        onFailure::onFailure
                );
    }

    public interface OnSuccess {
        void onSuccess();
    }

    public interface OnFailure {
        void onFailure(Exception e);
    }

    public interface OnUserSuccess {
        void onSuccess(Map<String, Object> userData);
    }
}