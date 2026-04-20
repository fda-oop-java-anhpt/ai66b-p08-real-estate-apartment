package com.oop.project.repository;

import java.sql.*;
import com.oop.project.db.DBConnection;
import com.oop.project.model.User;
import com.oop.project.util.HashingUtil;

/*
 * Data Access Object (DAO)
 * Core logic for login & register actions
 *
 * Compositions:
 * +UserRepository(username, password, role) -> void
 * +login() -> User
 * +register() -> void
 */

public class UserRepository implements DAO {
    private final String username;
    private final String password;
    private final String role;
    
    // Constructor method
    public UserRepository(String username, String password, String role) throws Exception {
        this.username = username;
        this.password = HashingUtil.bySHA256(password); // Plain password must be hashed.
        this.role = role;
    }
    
    /*
    * Login's core logic method
    */
    public User login() throws SQLException {
        Connection jcon = new DBConnection().establish();
        jcon.setAutoCommit(false);
        ResultSet rs = null;
        
        DBConnection.setCurrentUser(jcon, this.username);

        try (
            PreparedStatement find_user = jcon.prepareStatement("""
                SELECT username, role, created_at, last_login 
                FROM users
                WHERE users.username = ?
                AND users.password_hash = ?
                AND users.role = ?
                """);
            PreparedStatement update_last_login = jcon.prepareStatement("""
                UPDATE users SET last_login = NOW() WHERE username = ?
                """)
        ) {
            find_user.setString(1, this.username);
            find_user.setString(2, this.password);
            find_user.setString(3, this.role);
            update_last_login.setString(1, this.username);

            rs = find_user.executeQuery();
            update_last_login.executeUpdate();
            jcon.commit();

            update_last_login.close();

            while (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("last_login")
                );

                rs.close();
                find_user.close();
                jcon.close();

                return user;
            }

        } catch (SQLException e) {
            jcon.rollback();
            jcon.close();
            throw e;
        }

        return null;
    }
    
    /*
    * Register's core logic method
    */
    public void register() throws SQLException {
        // Establish new connection for register action.
        Connection jcon = new DBConnection().establish();
        jcon.setAutoCommit(false);  // Enable SQL transactions
        
        DBConnection.setCurrentUser(jcon, this.username);
        
        // Create a prepared statement for MySQL transaction.
        try (
            // Add new user
            PreparedStatement regis = jcon.prepareStatement("""
                                                            INSERT INTO users(username, password_hash, role)
                                                            VALUE(?, ?, ?)
                                                            """))
        {
            // Inject the parameters to '?' placeholders in the SQL statement.
            regis.setString(1, this.username);
            regis.setString(2, this.password);
            regis.setString(3, this.role);

            
            // Execute update statement and commit transaction
            regis.executeUpdate();
            jcon.commit();
            
            // Close all resources
            regis.close();
            jcon.close();

          
        } catch (SQLException e) {
            // Rollback and close all resources if error
            jcon.rollback();
            jcon.close();
            throw e;
        }
    }
}
