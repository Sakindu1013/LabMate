package com.example.labmate.models;

public class RegisterRequest {

    private final String name;
    private final String email;
    private final String password;
    private final String mobile;
    private final String dob;

    public RegisterRequest(
            String name,
            String email,
            String password,
            String mobile,
            String dob
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDob() {
        return dob;
    }
}