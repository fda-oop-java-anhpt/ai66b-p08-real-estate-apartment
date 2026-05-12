package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.UniversalLog;
import com.oop.project.util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'universal_log' table.
 * Provides read and delete operations (no insert/update, as triggers handle logging).
 */
public class UniversalLogRepository implements DAO {

    // === READ ===
    /**
     * Retrieves a log entry by its ID.
     */
    public UniversalLog getById(int logId) throws SQLException {
        String sql = "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                "FROM universal_log WHERE log_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLog(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all log entries, ordered by most recent first.
     */
    public List<UniversalLog> getAll() throws SQLException {
        String sql = "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                "FROM universal_log ORDER BY action_time DESC";
        List<UniversalLog> logs = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                logs.add(mapRowToLog(rs));
            }
        }
        return logs;
    }

    /**
     * Retrieves logs filtered by username.
     */
    public List<UniversalLog> getByUsername(String username) throws SQLException {
        String sql = "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                "FROM universal_log WHERE username LIKE ? ORDER BY action_time DESC";
        List<UniversalLog> logs = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + username + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Retrieves logs filtered by table name.
     */
    public List<UniversalLog> getByTableName(String tableName) throws SQLException {
        String sql = "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                "FROM universal_log WHERE table_name LIKE ? ORDER BY action_time DESC";
        List<UniversalLog> logs = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + tableName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Retrieves logs filtered by action type (INSERT, UPDATE, DELETE).
     */
    public List<UniversalLog> getByActionType(String actionType) throws SQLException {
        String sql = "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                "FROM universal_log WHERE action_type LIKE ? ORDER BY action_time DESC";
        List<UniversalLog> logs = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + actionType + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Retrieves logs related to a specific record (by its ID).
     */
    public List<UniversalLog> getByRecordId(int recordId) throws SQLException {
        String sql = "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                "FROM universal_log WHERE record_id = ? ORDER BY action_time DESC";
        List<UniversalLog> logs = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Advanced filtering with multiple optional criteria.
     * Null or empty parameters are ignored.
     */
    public List<UniversalLog> filter(String tableName, String actionType, String username,
                                     Timestamp startTime, Timestamp endTime) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT log_id, table_name, action_type, record_id, username, role, content, action_time " +
                        "FROM universal_log WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (tableName != null && !tableName.trim().isEmpty()) {
            sql.append("AND table_name LIKE ? ");
            params.add("%" + tableName + "%");
        }
        if (actionType != null && !actionType.trim().isEmpty()) {
            sql.append("AND action_type LIKE ? ");
            params.add("%" + actionType + "%");
        }
        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND username LIKE ? ");
            params.add("%" + username + "%");
        }
        if (startTime != null) {
            sql.append("AND action_time >= ? ");
            params.add(startTime);
        }
        if (endTime != null) {
            sql.append("AND action_time <= ? ");
            params.add(endTime);
        }

        sql.append("ORDER BY action_time DESC");

        List<UniversalLog> logs = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToLog(rs));
                }
            }
        }
        return logs;
    }

    // === DELETE ===
    /**
     * Deletes a single log entry by ID. Only admin users can perform this action.
     */
    public void delete(int logId) throws SQLException {
        // Permission check
        if (!SessionManager.isAdmin()) {
            throw new SQLException("Only administrators can delete log entries.");
        }

        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in.");
        }

        String sql = "DELETE FROM universal_log WHERE log_id = ?";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, logId);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Deletes logs older than a specified timestamp. Admin only.
     *
     * @param olderThan timestamp cutoff (logs with action_time < this will be deleted)
     * @return number of deleted rows
     */
    public int deleteOlderThan(Timestamp olderThan) throws SQLException {
        if (!SessionManager.isAdmin()) {
            throw new SQLException("Only administrators can delete log entries.");
        }

        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in.");
        }

        String sql = "DELETE FROM universal_log WHERE action_time < ?";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, olderThan);
                return stmt.executeUpdate();
            }
        }
    }

    /**
     * Deletes all log entries (dangerous, admin only).
     */
    public int deleteAll() throws SQLException {
        if (!SessionManager.isAdmin()) {
            throw new SQLException("Only administrators can delete log entries.");
        }

        String username = SessionManager.getCurrentUsername();
        if (username == null) {
            throw new SQLException("No user logged in.");
        }

        String sql = "DELETE FROM universal_log";
        try (Connection conn = new DBConnection().establish()) {
            DBConnection.setCurrentUser(conn, username);
            try (Statement stmt = conn.createStatement()) {
                return stmt.executeUpdate(sql);
            }
        }
    }

    // === HELPER ===
    private UniversalLog mapRowToLog(ResultSet rs) throws SQLException {
        return new UniversalLog(
                rs.getInt("log_id"),
                rs.getString("table_name"),
                rs.getString("action_type"),
                rs.getObject("record_id") != null ? rs.getInt("record_id") : null,
                rs.getString("username"),
                rs.getString("role"),
                rs.getString("content"),   // now safely retrievable
                rs.getTimestamp("action_time")
        );
    }
}
