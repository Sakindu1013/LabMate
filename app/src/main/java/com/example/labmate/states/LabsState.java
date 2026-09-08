package com.example.labmate.states;

import com.example.labmate.models.Lab;

import java.util.List;

public class LabsState {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final List<Lab> labs;
    private final boolean isAdmin;
    private final boolean isStaff;
    private final String message;

    private LabsState(
            Status status,
            List<Lab> labs,
            boolean isAdmin,
            boolean isStaff,
            String message
    ) {
        this.status = status;
        this.labs = labs;
        this.isAdmin = isAdmin;
        this.isStaff = isStaff;
        this.message = message;
    }

    public static LabsState idle() {

        return new LabsState(
                Status.IDLE,
                null,
                false,
                false,
                null
        );
    }

    public static LabsState loading() {

        return new LabsState(
                Status.LOADING,
                null,
                false,
                false,
                null
        );
    }

    public static LabsState success(
            List<Lab> labs,
            boolean isAdmin,
            boolean isStaff
    ) {

        return new LabsState(
                Status.SUCCESS,
                labs,
                isAdmin,
                isStaff,
                null
        );
    }

    public static LabsState error(
            String message
    ) {

        return new LabsState(
                Status.ERROR,
                null,
                false,
                false,
                message
        );
    }

    public Status getStatus() {
        return status;
    }

    public List<Lab> getLabs() {
        return labs;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    public boolean isStaff() {
        return isStaff;
    }

    public String getMessage() {
        return message;
    }
}