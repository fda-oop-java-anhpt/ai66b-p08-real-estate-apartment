package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.Apartment;
import com.oop.project.model.Favourite;
import com.oop.project.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'favourites' table.
 * Provides operations to add, remove, and retrieve favorite apartments.
 */
public class FavouriteRepository implements DAO {

    // === ADD ===
    /**
     * Adds an apartment to the current user's favorites.
     *
     * @param apartmentId the apartment ID to favorite
     * @throws SQLException if already favorited or database error
     */
    public void add(int apartmentId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot add favorite.");
        }

        String sql = "INSERT INTO favourites (username, apartment_id) VALUES (?, ?)";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setInt(2, apartmentId);
                stmt.executeUpdate();
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("Apartment already in favorites.");
        }
    }

    // === REMOVE ===
    /**
     * Removes an apartment from the current user's favorites.
     *
     * @param apartmentId the apartment ID to unfavorite
     * @throws SQLException if database error occurs
     */
    public void remove(int apartmentId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot remove favorite.");
        }

        String sql = "DELETE FROM favourites WHERE username = ? AND apartment_id = ?";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setInt(2, apartmentId);
                stmt.executeUpdate();
            }
        }
    }

    // === CHECK ===
    /**
     * Checks if an apartment is favorited by the current user.
     *
     * @param apartmentId the apartment ID
     * @return true if favorited, false otherwise
     * @throws SQLException on database error
     */
    public boolean isFavorited(int apartmentId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            return false;
        }

        String sql = "SELECT 1 FROM favourites WHERE username = ? AND apartment_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, apartmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // === READ ===
    /**
     * Retrieves all favorite apartments for the current user with full apartment details.
     *
     * @return list of Apartment objects
     * @throws SQLException on database error
     */
    public List<Apartment> getMyFavorites() throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            return new ArrayList<>();
        }
        return getFavoritesByUsername(username);
    }

    /**
     * Retrieves all favorite apartments for a given username.
     *
     * @param username the username
     * @return list of Apartment objects
     * @throws SQLException on database error
     */
    public List<Apartment> getFavoritesByUsername(String username) throws SQLException {
        String sql = "SELECT a.*, GROUP_CONCAT(am.name SEPARATOR ', ') AS amenities " +
                "FROM favourites f " +
                "JOIN apartment a ON f.apartment_id = a.apartment_id " +
                "LEFT JOIN apartmentAmenities aa ON a.apartment_id = aa.apartment_id " +
                "LEFT JOIN amenities am ON aa.amenity_id = am.amenity_id " +
                "WHERE f.username = ? " +
                "GROUP BY a.apartment_id " +
                "ORDER BY f.created_at DESC";

        List<Apartment> apartments = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                ApartmentRepository aptRepo = new ApartmentRepository();
                while (rs.next()) {
                    apartments.add(aptRepo.mapRowToApartment(rs)); // using package-private helper
                }
            }
        }
        return apartments;
    }

    /**
     * Retrieves all favorite entries (without apartment details) for the current user.
     *
     * @return list of Favourite objects
     * @throws SQLException on database error
     */
    public List<Favourite> getMyFavouriteEntries() throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT username, apartment_id, created_at FROM favourites WHERE username = ? ORDER BY created_at DESC";
        List<Favourite> favourites = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    favourites.add(new Favourite(
                            rs.getString("username"),
                            rs.getInt("apartment_id"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        }
        return favourites;
    }

    /**
     * Toggles the favorite status of an apartment for the current user.
     * If already favorited, removes it; otherwise, adds it.
     *
     * @param apartmentId the apartment ID
     * @return true if added, false if removed
     * @throws SQLException on database error
     */
    public boolean toggle(int apartmentId) throws SQLException {
        if (isFavorited(apartmentId)) {
            remove(apartmentId);
            return false;
        } else {
            add(apartmentId);
            return true;
        }
    }
}