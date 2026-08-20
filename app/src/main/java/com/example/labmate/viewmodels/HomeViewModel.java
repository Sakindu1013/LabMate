package com.example.labmate.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labmate.repositories.AuthRepository;
import com.example.labmate.repositories.UserRepository;
import com.example.labmate.states.HomeState;
import com.example.labmate.utils.UserSession;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class HomeViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final UserSession userSession;

    private final MutableLiveData<HomeState> homeState =
            new MutableLiveData<>(HomeState.idle());

    public HomeViewModel(
            @NonNull Application application
    ) {
        super(application);

        authRepository = new AuthRepository();
        userRepository = new UserRepository();

        userSession =
                new UserSession(application);
    }

    public LiveData<HomeState> getHomeState() {
        return homeState;
    }

    public void loadUserData() {

        homeState.setValue(
                HomeState.loading()
        );

        FirebaseUser user =
                authRepository.getCurrentUser();

        if (user == null) {

            homeState.setValue(
                    HomeState.error(
                            "User not found."
                    )
            );

            return;
        }

        userRepository.getUser(
                user.getUid(),

                userData -> {

                    if (userData == null) {

                        homeState.setValue(
                                HomeState.error(
                                        "User data not found."
                                )
                        );

                        return;
                    }

                    String name =
                            (String) userData.get("name");

                    String role =
                            (String) userData.get("role");

                    if (name == null) {
                        name = "Unknown";
                    }

                    if (role == null) {
                        role = "No Role";
                    }

                    // Save user information locally.
                    userSession.saveUser(
                            user.getUid(),
                            name,
                            role
                    );

                    homeState.setValue(
                            HomeState.success(
                                    name,
                                    role,
                                    userSession.isAdmin(),
                                    userSession.canManageInventory()
                            )
                    );
                },

                e -> homeState.setValue(
                        HomeState.error(
                                "Failed to load data."
                        )
                )
        );
    }
}