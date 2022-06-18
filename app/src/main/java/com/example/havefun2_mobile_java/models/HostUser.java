package com.example.havefun2_mobile_java.models;

public class HostUser extends User {
    String hotel_id;

    public HostUser(String hotel_id) {
        this.hotel_id = hotel_id;
    }
    public HostUser() {
    }
    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

}
