package com.example.labmate.models;

public class Lab {

    private String labId;
    private String name;
    private String inCharge;
    private String location;

    public Lab(){}

    public Lab(String labId, String name, String inCharge, String location){
        this.labId = labId;
        this.name = name;
        this.inCharge = inCharge;
        this.location = location;
    }

    public String getLabId() {
        return labId;
    }

    public String getName(){
        return name;
    }
    public String getInCharge(){
        return inCharge;
    }
    public String getLocation(){
        return location;
    }

}
