package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.Note;
import com.oop.project.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'notes' table.
 * Provides CRUD operations for notes.
 */
public class NoteRepository implements DAO {

    // === CREATE ===
    /**
     * Adds a new note to an apartment by the current user.
     *
     * @param apartmentId the apartment ID
     * @param content     the note content
     * @return the generated note ID
     * @throws SQLException on database error
     */
    public int create(int apartmentId, String content) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot create note.");
        }

        String sql = "INSERT INTO notes (username, apartment_id, content) VALUES (?, ?, ?)";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setInt(2, apartmentId);
                stmt.setString(3, content);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new SQLException("Creating note failed, no ID obtained.");
                }
            }
        }
    }

    // === READ ===
    /**
     * Retrieves a note by its ID.
     */
    public Note getById(int noteId) throws SQLException {
        String sql = "SELECT note_id, username, apartment_id, content, created_at, updated_at " +
                "FROM notes WHERE note_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToNote(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all notes for a specific apartment.
     */
    public List<Note> getByApartment(int apartmentId) throws SQLException {
        String sql = "SELECT note_id, username, apartment_id, content, created_at, updated_at " +
                "FROM notes WHERE apartment_id = ? ORDER BY created_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapRowToNote(rs));
                }
            }
        }
        return notes;
    }

    /**
     * Retrieves all notes written by a specific user.
     */
    public List<Note> getByUser(String username) throws SQLException {
        String sql = "SELECT note_id, username, apartment_id, content, created_at, updated_at " +
                "FROM notes WHERE username = ? ORDER BY created_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapRowToNote(rs));
                }
            }
        }
        return notes;
    }

    /**
     * Retrieves all notes written by the currently logged-in user.
     */
    public List<Note> getMyNotes() throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            return new ArrayList<>();
        }
        return getByUser(username);
    }

    /**
     * Retrieves all notes in the system.
     */
    public List<Note> getAll() throws SQLException {
        String sql = "SELECT note_id, username, apartment_id, content, created_at, updated_at " +
                "FROM notes ORDER BY created_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notes.add(mapRowToNote(rs));
            }
        }
        return notes;
    }

    // === UPDATE ===
    /**
     * Updates the content of an existing note.
     * Only the original author (or an admin) should be allowed to update.
     */
    public void update(int noteId, String newContent) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot update note.");
        }

        Connection conn = null;
        try {
            conn = new DBConnection().establish();
            DBConnection.setCurrentUser(conn, username);
            conn.setAutoCommit(false);

            // Verify ownership or admin role
            String checkSql = "SELECT username FROM notes WHERE note_id = ?";
            String noteOwner;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, noteId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Note not found.");
                    }
                    noteOwner = rs.getString("username");
                }
            }

            // Allow if current user is the owner or an admin
            if (!username.equals(noteOwner)) {
                throw new SQLException("You do not have permission to edit this note.");
            }

            String sql = "UPDATE notes SET content = ? WHERE note_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newContent);
                stmt.setInt(2, noteId);
                stmt.executeUpdate();
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
    /**
     * Deletes a note by its ID.
     * Only the original author (or an admin) can delete.
     */
    public void delete(int noteId) throws SQLException {
        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in. Cannot delete note.");
        }

        Connection conn = null;
        try {
            conn = new DBConnection().establish();
            DBConnection.setCurrentUser(conn, username);
            conn.setAutoCommit(false);

            // Verify ownership or admin role
            String checkSql = "SELECT username FROM notes WHERE note_id = ?";
            String noteOwner;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, noteId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Note not found.");
                    }
                    noteOwner = rs.getString("username");
                }
            }

            if (!username.equals(noteOwner)) {
                throw new SQLException("You do not have permission to delete this note.");
            }

            String sql = "DELETE FROM notes WHERE note_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, noteId);
                stmt.executeUpdate();
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

    // === HELPER ===
    private Note mapRowToNote(ResultSet rs) throws SQLException {
        return new Note(
                rs.getInt("note_id"),
                rs.getString("username"),
                rs.getInt("apartment_id"),
                rs.getString("content"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
