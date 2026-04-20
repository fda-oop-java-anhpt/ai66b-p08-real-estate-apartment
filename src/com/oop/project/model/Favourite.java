// Favourite.java
package com.oop.project.model;

import java.sql.Timestamp;

/**
 * Plain Old Java Object for the 'favourites' table (composite primary key).
 */
public class Favourite implements POJO {
    private final String username;
    private final int apartmentId;
    private final Timestamp createdAt;

    public Favourite(String username, int apartmentId, Timestamp createdAt) {
        this.username = username;
        this.apartmentId = apartmentId;
        this.createdAt = createdAt;
    }

    @Override
    public int getId() {
        // Composite key – returning -1 as convention (similar to User)
        return -1;
    }

    public String getUsername() {
        return username;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Favourite(" + username + ", apt " + apartmentId + ")";
    }
}