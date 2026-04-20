// LoginHistory.java
package com.oop.project.model;

import java.sql.Timestamp;

/**
 * Plain Old Java Object for the 'login_history' table.
 */
public class LoginHistory implements POJO {
    private final int loginId;
    private final String username;
    private final Timestamp logTime;

    public LoginHistory(int loginId, String username, Timestamp logTime) {
        this.loginId = loginId;
        this.username = username;
        this.logTime = logTime;
    }

    @Override
    public int getId() {
        return loginId;
    }

    public int getLoginId() {
        return loginId;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    @Override
    public String toString() {
        return "LoginHistory(" + loginId + ", " + username + ", " + logTime + ")";
    }
}