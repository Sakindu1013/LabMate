package com.example.labmate.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class BorrowingRequest {

    @Exclude
    private String id;

    private String equipmentId;
    private String userId;

    private Timestamp requestedAt;

    private String status;

    @Exclude
    private String equipmentName;

    @Exclude
    private String equipmentModel;

    @Exclude
    private String equipmentQrId;

    @Exclude
    private String userName;

    public BorrowingRequest() {
        // Required empty constructor for Firebase
    }

    public BorrowingRequest(
            String equipmentId,
            String userId,
            Timestamp requestedAt,
            String status
    ) {
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.requestedAt = requestedAt;
        this.status = status;
    }

    // ID
    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    // Equipment ID

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    // User ID

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Requested At

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    // Status

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    @Exclude
    public String getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    @Exclude
    public String getEquipmentQrId() {
        return equipmentQrId;
    }

    public void setEquipmentQrId(String equipmentQrId) {
        this.equipmentQrId = equipmentQrId;
    }

    @Exclude
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}