package com.example.havefun2_mobile_java.models;

public class Order {
    String RoomId, OrderTime, JoinTime,LeftTime,User;

    public Order(String roomId, String orderTime, String joinTime, String leftTime, String user) {
        RoomId = roomId;
        OrderTime = orderTime;
        JoinTime = joinTime;
        LeftTime = leftTime;
        User = user;
    }

    public Order() {
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getJoinTime() {
        return JoinTime;
    }

    public void setJoinTime(String joinTime) {
        JoinTime = joinTime;
    }

    public String getLeftTime() {
        return LeftTime;
    }

    public void setLeftTime(String leftTime) {
        LeftTime = leftTime;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}
