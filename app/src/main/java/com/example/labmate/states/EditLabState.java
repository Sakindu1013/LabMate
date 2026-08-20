package com.example.labmate.states;

public class EditLabState {

    public enum Status {
        IDLE,
        LOADING,
        UPDATE_SUCCESS,
        DELETE_SUCCESS,
        ERROR
    }

    private final Status status;
    private final String message;

    private EditLabState(
            Status status,
            String message
    ) {
        this.status = status;
        this.message = message;
    }

    public static EditLabState idle() {

        return new EditLabState(
                Status.IDLE,
                null
        );
    }

    public static EditLabState loading() {

        return new EditLabState(
                Status.LOADING,
                null
        );
    }

    public static EditLabState updateSuccess(
            String message
    ) {

        return new EditLabState(
                Status.UPDATE_SUCCESS,
                message
        );
    }

    public static EditLabState deleteSuccess(
            String message
    ) {

        return new EditLabState(
                Status.DELETE_SUCCESS,
                message
        );
    }

    public static EditLabState error(
            String message
    ) {

        return new EditLabState(
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