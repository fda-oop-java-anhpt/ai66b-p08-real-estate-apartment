package com.oop.project.model;

import java.sql.Timestamp;

public class UniversalLog implements POJO {
    private final int logId;
    private final String tableName;
    private final String actionType;
    private final Integer recordId;
    private final String username;
    private final String role;
    private final Timestamp actionTime;

    public UniversalLog(int logId, String tableName, String actionType, Integer recordId,
                        String username, String role, Timestamp actionTime) {
        this.logId = logId;
        this.tableName = tableName;
        this.actionType = actionType;
        this.recordId = recordId;
        this.username = username;
        this.role = role;
        this.actionTime = actionTime;
    }

    @Override
    public int getId() { return logId; }
    public int getLogId() { return logId; }
    public String getTableName() { return tableName; }
    public String getActionType() { return actionType; }
    public Integer getRecordId() { return recordId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Timestamp getActionTime() { return actionTime; }

    @Override
    public String toString() {
        return String.format("UniversalLog(%d, %s, %s, %d, %s, %s, %s)",
                logId, tableName, actionType, recordId, username, role, actionTime);
    }
}
