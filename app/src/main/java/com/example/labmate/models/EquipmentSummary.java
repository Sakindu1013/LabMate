package com.example.labmate.models;

public class EquipmentSummary {
    private String type;
    private int total;
    private int inLab;
    private int borrowed;
    private int maintenance;
    private int removed;

    public EquipmentSummary(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public int getInLab() {
        return inLab;
    }

    public int getBorrowed() {
        return borrowed;
    }

    public int getMaintenance() {
        return maintenance;
    }

    public int getRemoved() {
        return removed;
    }

    public int getTotal() {
        return total;
    }
    public void increaseTotal(){
        total++;
    }
    public void increaseInLab(){
        inLab++;
    }
    public void increaseBorrowed(){
        borrowed++;
    }
    public void increaseMaintenance(){
        maintenance++;
    }
    public void increaseRemoved(){
        removed++;
    }
}
