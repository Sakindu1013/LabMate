package com.example.labmate.states;

import com.google.firebase.auth.FirebaseUser;

public class LoginState {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        EMAIL_NOT_VERIFIED,
        ERROR
    }

    private final Status status;
    private final FirebaseUser user;
    private final String message;

    private LoginState(
            Status status,
            FirebaseUser user,
            String message
    ) {
        this.status = status;
        this.user = user;
        this.message = message;
    }

    public static LoginState idle() {
        return new LoginState(Status.IDLE, null, null);
    }

    public static LoginState loading() {
        return new LoginState(Status.LOADING, null, null);
    }

    public static LoginState success(FirebaseUser user) {
        return new LoginState(Status.SUCCESS, user, null);
    }

    public static LoginState emailNotVerified(FirebaseUser user) {
        return new LoginState(
                Status.EMAIL_NOT_VERIFIED,
                user,
                null
        );
    }

    public static LoginState error(String message) {
        return new LoginState(
                Status.ERROR,
                null,
                message
        );
    }

    public Status getStatus() {
        return status;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}