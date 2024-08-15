package com.example.myapplication;

public class ClassModel {
    private String name;
    private String phoneNumber;

    public ClassModel(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}

