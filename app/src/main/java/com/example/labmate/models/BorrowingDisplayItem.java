package com.example.labmate.models;

import com.google.firebase.Timestamp;

public class BorrowingDisplayItem {

    private String borrowingId;

    private String equipmentName;
    private String equipmentModel;
    private String equipmentQrId;

    private String userName;

    private Timestamp createdAt;
    private Timestamp borrowedAt;
    private Timestamp returnedAt;

    private String status;

    public BorrowingDisplayItem() {
    }

    public String getBorrowingId() {
        return borrowingId;
    }

    public void setBorrowingId(String borrowingId) {
        this.borrowingId = borrowingId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public String getEquipmentQrId() {
        return equipmentQrId;
    }

    public void setEquipmentQrId(String equipmentQrId) {
        this.equipmentQrId = equipmentQrId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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