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
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_ACADEMIC = "Academic Staff";
    public static final String ROLE_NON_ACADEMIC = "Non-academic Staff";
    public static final String ROLE_EXTERNAL = "Postgraduate Student";
    public static final String ROLE_UNDERGRAD = "Undergraduate Student";

    // Equipment states
    public static final String STATE_IN_LAB = "In Lab";
    public static final String STATE_BORROWED = "Borrowed";
    public static final String STATE_MAINTENANCE = "Under Maintenance";
    public static final String STATE_REMOVED = "Removed";
}