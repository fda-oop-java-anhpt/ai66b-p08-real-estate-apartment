package com.oop.project.model;

import java.sql.Timestamp;

public class LoginHistory implements POJO {
    private final int loginId;
    private final String username;
    private final String role;
    private final Timestamp logTime;

    public LoginHistory(int loginId, String username, String role, Timestamp logTime) {
        this.loginId = loginId;
        this.username = username;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    @Override
    public String toString() {
        return "LoginHistory(" + loginId + ", " + username + ", " + role + ", " + logTime + ")";
    }
}
