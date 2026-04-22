package com.oop.project.service;

import com.oop.project.model.Apartment;
import com.oop.project.repository.ApartmentRepository;
import com.oop.project.util.SessionManager;
import com.oop.project.model.Note;
import com.oop.project.service.ApartmentAmenity;
import com.oop.project.model.Amenity;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles core CRUD operations for apartments.
 * - Create: Admin only
 * - Delete: Admin only
 * - Update: Admin or Agent
 * - Read: Any logged-in user
 */
public class ApartmentManagement {

    private final ApartmentRepository repository;
    private final NoteManagement noteManagement;
    private final ApartmentAmenity amenityService = new ApartmentAmenity();

    public ApartmentManagement() {
        this.repository = new ApartmentRepository();
        this.noteManagement = new NoteManagement();
    }

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in to perform this action.");
        }
    }

    private void requireAdmin() throws SecurityException {
        requireLogin();
        if (!SessionManager.isAdmin()) {
            throw new SecurityException("Only administrators can perform this action.");
        }
    }

    // === CREATE (Admin only) ===
    public int createApartment(String address, String city, double price, int bedrooms,
                               double size, String status, List<Integer> amenityIds)
            throws SQLException, SecurityException {
        requireAdmin();
        return repository.create(address, city, price, bedrooms, size, status, amenityIds);
    }

    // === READ ===
    public Apartment getApartmentById(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return repository.getById(apartmentId);
    }

    public List<Apartment> getAllApartments() throws SQLException, SecurityException {
        requireLogin();
        return repository.getAll();
    }

    // === UPDATE (Both admin and agent) ===
    public void updateApartment(int apartmentId, String address, String city, double price,
                                int bedrooms, double size, String status,
                                List<Integer> amenityIds) throws SQLException, SecurityException {
        requireLogin(); // Agents can update apartment status, etc.
        repository.update(apartmentId, address, city, price, bedrooms, size, status, amenityIds);
    }

    // === DELETE (Admin only) ===
    public void deleteApartment(int apartmentId) throws SQLException, SecurityException {
        requireAdmin();
        repository.delete(apartmentId);
    }

    /**
     * Retrieves all notes associated with a specific apartment.
     * Delegates to NoteManagement service.
     */
    public List<Note> getNotesForApartment(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return noteManagement.getNotesForApartment(apartmentId);
    }

    /**
     * Adds a note to an apartment.
     */
    public int addNoteToApartment(int apartmentId, String content) throws SQLException, SecurityException {
        requireLogin();
        return noteManagement.addNote(apartmentId, content);
    }

    /**
     * Deletes a note (author or admin only).
     */
    public void deleteNote(int noteId) throws SQLException, SecurityException {
        requireLogin();
        noteManagement.deleteNote(noteId);
    }

    /**
     * Updates a note (author or admin only).
     */
    public void updateNote(int noteId, String newContent) throws SQLException, SecurityException {
        requireLogin();
        noteManagement.updateNote(noteId, newContent);
    }
    
    public List<Integer> getAmenityIdsForApartment(int apartmentId) throws SQLException, SecurityException {
    requireLogin();
    return amenityService.getAmenityIdsForApartment(apartmentId);
}

    public List<Amenity> getAmenitiesForApartment(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return amenityService.getAmenitiesForApartment(apartmentId);
    }
}