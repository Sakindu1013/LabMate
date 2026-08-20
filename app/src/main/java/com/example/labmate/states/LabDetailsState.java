package com.example.labmate.states;

import com.example.labmate.models.EquipmentSummary;

import java.util.List;

public class LabDetailsState {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final List<EquipmentSummary> summaries;
    private final int totalEquipment;
    private final String message;

    private LabDetailsState(
            Status status,
            List<EquipmentSummary> summaries,
            int totalEquipment,
            String message
    ) {
        this.status = status;
        this.summaries = summaries;
        this.totalEquipment = totalEquipment;
        this.message = message;
    }

    public static LabDetailsState idle() {

        return new LabDetailsState(
                Status.IDLE,
                null,
                0,
                null
        );
    }

    public static LabDetailsState loading() {

        return new LabDetailsState(
                Status.LOADING,
                null,
                0,
                null
        );
    }

    public static LabDetailsState success(
            List<EquipmentSummary> summaries,
            int totalEquipment
    ) {

        return new LabDetailsState(
                Status.SUCCESS,
                summaries,
                totalEquipment,
                null
        );
    }

    public static LabDetailsState error(
            String message
    ) {

        return new LabDetailsState(
                Status.ERROR,
                null,
                0,
                message
        );
    }

    public Status getStatus() {
        return status;
    }

    public List<EquipmentSummary> getSummaries() {
        return summaries;
    }

    public int getTotalEquipment() {
        return totalEquipment;
    }

    public String getMessage() {
        return message;
    }
}