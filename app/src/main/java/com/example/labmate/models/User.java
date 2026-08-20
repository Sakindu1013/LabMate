package com.example.labmate.models;

public class User {

    private String uid;
    private String name;
    private String email;
    private String mobile;
    private String dob;
    private String role;
    private long createdAt;

    public User() {
        // Required for Firebase/Firestore
    }

    public User(
            String uid,
            String name,
            String email,
            String mobile,
            String dob,
            String role,
            long createdAt
    ) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dob = dob;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}