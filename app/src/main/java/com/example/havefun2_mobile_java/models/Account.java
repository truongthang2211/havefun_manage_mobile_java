package com.example.havefun2_mobile_java.models;

public class Account {
    private String id;
    private String fullName;
    private String hotelName;
    private boolean manager;
    public Account(String id, String fullName,String hotelName, boolean manager) {
        this.id = id;
        this.fullName = fullName;
        this.hotelName=hotelName;
        this.manager = manager;
    }
    public Account() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
    public boolean isManager() {
        return manager;
    }
    public void setManager(boolean manager) {
        this.manager = manager;
    }
}
