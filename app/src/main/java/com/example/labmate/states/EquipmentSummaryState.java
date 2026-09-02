package com.example.labmate.states;

import com.example.labmate.models.EquipmentSummary;

import java.util.List;

public class EquipmentSummaryState {

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

    private EquipmentSummaryState(
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

    public static EquipmentSummaryState idle() {

        return new EquipmentSummaryState(
                Status.IDLE,
                null,
                0,
                null
        );
    }

    public static EquipmentSummaryState loading() {

        return new EquipmentSummaryState(
                Status.LOADING,
                null,
                0,
                null
        );
    }

    public static EquipmentSummaryState success(
            List<EquipmentSummary> summaries,
            int totalEquipment
    ) {

        return new EquipmentSummaryState(
                Status.SUCCESS,
                summaries,
                totalEquipment,
                null
        );
    }

    public static EquipmentSummaryState error(
            String message
    ) {

        return new EquipmentSummaryState(
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