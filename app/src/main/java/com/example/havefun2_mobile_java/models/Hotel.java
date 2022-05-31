package com.example.havefun2_mobile_java.models;

import java.time.LocalDateTime;

public class Hotel {
    String id,name,description;
    String [] imgs;
    com.example.havefun.models.Rating[] ratings;
    com.example.havefun.models.Room[] rooms;
    Promotion[] promotions;
    LocalDateTime created_at;
    com.example.havefun.models.Location location;

    public Hotel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public com.example.havefun.models.Rating[] getRatings() {
        return ratings;
    }

    public void setRatings(com.example.havefun.models.Rating[] ratings) {
        this.ratings = ratings;
    }

    public com.example.havefun.models.Room[] getRooms() {
        return rooms;
    }

    public void setRooms(com.example.havefun.models.Room[] rooms) {
        this.rooms = rooms;
    }

    public Promotion[] getPromotions() {
        return promotions;
    }

    public void setPromotions(Promotion[] promotions) {
        this.promotions = promotions;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public com.example.havefun.models.Location getLocation() {
        return location;
    }

    public void setLocation(com.example.havefun.models.Location location) {
        this.location = location;
    }

    public Hotel(String id, String name, String description, String[] imgs, com.example.havefun.models.Rating[] ratings, com.example.havefun.models.Room[] rooms, Promotion[] promotions, LocalDateTime created_at, com.example.havefun.models.Location location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imgs = imgs;
        this.ratings = ratings;
        this.rooms = rooms;
        this.promotions = promotions;
        this.created_at = created_at;
        this.location = location;
    }
}
