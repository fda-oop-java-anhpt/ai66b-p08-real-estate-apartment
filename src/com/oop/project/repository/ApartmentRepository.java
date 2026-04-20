   package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.Apartment;
import com.oop.project.model.Amenity;
import com.oop.project.util.ApartmentCategorizer;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApartmentRepository implements DAO {

    // === CREATE ===
    public int create(String address, String city, double price, int bedrooms,
                      double size, String status, List<Integer> amenityIds) throws SQLException {
        String category = ApartmentCategorizer.categorize(price, size);
        Connection conn = null;
        try {
            conn = new DBConnection().establish();
            conn.setAutoCommit(false);

            int apartmentId;
            String sql = "INSERT INTO apartment (address, city, price, bedrooms, size, category, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertApt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                insertApt.setString(1, address);
                insertApt.setString(2, city);
                insertApt.setDouble(3, price);
                insertApt.setInt(4, bedrooms);
                insertApt.setDouble(5, size);
                insertApt.setString(6, category);
                insertApt.setString(7, status);
                insertApt.executeUpdate();

                try (ResultSet generatedKeys = insertApt.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException("Creating apartment failed, no ID obtained.");
                    }
                    apartmentId = generatedKeys.getInt(1);
                }
            }

            // Insert amenities
            if (amenityIds != null && !amenityIds.isEmpty()) {
                String amenitySql = "INSERT INTO apartmentAmenities (apartment_id, amenity_id) VALUES (?, ?)";
                try (PreparedStatement insertAmenity = conn.prepareStatement(amenitySql)) {
                    for (int amenityId : amenityIds) {
                        insertAmenity.setInt(1, apartmentId);
                        insertAmenity.setInt(2, amenityId);
                        insertAmenity.addBatch();
                    }
                    insertAmenity.executeBatch();
                }
            }

            conn.commit();
            return apartmentId;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    // === READ ===
    public Apartment getById(int apartmentId) throws SQLException {
        String sql = "SELECT a.*, GROUP_CONCAT(am.name SEPARATOR ', ') AS amenities " +
                     "FROM apartment a " +
                     "LEFT JOIN apartmentAmenities aa ON a.apartment_id = aa.apartment_id " +
                     "LEFT JOIN amenities am ON aa.amenity_id = am.amenity_id " +
                     "WHERE a.apartment_id = ? " +
                     "GROUP BY a.apartment_id";

        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToApartment(rs);
                }
            }
        }
        return null;
    }

    public List<Apartment> getAll() throws SQLException {
        String sql = "SELECT a.*, GROUP_CONCAT(am.name SEPARATOR ', ') AS amenities " +
                     "FROM apartment a " +
                     "LEFT JOIN apartmentAmenities aa ON a.apartment_id = aa.apartment_id " +
                     "LEFT JOIN amenities am ON aa.amenity_id = am.amenity_id " +
                     "GROUP BY a.apartment_id " +
                     "ORDER BY a.created_at DESC";

        List<Apartment> apartments = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                apartments.add(mapRowToApartment(rs));
            }
        }
        return apartments;
    }

    // === UPDATE ===
    public void update(int apartmentId, String address, String city, double price,
                       int bedrooms, double size, String status,
                       List<Integer> amenityIds) throws SQLException {
        String category = ApartmentCategorizer.categorize(price, size);
        Connection conn = null;
        try {
            conn = new DBConnection().establish();
            conn.setAutoCommit(false);

            // Update apartment
            String aptSql = "UPDATE apartment SET address = ?, city = ?, price = ?, " +
                            "bedrooms = ?, size = ?, category = ?, status = ? " +
                            "WHERE apartment_id = ?";
            try (PreparedStatement updateApt = conn.prepareStatement(aptSql)) {
                updateApt.setString(1, address);
                updateApt.setString(2, city);
                updateApt.setDouble(3, price);
                updateApt.setInt(4, bedrooms);
                updateApt.setDouble(5, size);
                updateApt.setString(6, category);
                updateApt.setString(7, status);
                updateApt.setInt(8, apartmentId);
                updateApt.executeUpdate();
            }

            // Delete existing amenities
            try (PreparedStatement deleteAmenities = conn.prepareStatement(
                    "DELETE FROM apartmentAmenities WHERE apartment_id = ?")) {
                deleteAmenities.setInt(1, apartmentId);
                deleteAmenities.executeUpdate();
            }

            // Insert new amenities
            if (amenityIds != null && !amenityIds.isEmpty()) {
                String insSql = "INSERT INTO apartmentAmenities (apartment_id, amenity_id) VALUES (?, ?)";
                try (PreparedStatement insertAmenities = conn.prepareStatement(insSql)) {
                    for (int amenityId : amenityIds) {
                        insertAmenities.setInt(1, apartmentId);
                        insertAmenities.setInt(2, amenityId);
                        insertAmenities.addBatch();
                    }
                    insertAmenities.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    // === DELETE ===
    public void delete(int apartmentId) throws SQLException {
        String sql = "DELETE FROM apartment WHERE apartment_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            stmt.executeUpdate();
        }
    }

    // === FILTER ===
    public List<Apartment> filter(String city, Double minPrice, Double maxPrice,
                                  Integer minBedrooms, Integer maxBedrooms,
                                  Double minSize, Double maxSize,
                                  String category, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT a.*, GROUP_CONCAT(am.name SEPARATOR ', ') AS amenities " +
            "FROM apartment a " +
            "LEFT JOIN apartmentAmenities aa ON a.apartment_id = aa.apartment_id " +
            "LEFT JOIN amenities am ON aa.amenity_id = am.amenity_id " +
            "WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (city != null && !city.trim().isEmpty()) {
            sql.append("AND a.city LIKE ? ");
            params.add("%" + city + "%");
        }
        if (minPrice != null) {
            sql.append("AND a.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND a.price <= ? ");
            params.add(maxPrice);
        }
        if (minBedrooms != null) {
            sql.append("AND a.bedrooms >= ? ");
            params.add(minBedrooms);
        }
        if (maxBedrooms != null) {
            sql.append("AND a.bedrooms <= ? ");
            params.add(maxBedrooms);
        }
        if (minSize != null) {
            sql.append("AND a.size >= ? ");
            params.add(minSize);
        }
        if (maxSize != null) {
            sql.append("AND a.size <= ? ");
            params.add(maxSize);
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append("AND a.category = ? ");
            params.add(category);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND a.status = ? ");
            params.add(status);
        }

        sql.append("GROUP BY a.apartment_id ORDER BY a.price ASC");

        List<Apartment> apartments = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apartments.add(mapRowToApartment(rs));
                }
            }
        }
        return apartments;
    }

    // === SEARCH ===
    public List<Apartment> search(String keyword) throws SQLException {
        String sql = "SELECT a.*, GROUP_CONCAT(am.name SEPARATOR ', ') AS amenities " +
                     "FROM apartment a " +
                     "LEFT JOIN apartmentAmenities aa ON a.apartment_id = aa.apartment_id " +
                     "LEFT JOIN amenities am ON aa.amenity_id = am.amenity_id " +
                     "WHERE a.address LIKE ? OR a.city LIKE ? " +
                     "GROUP BY a.apartment_id " +
                     "ORDER BY a.created_at DESC";

        List<Apartment> apartments = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apartments.add(mapRowToApartment(rs));
                }
            }
        }
        return apartments;
    }

    // === CSV EXPORT ===
    public void exportAllToCSV(String filePath) throws SQLException, IOException {
        List<Apartment> apartments = getAll();
        exportToCSV(apartments, filePath);
    }

    public void exportFilteredToCSV(String city, Double minPrice, Double maxPrice,
                                    Integer minBedrooms, Integer maxBedrooms,
                                    Double minSize, Double maxSize,
                                    String category, String status,
                                    String filePath) throws SQLException, IOException {
        List<Apartment> apartments = filter(city, minPrice, maxPrice, minBedrooms, maxBedrooms,
                                            minSize, maxSize, category, status);
        exportToCSV(apartments, filePath);
    }

    private void exportToCSV(List<Apartment> apartments, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("ID,Address,City,Price (B VND),Bedrooms,Size (m²),Category,Status,Created At,Updated At\n");
            for (Apartment apt : apartments) {
                writer.append(String.valueOf(apt.getApartmentId())).append(',')
                      .append(escapeCsv(apt.getAddress())).append(',')
                      .append(escapeCsv(apt.getCity())).append(',')
                      .append(String.valueOf(apt.getPrice())).append(',')
                      .append(String.valueOf(apt.getBedrooms())).append(',')
                      .append(String.valueOf(apt.getSize())).append(',')
                      .append(apt.getCategory()).append(',')
                      .append(apt.getStatus()).append(',')
                      .append(apt.getCreatedAt().toString()).append(',')
                      .append(apt.getUpdatedAt().toString()).append('\n');
            }
        }
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private Apartment mapRowToApartment(ResultSet rs) throws SQLException {
        return new Apartment(
            rs.getInt("apartment_id"),
            rs.getString("address"),
            rs.getString("city"),
            rs.getDouble("price"),
            rs.getInt("bedrooms"),
            rs.getDouble("size"),
            rs.getString("category"),
            rs.getString("status"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }

    // === AMENITY HELPERS ===
    public List<Amenity> getAllAmenities() throws SQLException {
        List<Amenity> amenities = new ArrayList<>();
        String sql = "SELECT * FROM amenities ORDER BY name";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                amenities.add(new Amenity(rs.getInt("amenity_id"), rs.getString("name")));
            }
        }
        return amenities;
    }

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
}
