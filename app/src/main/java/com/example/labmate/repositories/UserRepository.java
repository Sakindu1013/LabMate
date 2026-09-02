package com.example.labmate.repositories;

import com.example.labmate.models.User;
import com.example.labmate.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                .addOnSuccessListener(
                        unused -> onSuccess.onSuccess()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    public void createGoogleUser(
            String uid,
            String name,
            String email,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {

        Map<String, Object> userData = new HashMap<>();

        userData.put(
                "name",
                name != null ? name : ""
        );

        userData.put(
                "email",
                email != null ? email : ""
        );

        userData.put("mobile", "");
        userData.put("dob", "");
        userData.put(
                "createdAt",
                System.currentTimeMillis()
        );
        userData.put(
                "role",
                Constants.ROLE_STUDENT
        );

        createUser(
                uid,
                userData,
                onSuccess,
                onFailure
        );
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
                .addOnFailureListener(onFailure::onFailure);
    }

    public void getById(
            String userId,
            OnDocumentSuccess onSuccess,
            OnFailure onFailure
    ) {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(onSuccess::onSuccess)
                .addOnFailureListener(onFailure::onFailure);
    }

    public void getAllUsers(
            OnUsersSuccess onSuccess,
            OnFailure onFailure
    ) {

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<User> users = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        User user = document.toObject(User.class);

                        if (user != null) {

                            user.setUid(document.getId());

                            users.add(user);
                        }
                    }

                    onSuccess.onSuccess(users);
                })
                .addOnFailureListener(onFailure::onFailure);
    }

    public void updateUserRole(
            String uid,
            String newRole,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {

        db.collection("users")
                .document(uid)
                .update("role", newRole)
                .addOnSuccessListener(
                        unused -> onSuccess.onSuccess()
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    public void updateUserProfile(
            String uid,
            String name,
            String mobile,
            String dob,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {

        Map<String, Object> updates = new HashMap<>();

        if (name != null && !name.trim().isEmpty()) {
            updates.put("name", name.trim());
        }

        if (mobile != null && !mobile.trim().isEmpty()) {
            updates.put("mobile", mobile.trim());
        }

        if (dob != null && !dob.trim().isEmpty()) {
            updates.put("dob", dob.trim());
        }

        // Nothing to update
        if (updates.isEmpty()) {
            onSuccess.onSuccess();
            return;
        }

        db.collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(
                        unused -> onSuccess.onSuccess()
                )
                .addOnFailureListener(onFailure::onFailure);
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

    public interface OnDocumentSuccess {

        void onSuccess(DocumentSnapshot document);
    }

    public interface OnUsersSuccess {

        void onSuccess(java.util.List<User> users);
    }
}