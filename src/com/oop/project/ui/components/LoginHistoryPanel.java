package com.oop.project.ui.components;

import com.oop.project.model.LoginHistory;
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
import java.util.List;

public class LoginHistoryPanel extends JPanel {
    private LoginHistoryTableModel tableModel;
    private JTable historyTable;
    private TableRowSorter<LoginHistoryTableModel> sorter;
    private JTextField filterUserField, filterRoleField;
    private StyledButton exportButton, refreshButton;

    public LoginHistoryPanel() {
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
        tableModel = new LoginHistoryTableModel();
        historyTable = new JTable(tableModel);
        historyTable.setFont(Theme.BODY_FONT);
        historyTable.setRowHeight(36);
        historyTable.getTableHeader().setFont(Theme.TITLE_FONT);
        historyTable.getTableHeader().setBackground(Theme.SURFACE);
        historyTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.setFillsViewportHeight(true);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Username
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Role
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(200);  // Login Time

        // Styled renderer
        StyledTableCellRenderer renderer = new StyledTableCellRenderer();
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Sorting
        sorter = new TableRowSorter<>(tableModel);
        historyTable.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));

        // Header border
        historyTable.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY)
        );

        // Filter fields
        filterUserField = createFilterField(15);
        filterRoleField = createFilterField(10);

        exportButton = new StyledButton("Export CSV", Theme.WARNING);
        exportButton.addActionListener(e -> exportToCSV());

        refreshButton = new StyledButton("Refresh", Theme.PRIMARY);
        refreshButton.addActionListener(e -> refreshTable());
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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(240, 245, 250));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1)
        ));

        filterPanel.add(createFilterLabel("Username:"));
        filterPanel.add(filterUserField);
        filterPanel.add(createFilterLabel("Role:"));
        filterPanel.add(filterRoleField);
        filterPanel.add(exportButton);
        filterPanel.add(refreshButton);

        add(filterPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(historyTable);
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
        filterUserField.getDocument().addDocumentListener(docListener);
        filterRoleField.getDocument().addDocumentListener(docListener);
    }

    private void applyFilter() {
        tableModel.filter(
            filterUserField.getText(),
            filterRoleField.getText()
        );
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
    }

    private void refreshTable() {
        tableModel.refreshData();
    }

    private void exportToCSV() {
        List<LoginHistory> currentList = tableModel.getCurrentList();
        if (currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("login_history.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.append("ID,Username,Role,Login Time\n");
                for (LoginHistory entry : currentList) {
                    writer.append(String.valueOf(entry.getLoginId())).append(',')
                          .append(escapeCsv(entry.getUsername())).append(',')
                          .append(escapeCsv(entry.getRole())).append(',')
                          .append(entry.getLogTime() != null ? entry.getLogTime().toString() : "None").append('\n');
                }
                JOptionPane.showMessageDialog(this, "Export completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
