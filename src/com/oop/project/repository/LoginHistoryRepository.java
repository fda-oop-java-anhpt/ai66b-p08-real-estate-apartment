package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.LoginHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for 'login_history' table.
 * Provides read operations (no create/update/delete, as login events are logged automatically by trigger).
 */
public class LoginHistoryRepository implements DAO {

    // === READ ===
    /**
     * Retrieves a login history entry by its ID.
     */
    public LoginHistory getById(int loginId) throws SQLException {
        String sql = "SELECT login_id, username, role, log_time FROM login_history WHERE login_id = ?";
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loginId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoginHistory(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all login history entries, ordered by most recent first.
     */
    public List<LoginHistory> getAll() throws SQLException {
        String sql = "SELECT login_id, username, role, log_time FROM login_history ORDER BY log_time DESC";
        List<LoginHistory> history = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                history.add(mapRowToLoginHistory(rs));
            }
        }
        return history;
    }

    /**
     * Retrieves login history for a specific user.
     */
    public List<LoginHistory> getByUsername(String username) throws SQLException {
        String sql = "SELECT login_id, username, role, log_time FROM login_history WHERE username = ? ORDER BY log_time DESC";
        List<LoginHistory> history = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapRowToLoginHistory(rs));
                }
            }
        }
        return history;
    }

    /**
     * Retrieves login history filtered by role (admin/agent).
     */
    public List<LoginHistory> getByRole(String role) throws SQLException {
        String sql = "SELECT login_id, username, role, log_time FROM login_history WHERE role = ? ORDER BY log_time DESC";
        List<LoginHistory> history = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapRowToLoginHistory(rs));
                }
            }
        }
        return history;
    }

    /**
     * Advanced filtering with date range and optional username/role.
     */
    public List<LoginHistory> filter(String username, String role, Timestamp startTime, Timestamp endTime) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT login_id, username, role, log_time FROM login_history WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND username = ? ");
            params.add(username);
        }
        if (role != null && !role.trim().isEmpty()) {
            sql.append("AND role = ? ");
            params.add(role);
        }
        if (startTime != null) {
            sql.append("AND log_time >= ? ");
            params.add(startTime);
        }
        if (endTime != null) {
            sql.append("AND log_time <= ? ");
            params.add(endTime);
        }

        sql.append("ORDER BY log_time DESC");

        List<LoginHistory> history = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapRowToLoginHistory(rs));
                }
            }
        }
        return history;
    }

    /**
     * Retrieves the most recent login for each user.
     */
    public List<LoginHistory> getLastLoginPerUser() throws SQLException {
        String sql = "SELECT lh.login_id, lh.username, lh.role, lh.log_time " +
                     "FROM login_history lh " +
                     "INNER JOIN (" +
                     "    SELECT username, MAX(log_time) AS max_time " +
                     "    FROM login_history " +
                     "    GROUP BY username" +
                     ") latest ON lh.username = latest.username AND lh.log_time = latest.max_time " +
                     "ORDER BY lh.log_time DESC";
        List<LoginHistory> history = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                history.add(mapRowToLoginHistory(rs));
            }
        }
        return history;
    }

    /**
     * Counts total login events (optionally filtered by username or role).
     */
    public int count(String username, String role) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM login_history WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND username = ? ");
            params.add(username);
        }
        if (role != null && !role.trim().isEmpty()) {
            sql.append("AND role = ? ");
            params.add(role);
        }

        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // === PAGINATION (useful for large datasets) ===
    /**
     * Retrieves a page of login history entries.
     *
     * @param limit  maximum number of rows
     * @param offset starting row offset
     */
    public List<LoginHistory> getPage(int limit, int offset) throws SQLException {
        String sql = "SELECT login_id, username, role, log_time FROM login_history ORDER BY log_time DESC LIMIT ? OFFSET ?";
        List<LoginHistory> history = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapRowToLoginHistory(rs));
                }
            }
        }
        return history;
    }

    // === HELPER ===
    private LoginHistory mapRowToLoginHistory(ResultSet rs) throws SQLException {
        return new LoginHistory(
            rs.getInt("login_id"),
            rs.getString("username"),
            rs.getString("role"),
            rs.getTimestamp("log_time")
        );
    }
}