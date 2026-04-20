// UniversalLog.java
package com.oop.project.model;

import java.sql.Timestamp;

/**
 * Plain Old Java Object for the 'universal_log' table.
 */
public class UniversalLog implements POJO {
    private final int logId;
    private final String tableName;
    private final String actionType;
    private final String username;
    private final Timestamp actionTime;

    public UniversalLog(int logId, String tableName, String actionType,
                        String username, Timestamp actionTime) {
        this.logId = logId;
        this.tableName = tableName;
        this.actionType = actionType;
        this.username = username;
        this.actionTime = actionTime;
    }

    @Override
    public int getId() {
        return logId;
    }

    public int getLogId() {
        return logId;
    }

    public String getTableName() {
        return tableName;
    }

    public String getActionType() {
        return actionType;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getActionTime() {
        return actionTime;
    }

    @Override
    public String toString() {
        return "UniversalLog(" + logId + ", " + tableName + ", " + actionType + ")";
    }
}   