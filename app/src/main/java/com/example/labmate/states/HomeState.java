package com.example.labmate.states;

public class HomeState {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final String name;
    private final String role;
    private final boolean canManageInventory;
    private final boolean admin;
    private final String message;

    private HomeState(
            Status status,
            String name,
            String role,
            boolean canManageInventory,
            boolean admin,
            String message
    ) {
        this.status = status;
        this.name = name;
        this.role = role;
        this.canManageInventory = canManageInventory;
        this.admin = admin;
        this.message = message;
    }

    public static HomeState idle() {

        return new HomeState(
                Status.IDLE,
                null,
                null,
                false,
                false,
                null
        );
    }

    public static HomeState loading() {

        return new HomeState(
                Status.LOADING,
                null,
                null,
                false,
                false,
                null
        );
    }

    public static HomeState success(
            String name,
            String role,
            boolean canManageInventory,
            boolean admin
    ) {

        return new HomeState(
                Status.SUCCESS,
                name,
                role,
                canManageInventory,
                admin,
                null
        );
    }

    public static HomeState error(
            String message
    ) {

        return new HomeState(
                Status.ERROR,
                null,
                null,
                false,
                false,
                message
        );
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean canManageInventory() {
        return canManageInventory;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getMessage() {
        return message;
    }
}