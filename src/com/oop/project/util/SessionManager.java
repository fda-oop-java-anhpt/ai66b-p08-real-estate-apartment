package com.oop.project.util;

import com.oop.project.model.User;

/**
 * Manages the currently logged-in user for the application session.
 * Provides the username needed for database logging triggers.
 */
public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}