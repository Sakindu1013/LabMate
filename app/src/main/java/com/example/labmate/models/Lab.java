package com.example.labmate.models;

public class Lab {
    private String name;
    private String inCharge;
    private String location;

    public Lab(){}

    public Lab(String name, String inCharge, String location){
        this.name = name;
        this.inCharge = inCharge;
        this.location = location;
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
