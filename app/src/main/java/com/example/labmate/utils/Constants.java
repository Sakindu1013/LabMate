package com.example.labmate.utils;

public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // Shared Preferences
    public static final String PREF_NAME = "UserPrefs";
    public static final String KEY_NAME = "name";
    public static final String KEY_ROLE = "role";
    public static final String KEY_USER_ID = "userId";

    // Roles
    // Roles
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_STUDENT = "Student";

    // Equipment states
    public static final String STATE_IN_LAB = "In Lab";
    public static final String STATE_BORROWED = "Borrowed";
    public static final String STATE_RESERVED = "Reserved";
    public static final String STATE_MAINTENANCE = "Under Maintenance";
    public static final String STATE_REMOVED = "Removed";

    // Borrowing request states
    public static final String REQUEST_PENDING = "Pending";
    public static final String REQUEST_ACCEPTED = "Accepted";
    public static final String REQUEST_REJECTED = "Rejected";

    // Borrowing states
    public static final String BORROWING_ACTIVE = "Active";
    public static final String BORROWING_BORROWED = "Borrowed";
    public static final String BORROWING_RETURNED = "Returned";
}