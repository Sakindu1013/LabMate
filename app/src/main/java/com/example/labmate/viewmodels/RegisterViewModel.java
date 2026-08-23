package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.RegisterRequest;
import com.example.labmate.repositories.AuthRepository;
import com.example.labmate.repositories.UserRepository;
import com.example.labmate.states.RegisterState;
import com.example.labmate.utils.Constants;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<RegisterState> registerState =
            new MutableLiveData<>(RegisterState.idle());

    public RegisterViewModel() {

        authRepository = new AuthRepository();
        userRepository = new UserRepository();
    }

    public LiveData<RegisterState> getRegisterState() {
        return registerState;
    }

    public void register(
            RegisterRequest request,
            String confirmPassword
    ) {

        String validationError =
                validateRequest(
                        request,
                        confirmPassword
                );

        if (validationError != null) {

            registerState.setValue(
                    RegisterState.error(validationError)
            );

            return;
        }

        registerState.setValue(
                RegisterState.loading()
        );

        authRepository.register(
                request.getEmail(),
                request.getPassword(),

                user -> {

                    if (user == null) {

                        registerState.setValue(
                                RegisterState.error(
                                        "Registration failed."
                                )
                        );

                        return;
                    }

                    createUserDocument(
                            user,
                            request
                    );
                },

                e -> registerState.setValue(
                        RegisterState.error(
                                getErrorMessage(e)
                        )
                )
        );
    }

    private String validateRequest(
            RegisterRequest request,
            String confirmPassword
    ) {

        if (request == null) {
            return "Registration information is missing.";
        }

        if (isEmpty(request.getName())
                || isEmpty(request.getEmail())
                || isEmpty(request.getPassword())
                || isEmpty(confirmPassword)
                || isEmpty(request.getMobile())
                || isEmpty(request.getDob())) {

            return "Fill all details";
        }

        if (request.getPassword().length() < 8) {

            return "Password must contain at least 8 characters.";
        }

        if (!request.getPassword()
                .equals(confirmPassword)) {

            return "Password does not match";
        }

        if (!request.getMobile()
                .matches("07[0-9]{8}")) {

            return "Enter valid SL mobile number (07XXXXXXXX)";
        }

        if (!isValidDate(request.getDob())) {

            return "Enter a valid DOB (dd/mm/yyyy)";
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(request.getEmail())
                .matches()) {

            return "Enter a valid email address";
        }

        return null;
    }

    private boolean isValidDate(String date) {

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        sdf.setLenient(false);

        try {

            sdf.parse(date);
            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }

    private void createUserDocument(
            FirebaseUser firebaseUser,
            RegisterRequest request
    ) {

        Map<String, Object> userData =
                new HashMap<>();

        userData.put(
                "name",
                request.getName()
        );

        userData.put(
                "email",
                request.getEmail()
        );

        userData.put(
                "mobile",
                request.getMobile()
        );

        userData.put(
                "dob",
                request.getDob()
        );

        userData.put(
                "createdAt",
                System.currentTimeMillis()
        );

        // Every newly registered user is a Student.
        userData.put(
                "role",
                Constants.ROLE_STUDENT
        );

        userRepository.createUser(
                firebaseUser.getUid(),
                userData,

                () -> sendVerificationEmail(
                        firebaseUser
                ),

                e -> registerState.setValue(
                        RegisterState.error(
                                getErrorMessage(e)
                        )
                )
        );
    }

    private void sendVerificationEmail(
            FirebaseUser user
    ) {

        authRepository.sendVerificationEmail(
                user,

                unused -> registerState.setValue(
                        RegisterState.success(true)
                ),

                e -> registerState.setValue(
                        RegisterState.success(false)
                )
        );
    }

    public void logout() {

        authRepository.logout();
    }

    private String getErrorMessage(Exception e) {

        if (e == null
                || e.getMessage() == null) {

            return "An unexpected error occurred.";
        }

        return e.getMessage();
    }
}