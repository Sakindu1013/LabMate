package com.example.labmate.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public void login(
            String email,
            String password,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> onSuccess.onSuccess(result.getUser()))
                .addOnFailureListener(onFailure::onFailure);
    }

    public void sendPasswordResetEmail(
            String email,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure::onFailure);
    }

    public void sendVerificationEmail(
            FirebaseUser user,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {
        user.sendEmailVerification()
                .addOnSuccessListener(unused -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure::onFailure);
    }

    public void logout() {
        auth.signOut();
    }

    public void register(
            String email,
            String password,
            OnSuccess onSuccess,
            OnFailure onFailure
    ) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result ->
                        onSuccess.onSuccess(result.getUser())
                )
                .addOnFailureListener(onFailure::onFailure);
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public interface OnSuccess {
        void onSuccess(FirebaseUser user);
    }

    public interface OnFailure {
        void onFailure(Exception e);
    }
}