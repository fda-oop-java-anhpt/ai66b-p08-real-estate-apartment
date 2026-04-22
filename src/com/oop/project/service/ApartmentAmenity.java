package com.oop.project.service;

import com.oop.project.model.Amenity;
import com.oop.project.repository.ApartmentAmenityRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing amenities linked to apartments.
 * Available to all logged-in users (since it's used by ApartmentPanel for editing).
 */
public class ApartmentAmenity {

    private final ApartmentAmenityRepository repository;

    public ApartmentAmenity() {
        this.repository = new ApartmentAmenityRepository();
    }

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in to perform this action.");
        }
    }

    public List<Integer> getAmenityIdsForApartment(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return repository.getAmenityIdsForApartment(apartmentId);
    }

    public List<Amenity> getAmenitiesForApartment(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return repository.getAmenitiesForApartment(apartmentId);
    }

    public void addAmenityToApartment(int apartmentId, int amenityId) throws SQLException, SecurityException {
        requireLogin();
        repository.addAmenityToApartment(apartmentId, amenityId);
    }

    public void removeAmenityFromApartment(int apartmentId, int amenityId) throws SQLException, SecurityException {
        requireLogin();
        repository.removeAmenityFromApartment(apartmentId, amenityId);
    }

    public List<Amenity> getAllAmenities() throws SQLException, SecurityException {
        requireLogin();
        // This is actually in ApartmentRepository, but we can also delegate to AmenityRepository.
        // For simplicity, we'll call ApartmentRepository's method.
        return new com.oop.project.repository.ApartmentRepository().getAllAmenities();
    }
}