package com.oop.project.ui.components;

import com.oop.project.model.UniversalLog;
import com.oop.project.service.UniversalLogQuery;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UniversalLogTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "Table", "Action", "Record ID", "Username", "Role", "Content", "Time"
    };
    private List<UniversalLog> logs = new ArrayList<>();
    private final UniversalLogQuery queryService;

    public UniversalLogTableModel() {
        this.queryService = new UniversalLogQuery();
        refreshData();
    }

    public void refreshData() {
        try {
            logs = queryService.getAll();
            fireTableDataChanged();
        } catch (SQLException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public void filter(String tableName, String actionType, String username) {
        try {
            logs = queryService.filter(
                (tableName == null || tableName.trim().isEmpty()) ? null : tableName.trim(),
                (actionType == null || actionType.trim().isEmpty()) ? null : actionType.trim(),
                (username == null || username.trim().isEmpty()) ? null : username.trim(),
                null, null
            );
            fireTableDataChanged();
        } catch (SQLException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public List<UniversalLog> getCurrentList() {
        return new ArrayList<>(logs);
    }

    @Override
    public int getRowCount() {
        return logs.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        UniversalLog log = logs.get(rowIndex);
        switch (columnIndex) {
            case 0: return log.getLogId();
            case 1: return log.getTableName() != null ? log.getTableName() : "None";
            case 2: return log.getActionType() != null ? log.getActionType() : "None";
            case 3: return log.getRecordId() != null ? log.getRecordId() : "None";
            case 4: return log.getUsername() != null ? log.getUsername() : "None";
            case 5: return log.getRole() != null ? log.getRole() : "None";
            case 6: return log.getContent() != null ? log.getContent() : "None";
            case 7: return log.getActionTime() != null ? log.getActionTime().toString() : "None";
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}