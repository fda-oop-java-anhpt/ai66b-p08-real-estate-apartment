package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.User;
import com.oop.project.util.HashingUtil;
import java.sql.*;

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
        // Establish new connection for login action.
        Connection jcon = DBConnection.establish(); 
        jcon.setAutoCommit(false);   // Enable SQL transactions
        ResultSet rs = null;
        
        
        // Create a prepared statements for MySQL transaction.
        // Prepared statements help preventing SQL injection attacks.
        try(
            // Search for user
            PreparedStatement find_user = jcon.prepareStatement("""
                                                        SELECT * FROM users
                                                        WHERE users.username = ?
                                                        AND users.password_hash = ?
                                                        AND users.role = ?
                                                        """);
            // Then update last login datetime
            PreparedStatement update_last_login = jcon.prepareStatement("""
                                                                    UPDATE users SET last_login = NOW() WHERE username = ?
                                                                    """)
            ) {
            
            // Inject the parameters to '?' placeholders in the SQL statements.
            find_user.setString(1, this.username);
            find_user.setString(2, this.password);
            find_user.setString(3, this.role);

            update_last_login.setString(1, username);
            
            
            // Execute statement & return the query result (Later be used to create User object)
            rs = find_user.executeQuery();
            update_last_login.executeUpdate();
            jcon.commit();
            

            // close the statements after execution.
            update_last_login.close();

            
            // Scan for the query's result row.
            while (rs.next()) {
                // Create new User object (POJO) if valid
                User user = new User(rs.getString("username"), rs.getString("role"));

                // Close all used resources
                rs.close();
                find_user.close();
                jcon.close();

                // return the user POJO
                return user;
            }
            
        } catch (SQLException e) {
        // Roll back and close all resources if error
        jcon.rollback();
        jcon.close();
        throw e;
        }

       // return none if user not found
       return null;
    }
    
    /*
    * Register's core logic method
    */
    public void register() throws SQLException {
        // Establish new connection for register action.
        Connection jcon = DBConnection.establish(); 
        jcon.setAutoCommit(false);  // Enable SQL transactions
        
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
