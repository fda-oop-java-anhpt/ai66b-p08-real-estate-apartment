package com.oop.project.model;
/*
 * Plain Old Java Object for entities from 'amenities' table.
 */
public class Amenity implements POJO {
    private final int id;
    private final String name;
    
    // Constructor method
    public Amenity(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    
    // Getter methods
    @Override
    public int getId() {return id;}
    public String getName() {return name;}
    
    
    // No setter method
    
    
    // toString method
    @Override
    public String toString() {
        return "Amenity(" + getId() + ", " + getName() + ")";
    }
}
