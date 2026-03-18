package com.oop.project.ui.model;

public class ApartmentRow {
    public final int id;
    public final String address;
    public final String city;
    public final double price;
    public final int bedrooms;
    public final double size;
    public final String category;
    public final String amenities;

    public ApartmentRow(int id, String address, String city, double price, int bedrooms, double size, String category, String amenities) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.price = price;
        this.bedrooms = bedrooms;
        this.size = size;
        this.category = category;
        this.amenities = amenities;
    }
}
