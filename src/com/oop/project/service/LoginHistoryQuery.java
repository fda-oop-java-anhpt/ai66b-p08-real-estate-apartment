package com.oop.project.service;

import com.oop.project.model.LoginHistory;
import com.oop.project.repository.LoginHistoryRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Handles read and filter operations for login history.
 * Restricted to admin users only.
 */
public class LoginHistoryQuery {

    private final LoginHistoryRepository repository;

    public LoginHistoryQuery() {
        this.repository = new LoginHistoryRepository();
    }

    private void requireAdmin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in.");
        }
        if (!SessionManager.isAdmin()) {
            throw new SecurityException("Only administrators can view login history.");
        }
    }

    public LoginHistory getById(int loginId) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getById(loginId);
    }

    public List<LoginHistory> getAll() throws SQLException, SecurityException {
        requireAdmin();
        return repository.getAll();
    }

    public List<LoginHistory> getByUsername(String username) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getByUsername(username);
    }

    public List<LoginHistory> getByRole(String role) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getByRole(role);
    }

    public List<LoginHistory> filter(String username, String role, Timestamp startTime, Timestamp endTime)
            throws SQLException, SecurityException {
        requireAdmin();
        return repository.filter(username, role, startTime, endTime);
    }

    public List<LoginHistory> getLastLoginPerUser() throws SQLException, SecurityException {
        requireAdmin();
        return repository.getLastLoginPerUser();
    }

    public int count(String username, String role) throws SQLException, SecurityException {
        requireAdmin();
        return repository.count(username, role);
    }

    public List<LoginHistory> getPage(int limit, int offset) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getPage(limit, offset);
    }
    
    public int deleteAll() throws SQLException, SecurityException {
        requireAdmin();
        return repository.deleteAll();
    }
}