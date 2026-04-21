package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.Amenity;
import com.oop.project.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'amenities' table.
 * Provides CRUD operations for amenities.
 */
public class AmenityRepository implements DAO {

    // === CREATE ===
    /**
     * Adds a new amenity.
     *
     * @param name the amenity name (e.g., "Swimming Pool")
     * @return the generated amenity ID
     * @throws SQLException if database error occurs or name already exists
     */
    public int create(String name) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot create amenity.");
        }

        String sql = "INSERT INTO amenities (name) VALUES (?)";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new SQLException("Creating amenity failed, no ID obtained.");
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("An amenity with this name already exists.");
        }
    }

    // === READ ===
    /**
     * Retrieves an amenity by its ID.
     */
    public Amenity getById(int amenityId) throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities WHERE amenity_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amenityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAmenity(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves an amenity by its name (case-insensitive).
     */
    public Amenity getByName(String name) throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAmenity(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all amenities, ordered by name.
     */
    public List<Amenity> getAll() throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities ORDER BY name";
        List<Amenity> amenities = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                amenities.add(mapRowToAmenity(rs));
            }
        }
        return amenities;
    }

    /**
     * Searches amenities by name (partial match, case-insensitive).
     */
    public List<Amenity> searchByName(String keyword) throws SQLException {
        String sql = "SELECT amenity_id, name FROM amenities WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        List<Amenity> amenities = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    amenities.add(mapRowToAmenity(rs));
                }
            }
        }
        return amenities;
    }

    // === UPDATE ===
    /**
     * Updates an amenity's name.
     *
     * @param amenityId the ID of the amenity to update
     * @param newName   the new name
     * @throws SQLException if database error occurs or name already exists
     */
    public void update(int amenityId, String newName) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot update amenity.");
        }

        String sql = "UPDATE amenities SET name = ? WHERE amenity_id = ?";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newName);
                stmt.setInt(2, amenityId);
                stmt.executeUpdate();
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("An amenity with this name already exists.");
        }
    }

    // === DELETE ===
    /**
     * Deletes an amenity by its ID.
     * Cascades to apartmentAmenities via database trigger.
     *
     * @param amenityId the ID of the amenity to delete
     * @throws SQLException if database error occurs
     */
    public void delete(int amenityId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot delete amenity.");
        }

        String sql = "DELETE FROM amenities WHERE amenity_id = ?";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, amenityId);
                stmt.executeUpdate();
            }
        }
    }

    // === HELPER ===
    private Amenity mapRowToAmenity(ResultSet rs) throws SQLException {
        return new Amenity(
                rs.getInt("amenity_id"),
                rs.getString("name")
        );
    }
}