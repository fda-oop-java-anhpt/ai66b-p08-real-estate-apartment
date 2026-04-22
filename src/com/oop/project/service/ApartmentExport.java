package com.oop.project.service;

import com.oop.project.repository.ApartmentRepository;
import com.oop.project.util.SessionManager;
import com.oop.project.model.Apartment;

import java.io.IOException;
import java.sql.SQLException;
import java.io.FileWriter;
import java.util.List;

/**
 * Handles CSV export of apartment data.
 * Available to all logged-in users.
 */
public class ApartmentExport {

    private final ApartmentRepository repository;

    public ApartmentExport() {
        this.repository = new ApartmentRepository();
    }

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in to perform this action.");
        }
    }

    public void exportAllToCSV(String filePath) throws SQLException, IOException, SecurityException {
        requireLogin();
        repository.exportAllToCSV(filePath);
    }

    public void exportFilteredToCSV(String city, Double minPrice, Double maxPrice,
                                    Integer minBedrooms, Integer maxBedrooms,
                                    Double minSize, Double maxSize,
                                    String category, String status,
                                    List<Integer> amenityIds,
                                    String filePath) throws SQLException, IOException, SecurityException {
        requireLogin();
        repository.exportFilteredToCSV(city, minPrice, maxPrice, minBedrooms, maxBedrooms,
                                       minSize, maxSize, category, status, amenityIds, filePath);
    }
    
        public void exportListToCSV(List<Apartment> apartments, String filePath) throws IOException {
            try (FileWriter writer = new FileWriter(filePath)) {
                // Header with Amenities column
                writer.append("ID,Address,City,Price (B VND),Bedrooms,Size (m²),Category,Status,Amenities,Created At,Updated At\n");
                for (Apartment apt : apartments) {
                    writer.append(String.valueOf(apt.getApartmentId())).append(',')
                          .append(escapeCsv(apt.getAddress())).append(',')
                          .append(escapeCsv(apt.getCity())).append(',')
                          .append(String.valueOf(apt.getPrice())).append(',')
                          .append(String.valueOf(apt.getBedrooms())).append(',')
                          .append(String.valueOf(apt.getSize())).append(',')
                          .append(apt.getCategory()).append(',')
                          .append(apt.getStatus()).append(',')
                          .append(escapeCsv(apt.getAmenities())).append(',')   // ← Added
                          .append(apt.getCreatedAt().toString()).append(',')
                          .append(apt.getUpdatedAt().toString()).append('\n');
                }
            }
        }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}