package com.oop.project.service;

import com.oop.project.model.UniversalLog;
import com.oop.project.repository.UniversalLogRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Handles read and filter operations for universal logs.
 * Restricted to admin users only.
 */
public class UniversalLogQuery {

    private final UniversalLogRepository repository;

    public UniversalLogQuery() {
        this.repository = new UniversalLogRepository();
    }

    private void requireAdmin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in.");
        }
        if (!SessionManager.isAdmin()) {
            throw new SecurityException("Only administrators can view audit logs.");
        }
    }

    public UniversalLog getById(int logId) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getById(logId);
    }

    public List<UniversalLog> getAll() throws SQLException, SecurityException {
        requireAdmin();
        return repository.getAll();
    }

    public List<UniversalLog> getByUsername(String username) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getByUsername(username);
    }

    public List<UniversalLog> getByTableName(String tableName) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getByTableName(tableName);
    }

    public List<UniversalLog> getByActionType(String actionType) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getByActionType(actionType);
    }

    public List<UniversalLog> getByRecordId(int recordId) throws SQLException, SecurityException {
        requireAdmin();
        return repository.getByRecordId(recordId);
    }

    public List<UniversalLog> filter(String tableName, String actionType, String username,
                                     Timestamp startTime, Timestamp endTime)
            throws SQLException, SecurityException {
        requireAdmin();
        return repository.filter(tableName, actionType, username, startTime, endTime);
    }
}