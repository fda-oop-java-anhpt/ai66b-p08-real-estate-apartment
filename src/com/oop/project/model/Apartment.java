package com.oop.project.model;
/*
 * Plain Old Java Object for entities from 'apartment' table.
 */
public class Apartment implements POJO {
    private final int id;
    private final String address;
    private final String city;
    private final double price;
    private final int bedrooms;
    private final double size;
    private final String category;
    
    
    // Constructor method
    public Apartment(
            int id, String address, String city,
            double price, int bedrooms, double size,
            String category)
    {
        this.id = id;
        this.address = address;
        this.city = city;
        this.price = price;
        this.bedrooms = bedrooms;
        this.size = size;
        this.category = category;
    }
    
    
    // Getter methods
    @Override
    public int getId() {return id;}
    public String getAddress() {return address;}
    public String getCity() {return city;}
    public double getPrice() {return price;}
    public int getBedrooms() {return bedrooms;}
    public double getSize() {return size;}
    public String getCategory() {return category;}
    
    
    // No setter method
  
  
    // toString method
    @Override
    public String toString() {
        return "Apartment(" + getId() + ", " + getAddress() + ", " + getCity() + ", " + getPrice() + ", " + getBedrooms() + ", " + getSize() + ", " + getCategory() + ")";
    }
}
