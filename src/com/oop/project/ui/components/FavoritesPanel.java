package com.oop.project.ui.components;

import com.oop.project.model.Apartment;
import com.oop.project.service.ApartmentExport;
import com.oop.project.service.ApartmentSearch;
import com.oop.project.service.FavouriteService;
import com.oop.project.ui.Theme;
import com.oop.project.util.SessionManager;
import com.oop.project.ui.MainFrame;

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
import java.util.*;
import java.util.List;
import javax.swing.Timer;          // correct import

public class FavoritesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private FavoritesTableModel tableModel;
    private JTable favoritesTable;
    private TableRowSorter<FavoritesTableModel> sorter;
    private StyledButton exportButton;
    private JTextField searchCityField, minPriceField, maxPriceField, minBedField, maxBedField, minSizeField, maxSizeField;
    private JComboBox<String> categoryCombo, statusCombo;
    private JButton selectAmenitiesButton;
    private JLabel selectedAmenitiesLabel;
    private List<Integer> selectedAmenityIds = new ArrayList<>();

    private FavouriteService favService = new FavouriteService();
    private ApartmentSearch searchService = new ApartmentSearch();
    private ApartmentExport exportService = new ApartmentExport();
    private List<Integer> favoriteApartmentIds = new ArrayList<>();
    private Timer resizeTimer;

    public FavoritesPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        initComponents();
        layoutComponents();
        setupFilterListeners();
        loadFavorites();
    }

    private void initComponents() {
        tableModel = new FavoritesTableModel();

        // Create table with prepareRenderer override (ONCE)
        favoritesTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (column == 8 && comp instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) comp;
                    int colWidth = getColumnModel().getColumn(column).getWidth();
                    textArea.setSize(new Dimension(colWidth, Integer.MAX_VALUE));
                    int prefHeight = textArea.getPreferredSize().height + 10;
                    if (getRowHeight(row) != prefHeight) setRowHeight(row, prefHeight);
                }
                return comp;
            }
        };

        favoritesTable.setFont(Theme.BODY_FONT);
        favoritesTable.setRowHeight(36);
        favoritesTable.getTableHeader().setFont(Theme.TITLE_FONT);
        favoritesTable.getTableHeader().setBackground(Theme.SURFACE);
        favoritesTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        favoritesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        favoritesTable.setShowGrid(false);
        favoritesTable.setIntercellSpacing(new Dimension(0, 0));
        favoritesTable.setFillsViewportHeight(true);
        favoritesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        favoritesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        favoritesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        favoritesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        favoritesTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        favoritesTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        favoritesTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        favoritesTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        favoritesTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        favoritesTable.getColumnModel().getColumn(8).setPreferredWidth(200);
        favoritesTable.getColumnModel().getColumn(9).setPreferredWidth(80);
        favoritesTable.getColumnModel().getColumn(10).setPreferredWidth(80);

        resizeTimer = new Timer(150, e -> adjustAllRowHeights());
        resizeTimer.setRepeats(false);
        favoritesTable.getColumnModel().addColumnModelListener(new javax.swing.event.TableColumnModelListener() {
            @Override public void columnMarginChanged(javax.swing.event.ChangeEvent e) { resizeTimer.restart(); }
            @Override public void columnAdded(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnMoved(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {}
        });

        sorter = new TableRowSorter<>(tableModel);
        favoritesTable.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        favoritesTable.getColumnModel().getColumn(9).setCellRenderer(new NotesButtonRenderer());
        favoritesTable.getColumnModel().getColumn(9).setCellEditor(new NotesButtonEditor());
        favoritesTable.getColumnModel().getColumn(10).setCellRenderer(new RemoveButtonRenderer());
        favoritesTable.getColumnModel().getColumn(10).setCellEditor(new RemoveButtonEditor());

        SelectionBorderRenderer baseRenderer = new SelectionBorderRenderer();
        for (int i = 0; i < 8; i++) {
            favoritesTable.getColumnModel().getColumn(i).setCellRenderer(baseRenderer);
        }
        favoritesTable.getColumnModel().getColumn(8).setCellRenderer(new AmenityCellRenderer());

        favoritesTable.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY));

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

        selectAmenitiesButton = new StyledButton("Select", Theme.PRIMARY);
        selectAmenitiesButton.addActionListener(e -> openAmenityFilterDialog());
        selectedAmenitiesLabel = new JLabel("None");
        selectedAmenitiesLabel.setFont(Theme.SMALL_FONT);
        selectedAmenitiesLabel.setForeground(Theme.TEXT_SECONDARY);

        exportButton = new StyledButton("Export CSV", Theme.WARNING);
        exportButton.addActionListener(e -> exportToCSV());
    }

    private void layoutComponents() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(240, 245, 250));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1)));

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(230, 238, 245));
        buttonPanel.setBorder(new EmptyBorder(8, 15, 15, 15));
        buttonPanel.add(exportButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.SURFACE);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(favoritesTable);
        scrollPane.setBorder(new EmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(Theme.SURFACE);
        scrollPane.setBackground(Theme.SURFACE);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);   // smooth scroll
        add(scrollPane, BorderLayout.CENTER);
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
        // Always update the table, even when the list is empty.
        if (favoriteApartmentIds.isEmpty()) {
            tableModel.setApartments(new ArrayList<>());   // clear the table
            return;
        }

        String city = searchCityField.getText().trim();
        Double minPrice = parseDouble(minPriceField.getText());
        Double maxPrice = parseDouble(maxPriceField.getText());
        Integer minBeds = parseInteger(minBedField.getText());
        Integer maxBeds = parseInteger(maxBedField.getText());
        Double minSize = parseDouble(minSizeField.getText());
        Double maxSize = parseDouble(maxSizeField.getText());
        String category = Objects.equals(categoryCombo.getSelectedItem(), "All") ? null : (String) categoryCombo.getSelectedItem();
        String status = Objects.equals(statusCombo.getSelectedItem(), "All") ? null : (String) statusCombo.getSelectedItem();

        try {
            List<Apartment> filtered = searchService.filterFavorites(favoriteApartmentIds,
                    city.isEmpty() ? null : city, minPrice, maxPrice, minBeds, maxBeds, minSize, maxSize,
                    category, status, selectedAmenityIds.isEmpty() ? null : selectedAmenityIds);
            tableModel.setApartments(filtered);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFavorites() {
        try {
            favoriteApartmentIds = favService.getMyFavoriteIds();
            applyFilter();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load favorites: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void exportToCSV() {
        List<Apartment> currentList = tableModel.getCurrentList();
        if (currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("favorites_filtered.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                exportService.exportListToCSV(currentList, fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Export completed: " + currentList.size() + " apartments exported.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- helper methods ---
    private JTextField createFilterField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(Theme.SMALL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(Theme.SMALL_FONT);
        combo.setBackground(Theme.SURFACE);
        ((JLabel) combo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        label.setForeground(Theme.TEXT_PRIMARY);
        return label;
    }

    private Double parseDouble(String s) {
        try { return s.isEmpty() ? null : Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }
    private Integer parseInteger(String s) {
        try { return s.isEmpty() ? null : Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

    // --- dynamic row heights ---
    private void adjustAllRowHeights() {
        int col = 8;
        int columnWidth = favoritesTable.getColumnModel().getColumn(col).getWidth();
        if (columnWidth <= 0) return;
        for (int row = 0; row < favoritesTable.getRowCount(); row++) {
            Object value = favoritesTable.getValueAt(row, col);
            JTextArea area = new JTextArea(value != null ? value.toString() : "");
            area.setFont(Theme.BODY_FONT);
            area.setSize(new Dimension(columnWidth, Integer.MAX_VALUE));
            int prefHeight = area.getPreferredSize().height + 10;
            favoritesTable.setRowHeight(row, Math.max(prefHeight, 36));
        }
    }

    // ----- Renderers (unchanged from your version) -----
    private class SelectionBorderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            if (isSelected) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.PRIMARY, 2),
                        new EmptyBorder(0, 12, 0, 12)));
            }
            setBackground(row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250));
            setForeground(Theme.TEXT_PRIMARY);
            return this;
        }
    }

    private class AmenityCellRenderer extends JTextArea implements TableCellRenderer {
        public AmenityCellRenderer() {
            setLineWrap(true); setWrapStyleWord(true); setOpaque(true);
            setFont(Theme.BODY_FONT); setBorder(new EmptyBorder(6, 12, 6, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value != null ? value.toString() : "");
            if (isSelected) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.PRIMARY, 2),
                        new EmptyBorder(6, 12, 6, 12)));
            } else {
                setBorder(new EmptyBorder(6, 12, 6, 12));
            }
            setBackground(row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250));
            return this;
        }
    }

    private class NotesButtonRenderer extends JButton implements TableCellRenderer {
        public NotesButtonRenderer() {
            setOpaque(true); setFont(Theme.SMALL_FONT); setText("Notes");
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setText("Notes");
            setBorder(isSelected ? BorderFactory.createLineBorder(Theme.PRIMARY, 2) : null);
            setBackground(Theme.SURFACE); setForeground(Theme.TEXT_PRIMARY);
            return this;
        }
    }

    private class NotesButtonEditor extends DefaultCellEditor {
        public NotesButtonEditor() { super(new JTextField()); setClickCountToStart(1); }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            JButton btn = new JButton("Notes");
            btn.addActionListener(e -> {
                int aptId = (int) table.getValueAt(row, 0);
                new NotesDialog((Frame) SwingUtilities.getWindowAncestor(FavoritesPanel.this), aptId).setVisible(true);
                fireEditingStopped();
            });
            return btn;
        }
        public Object getCellEditorValue() { return ""; }
    }

    private class RemoveButtonRenderer extends JButton implements TableCellRenderer {
        public RemoveButtonRenderer() {
            setOpaque(true); setFont(Theme.SMALL_FONT); setText("Remove");
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setText("Remove");
            setBorder(isSelected ? BorderFactory.createLineBorder(Theme.PRIMARY, 2) : null);
            setBackground(Theme.SURFACE); setForeground(Theme.DANGER);
            return this;
        }
    }

    private class RemoveButtonEditor extends DefaultCellEditor {
        private int selectedRow;
        public RemoveButtonEditor() { super(new JTextField()); setClickCountToStart(1); }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            selectedRow = row;
            JButton btn = new JButton("Remove");
            btn.addActionListener(e -> {
                int aptId = (int) table.getValueAt(selectedRow, 0);
                try {
                    favService.toggle(aptId);
                    loadFavorites();
                    Window window = SwingUtilities.getWindowAncestor(FavoritesPanel.this);
                    if (window instanceof MainFrame) {
                        ((MainFrame) window).refreshMainListingFavorites();
                    }
                    Window w = SwingUtilities.getWindowAncestor(FavoritesPanel.this);
                    if (w instanceof MainFrame) {
                        ((MainFrame) w).refreshDashboard();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FavoritesPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                fireEditingStopped();
            });
            return btn;
        }
        public Object getCellEditorValue() { return ""; }
    }
}
