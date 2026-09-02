package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.User;
import com.example.labmate.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageUserViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<List<User>> users =
            new MutableLiveData<>();

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> roleUpdateSuccess =
            new MutableLiveData<>();

    private final List<User> allUsers =
            new ArrayList<>();

    public ManageUserViewModel() {

        userRepository = new UserRepository();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getRoleUpdateSuccess() {
        return roleUpdateSuccess;
    }

    public void loadUsers(
            String currentAdminUid
    ) {

        userRepository.getAllUsers(
                loadedUsers -> {

                    allUsers.clear();

                    for (User user : loadedUsers) {

                        // Do not show the currently logged-in Admin
                        if (!user.getUid().equals(
                                currentAdminUid
                        )) {

                            allUsers.add(user);
                        }
                    }

                    // Show all users initially
                    users.postValue(
                            new ArrayList<>(allUsers)
                    );
                },

                exception -> {

                    error.postValue(
                            exception.getMessage()
                    );
                }
        );
    }

    public void searchUsers(
            String searchText
    ) {

        if (searchText == null) {
            searchText = "";
        }

        String query =
                searchText.trim().toLowerCase();

        List<User> filteredUsers =
                new ArrayList<>();

        if (query.isEmpty()) {

            filteredUsers.addAll(
                    allUsers
            );

        } else {

            for (User user : allUsers) {

                String email =
                        user.getEmail();

                if (email != null &&
                        email.toLowerCase()
                                .contains(query)) {

                    filteredUsers.add(user);
                }
            }
        }

        users.setValue(
                filteredUsers
        );
    }

    public void updateUserRole(
            String uid,
            String newRole
    ) {

        userRepository.updateUserRole(
                uid,
                newRole,

                () -> {

                    roleUpdateSuccess.postValue(
                            true
                    );
                },

                exception -> {

                    roleUpdateSuccess.postValue(
                            false
                    );

                    error.postValue(
                            exception.getMessage()
                    );
                }
        );
    }
}