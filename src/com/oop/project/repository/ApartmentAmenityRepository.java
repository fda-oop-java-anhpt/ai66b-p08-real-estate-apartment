package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.Amenity;
import com.oop.project.model.ApartmentAmenity;
import com.oop.project.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'apartmentAmenities' junction table.
 * Provides operations to manage amenities linked to apartments.
 */
public class ApartmentAmenityRepository implements DAO {

    // === ADD ===
    public void addAmenityToApartment(int apartmentId, int amenityId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in.");
        }

        String sql = "INSERT INTO apartmentAmenities (apartment_id, amenity_id) VALUES (?, ?)";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                stmt.setInt(2, amenityId);
                stmt.executeUpdate();
            }
        }
    }

    // === REMOVE ===
    public void removeAmenityFromApartment(int apartmentId, int amenityId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in.");
        }

        String sql = "DELETE FROM apartmentAmenities WHERE apartment_id = ? AND amenity_id = ?";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentId);
                stmt.setInt(2, amenityId);
                stmt.executeUpdate();
            }
        }
    }

    // === READ ===
    public List<Integer> getAmenityIdsForApartment(int apartmentId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT amenity_id FROM apartmentAmenities WHERE apartment_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("amenity_id"));
                }
            }
        }
        return ids;
    }

    public List<Amenity> getAmenitiesForApartment(int apartmentId) throws SQLException {
        List<Amenity> amenities = new ArrayList<>();
        String sql = "SELECT a.amenity_id, a.name FROM amenities a " +
                     "JOIN apartmentAmenities aa ON a.amenity_id = aa.amenity_id " +
                     "WHERE aa.apartment_id = ? ORDER BY a.name";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    amenities.add(new Amenity(rs.getInt("amenity_id"), rs.getString("name")));
                }
            }
        }
        return amenities;
    }

    public List<ApartmentAmenity> getAll() throws SQLException {
        List<ApartmentAmenity> list = new ArrayList<>();
        String sql = "SELECT apartment_id, amenity_id FROM apartmentAmenities";
        try (Connection conn = new DBConnection().establish();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ApartmentAmenity(rs.getInt("apartment_id"), rs.getInt("amenity_id")));
            }
        }
        return list;
    }
}