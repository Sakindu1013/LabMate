package com.example.labmate.states;

public class RegisterState {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final boolean verificationEmailSent;
    private final String message;

    private RegisterState(
            Status status,
            boolean verificationEmailSent,
            String message
    ) {
        this.status = status;
        this.verificationEmailSent = verificationEmailSent;
        this.message = message;
    }

    public static RegisterState idle() {
        return new RegisterState(
                Status.IDLE,
                false,
                null
        );
    }

    public static RegisterState loading() {
        return new RegisterState(
                Status.LOADING,
                false,
                null
        );
    }

    public static RegisterState success(
            boolean verificationEmailSent
    ) {
        return new RegisterState(
                Status.SUCCESS,
                verificationEmailSent,
                null
        );
    }

    public static RegisterState error(String message) {
        return new RegisterState(
                Status.ERROR,
                false,
                message
        );
    }

    public Status getStatus() {
        return status;
    }

    public boolean isVerificationEmailSent() {
        return verificationEmailSent;
    }

    public String getMessage() {
        return message;
    }
}