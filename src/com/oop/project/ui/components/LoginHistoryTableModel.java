package com.oop.project.ui.components;

import com.oop.project.model.LoginHistory;
import com.oop.project.service.LoginHistoryQuery;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "Username", "Role", "Login Time"};
    private List<LoginHistory> entries = new ArrayList<>();
    private final LoginHistoryQuery queryService;

    public LoginHistoryTableModel() {
        this.queryService = new LoginHistoryQuery();
        refreshData();
    }

    public void refreshData() {
        try {
            entries = queryService.getAll();
            fireTableDataChanged();
        } catch (SQLException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public void filter(String username, String role) {
        try {
            entries = queryService.filter(
                (username == null || username.trim().isEmpty()) ? null : username.trim(),
                (role == null || role.trim().isEmpty()) ? null : role.trim(),
                null, null
            );
            fireTableDataChanged();
        } catch (SQLException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public List<LoginHistory> getCurrentList() {
        return new ArrayList<>(entries);
    }

    @Override
    public int getRowCount() {
        return entries.size();
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
        LoginHistory entry = entries.get(rowIndex);
        switch (columnIndex) {
            case 0: return entry.getLoginId();
            case 1: return entry.getUsername() != null ? entry.getUsername() : "None";
            case 2: return entry.getRole() != null ? entry.getRole() : "None";
            case 3: return entry.getLogTime() != null ? entry.getLogTime().toString() : "None";
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}