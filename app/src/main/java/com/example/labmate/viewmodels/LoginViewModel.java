package com.example.labmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmate.models.LoginRequest;
import com.example.labmate.repositories.AuthRepository;
import com.example.labmate.states.LoginState;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<LoginState> loginState =
            new MutableLiveData<>(LoginState.idle());

    private final MutableLiveData<LoginState> passwordResetState =
            new MutableLiveData<>(LoginState.idle());

    private final MutableLiveData<LoginState> verificationEmailState =
            new MutableLiveData<>(LoginState.idle());

    public LoginViewModel() {
        authRepository = new AuthRepository();
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<LoginState> getPasswordResetState() {
        return passwordResetState;
    }

    public LiveData<LoginState> getVerificationEmailState() {
        return verificationEmailState;
    }

    public void login(LoginRequest request) {

        if (request == null) {
            loginState.setValue(
                    LoginState.error("Login information is missing.")
            );
            return;
        }

        loginState.setValue(LoginState.loading());

        authRepository.login(
                request.getEmail(),
                request.getPassword(),

                user -> {

                    if (user == null) {
                        loginState.setValue(
                                LoginState.error("Login failed.")
                        );
                        return;
                    }

                    if (user.isEmailVerified()) {
                        loginState.setValue(
                                LoginState.success(user)
                        );
                    } else {
                        loginState.setValue(
                                LoginState.emailNotVerified(user)
                        );
                    }
                },

                e -> loginState.setValue(
                        LoginState.error("Incorrect email or password.")
                )
        );
    }

    public void resetPassword(String email) {

        if (email == null || email.trim().isEmpty()) {
            passwordResetState.setValue(
                    LoginState.error("Enter your email address.")
            );
            return;
        }

        passwordResetState.setValue(LoginState.loading());

        authRepository.sendPasswordResetEmail(
                email.trim(),

                unused -> passwordResetState.setValue(
                        LoginState.success(null)
                ),

                e -> passwordResetState.setValue(
                        LoginState.error(getErrorMessage(e))
                )
        );
    }

    public void resendVerificationEmail(FirebaseUser user) {

        if (user == null) {
            verificationEmailState.setValue(
                    LoginState.error("User not found.")
            );
            return;
        }

        verificationEmailState.setValue(LoginState.loading());

        authRepository.sendVerificationEmail(
                user,

                unused -> verificationEmailState.setValue(
                        LoginState.success(user)
                ),

                e -> verificationEmailState.setValue(
                        LoginState.error(getErrorMessage(e))
                )
        );
    }

    public void logout() {
        authRepository.logout();
    }

    private String getErrorMessage(Exception e) {

        if (e == null || e.getMessage() == null) {
            return "An unexpected error occurred.";
        }

        return e.getMessage();
    }
}