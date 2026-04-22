package com.oop.project.model;

import java.sql.Timestamp;

public class Apartment implements POJO {
    private final int apartmentId;
    private final String address;
    private final String city;
    private final double price;
    private final int bedrooms;
    private final double size;
    private final String category;
    private final String status;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;
    private final String amenities;   // NEW FIELD

    // Full constructor – ADD amenities parameter at the end
    public Apartment(int apartmentId, String address, String city, double price,
                     int bedrooms, double size, String category, String status,
                     Timestamp createdAt, Timestamp updatedAt, String amenities) {
        this.apartmentId = apartmentId;
        this.address = address;
        this.city = city;
        this.price = price;
        this.bedrooms = bedrooms;
        this.size = size;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.amenities = amenities;    // NEW ASSIGNMENT
    }

    // Getters
    public int getApartmentId() {
        return apartmentId;
    }
    
    @Override
    public int getId() {
        return getApartmentId();
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public double getPrice() {
        return price;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public double getSize() {
        return size;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public String getAmenities() {
        return (amenities == null || amenities.trim().isEmpty()) ? "None" : amenities;
    }
    
    @Override
    public String toString() {
        return "";
    }
}
