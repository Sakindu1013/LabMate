package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.repositories.UserRepository;

import java.util.Map;

public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<Map<String, Object>> userData =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> updateSuccess =
            new MutableLiveData<>();

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    public ProfileViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Map<String, Object>> getUserData() {
        return userData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadUser(String uid) {

        loading.setValue(true);
        error.setValue(null);

        userRepository.getUser(
                uid,
                data -> {

                    loading.postValue(false);

                    if (data != null) {

                        userData.postValue(data);

                    } else {

                        error.postValue(
                                "User profile not found."
                        );
                    }
                },
                e -> {

                    loading.postValue(false);

                    error.postValue(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Failed to load profile."
                    );
                }
        );
    }

    public void updateProfile(
            String uid,
            String name,
            String mobile,
            String dob
    ) {

        loading.setValue(true);
        error.setValue(null);
        updateSuccess.setValue(null);

        userRepository.updateUserProfile(
                uid,
                name,
                mobile,
                dob,
                () -> {

                    loading.postValue(false);
                    updateSuccess.postValue(true);

                },
                e -> {

                    loading.postValue(false);

                    error.postValue(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Failed to update profile."
                    );
                }
        );
    }
}