package com.oop.project.model;

import java.sql.Timestamp;

/**
 * Plain Old Java Object for entities from 'users' table.
 */
public class User implements POJO {
    private final String username;
    private final String role;
    private final Timestamp createdAt;
    private final Timestamp lastLogin;

    // Full constructor
    public User(String username, String role, Timestamp createdAt, Timestamp lastLogin) {
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters
    @Override
    public int getId() {
        // Username is the primary key (String), so return -1 as a sentinel.
        return -1;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    // Admin check
    public boolean isAdmin() {
        return getRole().equalsIgnoreCase("admin");
    }

    // toString
    @Override
    public String toString() {
        return "User(" + getUsername() + ", " + getRole() + 
               ", created=" + createdAt + ", lastLogin=" + lastLogin + ")";
    }
}
