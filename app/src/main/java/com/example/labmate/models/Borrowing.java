package com.example.labmate.models;

import com.google.firebase.Timestamp;

public class Borrowing {

    private String equipmentId;
    private String userId;

    private Timestamp borrowedAt;
    private Timestamp returnedAt;

    private String status;

    public Borrowing() {
        // Required empty constructor for Firebase
    }

    public Borrowing(
            String equipmentId,
            String userId,
            Timestamp borrowedAt,
            String status
    ) {
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.borrowedAt = borrowedAt;
        this.status = status;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(Timestamp borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public Timestamp getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Timestamp returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}