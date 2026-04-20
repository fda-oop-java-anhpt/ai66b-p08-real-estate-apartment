package com.oop.project.service;

import com.oop.project.exception.AuthenticationException;
import com.oop.project.model.User;
import com.oop.project.util.SessionManager;

/**
 * Controller for authentication operations.
 * Handles login and registration business logic, session management.
 */
public class AuthenticationController {

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param username the username
     * @param password the plain-text password
     * @param role     the selected role (admin/agent)
     * @return the authenticated User object
     * @throws Exception if authentication fails (wrong credentials, DB error, etc.)
     */
    public User login(String username, String password, String role) throws Exception {
        // Delegate to existing service
        User user = LoginAuthentication.getUser(username, password, role);
        // Store in session
        SessionManager.setCurrentUser(user);
        return user;
    }

    /**
     * Registers a new user.
     *
     * @param username the desired username
     * @param password the plain-text password
     * @param role     the role (admin/agent)
     * @throws Exception if registration fails (duplicate username, DB error, etc.)
     */
    public void register(String username, String password, String role) throws Exception {
        RegisterAuthentication.createUser(username, password, role);
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        SessionManager.clearSession();
    }

    /**
     * Checks if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return SessionManager.isLoggedIn();
    }

    /**
     * Gets the currently logged-in user.
     */
    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }
}