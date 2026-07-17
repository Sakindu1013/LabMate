package com.example.labmate.models;

public class Equipment {

    private String equipmentName;
    private String equipmentModel;
    private String lab;
    private String state;
    private String qrId;

    public Equipment(){}

    public String getEquipmentName(){
        return equipmentName;
    }
    public String getEquipmentModel() {
        return equipmentModel;
    }

    public String getLab() {
        return lab;
    }

    public String getState() {
        return state;
    }

    public String getQrId() {
        return qrId;
    }
}
