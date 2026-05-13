package com.oop.project.ui.components;

import com.oop.project.model.UniversalLog;
import com.oop.project.service.UniversalLogCleanup;
import com.oop.project.ui.Theme;
import com.oop.project.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AuditLogPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private UniversalLogTableModel tableModel;
    private JTable logTable;
    private TableRowSorter<UniversalLogTableModel> sorter;
    private JTextField filterTableField, filterActionField, filterUserField;
    private StyledButton exportButton;
    private StyledButton refreshButton;
    private StyledButton clearLogsButton;

    public AuditLogPanel() {
        if (!SessionManager.isAdmin()) {
            setLayout(new BorderLayout());
            add(new JLabel("Access denied. Admin only."), BorderLayout.CENTER);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        initComponents();
        layoutComponents();
        setupFilterListeners();
        refreshTable();
    }

    private void initComponents() {
        tableModel = new UniversalLogTableModel();
        logTable = new JTable(tableModel);
        logTable.setFont(Theme.BODY_FONT);
        logTable.setRowHeight(36);
        logTable.getTableHeader().setFont(Theme.TITLE_FONT);
        logTable.getTableHeader().setBackground(Theme.SURFACE);
        logTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.setShowGrid(false);
        logTable.setIntercellSpacing(new Dimension(0, 0));
        logTable.setFillsViewportHeight(true);
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        logTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Table
        logTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Action
        logTable.getColumnModel().getColumn(3).setPreferredWidth(70);   // Record ID
        logTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Username
        logTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Role
        logTable.getColumnModel().getColumn(6).setPreferredWidth(300);  // Content
        logTable.getColumnModel().getColumn(7).setPreferredWidth(180);  // Time

        // Apply styled renderer
        StyledTableCellRenderer renderer = new StyledTableCellRenderer();
        for (int i = 0; i < logTable.getColumnCount(); i++) {
            logTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Sorting
        sorter = new TableRowSorter<>(tableModel);
        logTable.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING))); // latest first

        // Header border
        logTable.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY)
        );

        // Filter fields
        filterTableField = createFilterField(15);
        filterActionField = createFilterField(10);
        filterUserField = createFilterField(10);

        exportButton = new StyledButton("Export CSV", Theme.WARNING);
        exportButton.addActionListener(e -> exportToCSV());

        refreshButton = new StyledButton("Refresh", Theme.PRIMARY);
        refreshButton.addActionListener(e -> refreshTable());

        clearLogsButton = new StyledButton("Clear All", Theme.DANGER);
        clearLogsButton.addActionListener(e -> clearAllLogs());
    }

    private JTextField createFilterField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(Theme.SMALL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    private void layoutComponents() {
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(240, 245, 250));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1)
        ));

        filterPanel.add(createFilterLabel("Table:"));
        filterPanel.add(filterTableField);
        filterPanel.add(createFilterLabel("Action:"));
        filterPanel.add(filterActionField);
        filterPanel.add(createFilterLabel("User:"));
        filterPanel.add(filterUserField);
        filterPanel.add(exportButton);
        filterPanel.add(refreshButton);
        filterPanel.add(clearLogsButton);   // <-- added here

        add(filterPanel, BorderLayout.NORTH);

        // Table
        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(new EmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(Theme.SURFACE);
        scrollPane.setBackground(Theme.SURFACE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        label.setForeground(Theme.TEXT_PRIMARY);
        return label;
    }

    private void setupFilterListeners() {
        DocumentListener docListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
        };
        filterTableField.getDocument().addDocumentListener(docListener);
        filterActionField.getDocument().addDocumentListener(docListener);
        filterUserField.getDocument().addDocumentListener(docListener);
    }

    private void applyFilter() {
        tableModel.filter(
            filterTableField.getText(),
            filterActionField.getText(),
            filterUserField.getText()
        );
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
    }

    private void refreshTable() {
        tableModel.refreshData();
    }

    private void exportToCSV() {
        List<UniversalLog> currentList = tableModel.getCurrentList();
        if (currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("audit_logs.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.append("ID,Table,Action,Record ID,Username,Role,Content,Time\n");
                for (UniversalLog log : currentList) {
                    writer.append(String.valueOf(log.getLogId())).append(',')
                          .append(escapeCsv(log.getTableName())).append(',')
                          .append(escapeCsv(log.getActionType())).append(',')
                          .append(log.getRecordId() != null ? String.valueOf(log.getRecordId()) : "None").append(',')
                          .append(escapeCsv(log.getUsername())).append(',')
                          .append(escapeCsv(log.getRole())).append(',')
                          .append(escapeCsv(log.getContent())).append(',')
                          .append(log.getActionTime() != null ? log.getActionTime().toString() : "None").append('\n');
                }
                JOptionPane.showMessageDialog(this, "Export completed: " + currentList.size() + " logs exported.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "None";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void clearAllLogs() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ WARNING: This will permanently delete ALL audit logs.\nThis action cannot be undone.\n\nAre you sure?",
                "Clear All Audit Logs",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                UniversalLogCleanup cleanup = new UniversalLogCleanup();
                int deleted = cleanup.deleteAll();
                JOptionPane.showMessageDialog(this,
                        "Deleted " + deleted + " audit log entries.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } catch (SQLException | SecurityException ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to clear logs: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Reuse the same StyledTableCellRenderer from ApartmentPanel (or copy it here)
    private static class StyledTableCellRenderer extends DefaultTableCellRenderer {
        private final Color evenRowColor = Theme.SURFACE;
        private final Color oddRowColor = new Color(245, 248, 250);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            if (isSelected) {
                setBackground(Theme.PRIMARY);
                setForeground(Color.WHITE);
            } else {
                setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);
                setForeground(Theme.TEXT_PRIMARY);
            }
            return this;
        }
    }
}