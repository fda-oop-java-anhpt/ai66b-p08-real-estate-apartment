package com.oop.project.service;

import com.oop.project.model.Apartment;
import com.oop.project.repository.ApartmentRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles search and filter operations for apartments.
 * Available to all logged-in users.
 */
public class ApartmentSearch {

    private final ApartmentRepository repository;

    public ApartmentSearch() {
        this.repository = new ApartmentRepository();
    }

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in to perform this action.");
        }
    }

    public List<Apartment> searchByKeyword(String keyword) throws SQLException, SecurityException {
        requireLogin();
        return repository.search(keyword);
    }

    public List<Apartment> filterApartments(String city, Double minPrice, Double maxPrice,
                                            Integer minBedrooms, Integer maxBedrooms,
                                            Double minSize, Double maxSize,
                                            String category, String status,
                                            List<Integer> amenityIds)
            throws SQLException, SecurityException {
        requireLogin();
        return repository.filter(city, minPrice, maxPrice, minBedrooms, maxBedrooms,
                minSize, maxSize, category, status, amenityIds);
    }
}