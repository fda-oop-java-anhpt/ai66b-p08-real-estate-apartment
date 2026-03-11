package com.oop.project.model;
/*
 * Plain Old Java Object for entities from 'user' table
 */
public class User implements POJO {
    private final String username;
    private final String role;
    
    // Constructor method
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }
    
    // Getter methods
    @Override
    public int getId() {return -1;}  // return -1 as null because 'username' is primary key
    public String getUsername() {return username;}
    public String getRole() {return role;}
    
    
    // No setter methods

  
    // Admin checker
    public boolean isAdmin() {
        return getRole().equalsIgnoreCase("admin");
    }
    
    // toString method
    @Override
    public String toString() {
        return "User(" + getUsername() + ", " + getRole() + ")";
    }
}
