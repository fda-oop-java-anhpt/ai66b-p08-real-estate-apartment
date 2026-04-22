package com.oop.project.service;

import com.oop.project.model.Note;
import com.oop.project.repository.NoteRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles note operations for apartments.
 * - Add: any logged-in user
 * - Update/Delete: original author or admin only
 * - Read: any logged-in user
 */
public class NoteManagement {

    private final NoteRepository repository;

    public NoteManagement() {
        this.repository = new NoteRepository();
    }

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in to perform this action.");
        }
    }

    // === CREATE ===
    public int addNote(int apartmentId, String content) throws SQLException, SecurityException {
        requireLogin();
        return repository.create(apartmentId, content);
    }

    // === READ ===
    public Note getNoteById(int noteId) throws SQLException, SecurityException {
        requireLogin();
        return repository.getById(noteId);
    }

    public List<Note> getNotesForApartment(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return repository.getByApartment(apartmentId);
    }

    public List<Note> getNotesByUser(String username) throws SQLException, SecurityException {
        requireLogin();
        return repository.getByUser(username);
    }

    public List<Note> getMyNotes() throws SQLException, SecurityException {
        requireLogin();
        return repository.getMyNotes();
    }

    public List<Note> getAllNotes() throws SQLException, SecurityException {
        requireLogin();
        if (!SessionManager.isAdmin()) {
            throw new SecurityException("Only administrators can view all notes.");
        }
        return repository.getAll();
    }

    // === UPDATE ===
    public void updateNote(int noteId, String newContent) throws SQLException, SecurityException {
        requireLogin();
        // NoteRepository already checks ownership/admin; we can add an extra layer here.
        repository.update(noteId, newContent);
    }

    // === DELETE ===
    public void deleteNote(int noteId) throws SQLException, SecurityException {
        requireLogin();
        // NoteRepository enforces that only author or admin can delete.
        repository.delete(noteId);
    }
}