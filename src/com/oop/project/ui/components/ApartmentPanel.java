package com.oop.project.ui.components;

import com.oop.project.model.Apartment;
import com.oop.project.service.ApartmentExport;
import com.oop.project.service.ApartmentManagement;
import com.oop.project.ui.Theme;
import com.oop.project.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApartmentPanel extends JPanel {
    private ApartmentTableModel tableModel;
    private JTable apartmentTable;
    private TableRowSorter<ApartmentTableModel> sorter;
    private StyledButton addButton, editButton, deleteButton, exportButton;
    private JTextField searchCityField, minPriceField, maxPriceField, minBedField, maxBedField, minSizeField, maxSizeField;
    private JComboBox<String> categoryCombo, statusCombo;
    private ApartmentManagement aptService;
    private ApartmentExport exportService;
    private JButton selectAmenitiesButton;
    private JLabel selectedAmenitiesLabel;
    private List<Integer> selectedAmenityIds = new ArrayList<>();

    // Timer to throttle row height adjustments during column resizing
    private Timer resizeTimer;

    public ApartmentPanel() {
        aptService = new ApartmentManagement();
        exportService = new ApartmentExport();
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        initComponents();
        layoutComponents();
        setupFilterListeners();
        applyRoleBasedVisibility();
        refreshTable();
    }

    private void initComponents() {
        tableModel = new ApartmentTableModel();

        // Create table with overridden prepareRenderer for dynamic row heights
        apartmentTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (column == 8) {
                    int currentHeight = getRowHeight(row);
                    if (comp instanceof JTextArea) {
                        JTextArea textArea = (JTextArea) comp;
                        int columnWidth = getColumnModel().getColumn(column).getWidth();
                        textArea.setSize(new Dimension(columnWidth, Integer.MAX_VALUE));
                        int preferredHeight = textArea.getPreferredSize().height + 10;
                        if (currentHeight != preferredHeight) {
                            setRowHeight(row, preferredHeight);
                        }
                    }
                }
                return comp;
            }
        };

        // Table appearance
        apartmentTable.setFont(Theme.BODY_FONT);
        apartmentTable.getTableHeader().setFont(Theme.TITLE_FONT);
        apartmentTable.getTableHeader().setBackground(Theme.SURFACE);
        apartmentTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        apartmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        apartmentTable.setShowGrid(false);
        apartmentTable.setIntercellSpacing(new Dimension(0, 0));
        apartmentTable.setFillsViewportHeight(true);
        apartmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // We'll handle initial sizing manually

        // Sorting
        sorter = new TableRowSorter<>(tableModel);
        apartmentTable.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        // Initial preferred widths (will be overridden proportionally when viewport is ready)
        apartmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        apartmentTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        apartmentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        apartmentTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        apartmentTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        apartmentTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        apartmentTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        apartmentTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        apartmentTable.getColumnModel().getColumn(8).setPreferredWidth(200);

        // Throttled column resize listener to prevent glitching
        resizeTimer = new Timer(150, e -> adjustAllRowHeights());
        resizeTimer.setRepeats(false);

        apartmentTable.getColumnModel().addColumnModelListener(new javax.swing.event.TableColumnModelListener() {
            @Override
            public void columnMarginChanged(javax.swing.event.ChangeEvent e) {
                // Restart timer on each margin change; row heights adjust only after resizing stops
                resizeTimer.restart();
            }
            @Override public void columnAdded(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnMoved(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {}
        });

        // Apply styled renderer to all columns except amenities
        StyledTableCellRenderer styledRenderer = new StyledTableCellRenderer();
        for (int i = 0; i < apartmentTable.getColumnCount(); i++) {
            if (i != 8) {
                apartmentTable.getColumnModel().getColumn(i).setCellRenderer(styledRenderer);
            }
        }

        // Apply wrapping renderer to amenities column
        apartmentTable.getColumnModel().getColumn(8).setCellRenderer(new AmenityCellRenderer());

        // Header border
        apartmentTable.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY)
        );

        // Buttons
        addButton = new StyledButton("Add", Theme.PRIMARY);
        editButton = new StyledButton("Edit", Theme.ACCENT);
        deleteButton = new StyledButton("Delete", Theme.DANGER);
        exportButton = new StyledButton("Export CSV", Theme.WARNING);

        addButton.addActionListener(e -> addApartment());
        editButton.addActionListener(e -> editApartment());
        deleteButton.addActionListener(e -> deleteApartment());
        exportButton.addActionListener(e -> exportToCSV());

        selectAmenitiesButton = new StyledButton("Select", Theme.PRIMARY);
        selectAmenitiesButton.addActionListener(e -> openAmenityFilterDialog());
        selectedAmenitiesLabel = new JLabel("None");
        selectedAmenitiesLabel.setFont(Theme.SMALL_FONT);
        selectedAmenitiesLabel.setForeground(Theme.TEXT_SECONDARY);

        // Filter fields
        searchCityField = createFilterField(12);
        minPriceField = createFilterField(6);
        maxPriceField = createFilterField(6);
        minBedField = createFilterField(4);
        maxBedField = createFilterField(4);
        minSizeField = createFilterField(6);
        maxSizeField = createFilterField(6);
        categoryCombo = new JComboBox<>(new String[]{"All", "luxury", "standard", "budget"});
        statusCombo = new JComboBox<>(new String[]{"All", "empty", "rented"});
        styleComboBox(categoryCombo);
        styleComboBox(statusCombo);
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

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(Theme.SMALL_FONT);
        combo.setBackground(Theme.SURFACE);
        ((JLabel) combo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void layoutComponents() {
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(240, 245, 250));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1)
        ));

        filterPanel.add(createFilterLabel("City:"));
        filterPanel.add(searchCityField);
        filterPanel.add(createFilterLabel("Price:"));
        filterPanel.add(minPriceField);
        filterPanel.add(new JLabel("-"));
        filterPanel.add(maxPriceField);
        filterPanel.add(createFilterLabel("Beds:"));
        filterPanel.add(minBedField);
        filterPanel.add(new JLabel("-"));
        filterPanel.add(maxBedField);
        filterPanel.add(createFilterLabel("Size:"));
        filterPanel.add(minSizeField);
        filterPanel.add(new JLabel("-"));
        filterPanel.add(maxSizeField);
        filterPanel.add(createFilterLabel("Category:"));
        filterPanel.add(categoryCombo);
        filterPanel.add(createFilterLabel("Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(createFilterLabel("Amenities:"));
        filterPanel.add(selectAmenitiesButton);
        filterPanel.add(selectedAmenitiesLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(230, 238, 245));
        buttonPanel.setBorder(new EmptyBorder(8, 15, 15, 15));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.SURFACE);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table scroll pane
        JScrollPane scrollPane = new JScrollPane(apartmentTable);
        scrollPane.setBorder(new EmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(Theme.SURFACE);
        scrollPane.setBackground(Theme.SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        // --- Initial proportional column sizing when viewport is ready ---
        scrollPane.addComponentListener(new ComponentAdapter() {
            private boolean initialSizingDone = false;
            @Override
            public void componentResized(ComponentEvent e) {
                if (!initialSizingDone && scrollPane.getViewport().getWidth() > 0) {
                    initialSizingDone = true;
                    setProportionalColumnWidths(scrollPane.getViewport().getWidth());
                }
            }
        });
    }

    /**
     * Distributes the available width among columns proportionally based on their preferred widths.
     */
    private void setProportionalColumnWidths(int totalWidth) {
        if (totalWidth <= 0) return;
        int totalPreferred = 0;
        for (int i = 0; i < apartmentTable.getColumnCount(); i++) {
            totalPreferred += apartmentTable.getColumnModel().getColumn(i).getPreferredWidth();
        }
        if (totalPreferred == 0) return;

        for (int i = 0; i < apartmentTable.getColumnCount(); i++) {
            int preferred = apartmentTable.getColumnModel().getColumn(i).getPreferredWidth();
            int proportionalWidth = (int) ((long) preferred * totalWidth / totalPreferred);
            apartmentTable.getColumnModel().getColumn(i).setPreferredWidth(proportionalWidth);
        }
        adjustAllRowHeights(); // Adjust row heights after setting initial widths
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

        searchCityField.getDocument().addDocumentListener(docListener);
        minPriceField.getDocument().addDocumentListener(docListener);
        maxPriceField.getDocument().addDocumentListener(docListener);
        minBedField.getDocument().addDocumentListener(docListener);
        maxBedField.getDocument().addDocumentListener(docListener);
        minSizeField.getDocument().addDocumentListener(docListener);
        maxSizeField.getDocument().addDocumentListener(docListener);

        categoryCombo.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) applyFilter(); });
        statusCombo.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) applyFilter(); });
    }

    private void applyFilter() {
        String city = searchCityField.getText().trim();
        Double minPrice = parseDouble(minPriceField.getText());
        Double maxPrice = parseDouble(maxPriceField.getText());
        Integer minBeds = parseInteger(minBedField.getText());
        Integer maxBeds = parseInteger(maxBedField.getText());
        Double minSize = parseDouble(minSizeField.getText());
        Double maxSize = parseDouble(maxSizeField.getText());
        String category = Objects.equals(categoryCombo.getSelectedItem(), "All") ? null : (String) categoryCombo.getSelectedItem();
        String status = Objects.equals(statusCombo.getSelectedItem(), "All") ? null : (String) statusCombo.getSelectedItem();

        tableModel.filter(city.isEmpty() ? null : city,
                minPrice, maxPrice,
                minBeds, maxBeds,
                minSize, maxSize,
                category, status,
                selectedAmenityIds.isEmpty() ? null : selectedAmenityIds);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        adjustAllRowHeights();
    }

    private Double parseDouble(String s) {
        try { return s.isEmpty() ? null : Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }

    private Integer parseInteger(String s) {
        try { return s.isEmpty() ? null : Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

    private void refreshTable() {
        tableModel.refreshData();
        adjustAllRowHeights();
    }

    private void applyRoleBasedVisibility() {
        boolean isAdmin = SessionManager.isAdmin();
        addButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
    }

    private void addApartment() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        ApartmentDialog dialog = new ApartmentDialog(owner instanceof Frame ? (Frame) owner : null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Apartment added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editApartment() {
        int selectedRow = apartmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an apartment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = apartmentTable.convertRowIndexToModel(selectedRow);
        Apartment apt = tableModel.getApartmentAt(modelRow);
        try {
            List<Integer> amenityIds = aptService.getAmenityIdsForApartment(apt.getApartmentId());
            Window owner = SwingUtilities.getWindowAncestor(this);
            ApartmentDialog dialog = new ApartmentDialog(owner instanceof Frame ? (Frame) owner : null,
                    apt.getApartmentId(), apt.getAddress(), apt.getCity(), apt.getPrice(),
                    apt.getBedrooms(), apt.getSize(), apt.getStatus(), amenityIds);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Apartment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | SecurityException e) {
            JOptionPane.showMessageDialog(this, "Failed to load amenities: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteApartment() {
        int selectedRow = apartmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an apartment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = apartmentTable.convertRowIndexToModel(selectedRow);
        Apartment apt = tableModel.getApartmentAt(modelRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete apartment #" + apt.getApartmentId() + " at " + apt.getAddress() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                aptService.deleteApartment(apt.getApartmentId());
                refreshTable();
                JOptionPane.showMessageDialog(this, "Apartment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | SecurityException e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV() {
        List<Apartment> currentList = tableModel.getCurrentList();
        if (currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("apartments_filtered.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                exportService.exportListToCSV(currentList, fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Export completed: " + currentList.size() + " apartments exported.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAmenityFilterDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        AmenityFilterDialog dialog = new AmenityFilterDialog(owner instanceof Frame ? (Frame) owner : null, selectedAmenityIds);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            selectedAmenityIds = dialog.getSelectedAmenityIds();
            updateSelectedAmenitiesLabel();
            applyFilter();
        }
    }

    private void updateSelectedAmenitiesLabel() {
        if (selectedAmenityIds.isEmpty()) {
            selectedAmenitiesLabel.setText("None");
        } else {
            selectedAmenitiesLabel.setText(selectedAmenityIds.size() + " selected");
        }
    }

    private void adjustAllRowHeights() {
        int amenitiesColumnIndex = 8;
        int columnWidth = apartmentTable.getColumnModel().getColumn(amenitiesColumnIndex).getWidth();
        if (columnWidth <= 0) return;

        for (int row = 0; row < apartmentTable.getRowCount(); row++) {
            Object value = apartmentTable.getValueAt(row, amenitiesColumnIndex);
            JTextArea textArea = new JTextArea(value != null ? value.toString() : "");
            textArea.setFont(Theme.BODY_FONT);
            textArea.setSize(new Dimension(columnWidth, Integer.MAX_VALUE));
            int preferredHeight = textArea.getPreferredSize().height + 10;
            apartmentTable.setRowHeight(row, Math.max(preferredHeight, 36));
        }
    }

    // Custom renderers
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

    private static class AmenityCellRenderer extends JTextArea implements TableCellRenderer {
        private final Color evenRowColor = Theme.SURFACE;
        private final Color oddRowColor = new Color(245, 248, 250);

        public AmenityCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(Theme.BODY_FONT);
            setBorder(new EmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value != null ? value.toString() : "");
            if (isSelected) {
                setBackground(Theme.PRIMARY);
                setForeground(Color.WHITE);
            } else {
                setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);
                setForeground(Theme.TEXT_PRIMARY);
            }
            int columnWidth = table.getColumnModel().getColumn(column).getWidth();
            setSize(new Dimension(columnWidth, Integer.MAX_VALUE));
            int preferredHeight = getPreferredSize().height;
            if (table.getRowHeight(row) < preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }
            return this;
        }
    }
}