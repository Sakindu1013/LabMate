package com.example.labmate.models;

public class Equipment {

    private String id;
    private String name;
    private String model;
    private String lab;
    private String state;
    private String type;

    public Equipment(){}

    public Equipment(String id, String name, String model, String lab, String state, String type){
        this.id = id;
        this.name = name;
        this.model = model;
        this.lab = lab;
        this.state = state;
        this.type = type;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getModel() {
        return model;
    }
    public String getLab() {
        return lab;
    }
    public String getState() {
        return state;
    }
    public String getType() {
        return type;
    }
}
