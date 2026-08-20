package com.example.labmate.states;

public class AddLabState {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final String message;

    private AddLabState(
            Status status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    public static AddLabState idle() {
        return new AddLabState(
                Status.IDLE,
                null
        );
    }

    public static AddLabState loading() {
        return new AddLabState(
                Status.LOADING,
                null
        );
    }

    public static AddLabState success(
            String message
    ) {
        return new AddLabState(
                Status.SUCCESS,
                message
        );
    }

    public static AddLabState error(
            String message
    ) {
        return new AddLabState(
                Status.ERROR,
                message
        );
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}