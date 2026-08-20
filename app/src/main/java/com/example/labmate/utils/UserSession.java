package com.example.labmate.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private final SharedPreferences preferences;

    public UserSession(Context context) {
        preferences = context.getSharedPreferences(
                Constants.PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public String getName() {
        return preferences.getString(
                Constants.KEY_NAME,
                ""
        );
    }

    public String getRole() {
        return preferences.getString(
                Constants.KEY_ROLE,
                ""
        );
    }

    public String getUserId() {
        return preferences.getString(
                Constants.KEY_USER_ID,
                ""
        );
    }

    public void saveUser(
            String userId,
            String name,
            String role
    ) {

        preferences.edit()
                .putString(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_NAME, name)
                .putString(Constants.KEY_ROLE, role)
                .apply();
    }

    public void clear() {
        preferences.edit()
                .clear()
                .apply();
    }

    public boolean isAdmin() {
        return Constants.ROLE_ADMIN.equalsIgnoreCase(
                getRole()
        );
    }

    public boolean canManageInventory() {

        String role = getRole();

        return Constants.ROLE_ADMIN.equalsIgnoreCase(role)
                || Constants.ROLE_ACADEMIC.equalsIgnoreCase(role)
                || Constants.ROLE_NON_ACADEMIC.equalsIgnoreCase(role);
    }
}