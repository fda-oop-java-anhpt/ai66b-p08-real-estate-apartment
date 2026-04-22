package com.oop.project.service;

import com.oop.project.repository.UniversalLogRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Handles deletion of universal log entries.
 * Restricted to admin users only.
 */
public class UniversalLogCleanup {

    private final UniversalLogRepository repository;

    public UniversalLogCleanup() {
        this.repository = new UniversalLogRepository();
    }

    private void requireAdmin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in.");
        }
        if (!SessionManager.isAdmin()) {
            throw new SecurityException("Only administrators can delete audit logs.");
        }
    }

    public void deleteById(int logId) throws SQLException, SecurityException {
        requireAdmin();
        repository.delete(logId);
    }

    public int deleteOlderThan(Timestamp cutoff) throws SQLException, SecurityException {
        requireAdmin();
        return repository.deleteOlderThan(cutoff);
    }

    public int deleteAll() throws SQLException, SecurityException {
        requireAdmin();
        return repository.deleteAll();
    }
}