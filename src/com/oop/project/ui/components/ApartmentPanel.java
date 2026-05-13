package com.oop.project.ui.components;

import com.oop.project.model.Apartment;
import com.oop.project.service.ApartmentExport;
import com.oop.project.service.ApartmentManagement;
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
import javax.swing.Timer;

public class ApartmentPanel extends JPanel {
    private static final long serialVersionUID = 1L;

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
    private Timer resizeTimer;

    // Favorites
    private Set<Integer> favoriteApartmentIds = new HashSet<>();
    private FavouriteService favouriteService = new FavouriteService();

    public ApartmentPanel() {
        aptService = new ApartmentManagement();
        exportService = new ApartmentExport();
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        initComponents();
        layoutComponents();
        setupFilterListeners();
        applyRoleBasedVisibility();
        loadFavoriteIds();
        refreshTable();
    }

    // CHANGED: public visibility
    public void loadFavoriteIds() {
        try {
            favoriteApartmentIds.clear();
            favoriteApartmentIds.addAll(favouriteService.getMyFavoriteIds());
            apartmentTable.repaint();  // ensure hearts update immediately
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        tableModel = new ApartmentTableModel();

        apartmentTable = new JTable(tableModel) {
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

        apartmentTable.setFont(Theme.BODY_FONT);
        apartmentTable.getTableHeader().setFont(Theme.TITLE_FONT);
        apartmentTable.getTableHeader().setBackground(Theme.SURFACE);
        apartmentTable.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        apartmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        apartmentTable.setShowGrid(false);
        apartmentTable.setIntercellSpacing(new Dimension(0, 0));
        apartmentTable.setFillsViewportHeight(true);
        apartmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        sorter = new TableRowSorter<>(tableModel);
        apartmentTable.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));

        apartmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        apartmentTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        apartmentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        apartmentTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        apartmentTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        apartmentTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        apartmentTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        apartmentTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        apartmentTable.getColumnModel().getColumn(8).setPreferredWidth(200);
        apartmentTable.getColumnModel().getColumn(9).setPreferredWidth(80);
        apartmentTable.getColumnModel().getColumn(10).setPreferredWidth(60);

        resizeTimer = new Timer(150, e -> adjustAllRowHeights());
        resizeTimer.setRepeats(false);
        apartmentTable.getColumnModel().addColumnModelListener(new javax.swing.event.TableColumnModelListener() {
            @Override public void columnMarginChanged(javax.swing.event.ChangeEvent e) { resizeTimer.restart(); }
            @Override public void columnAdded(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnMoved(javax.swing.event.TableColumnModelEvent e) {}
            @Override public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {}
        });

        // Renderers
        for (int i = 0; i < apartmentTable.getColumnCount(); i++) {
            if (i == 8) {
                apartmentTable.getColumnModel().getColumn(i).setCellRenderer(new AmenityCellRenderer());
            } else if (i == 9) {
                apartmentTable.getColumnModel().getColumn(i).setCellRenderer(new NotesButtonRenderer());
                apartmentTable.getColumnModel().getColumn(i).setCellEditor(new NotesButtonEditor());
            } else if (i == 10) {
                apartmentTable.getColumnModel().getColumn(i).setCellRenderer(new FavButtonRenderer());
                apartmentTable.getColumnModel().getColumn(i).setCellEditor(new FavButtonEditor());
            } else {
                apartmentTable.getColumnModel().getColumn(i).setCellRenderer(new StyledTableCellRenderer());
            }
        }

        apartmentTable.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY));

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

    // ── Filter fields ──
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
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.SURFACE);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(apartmentTable);
        scrollPane.setBorder(new EmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(Theme.SURFACE);
        scrollPane.setBackground(Theme.SURFACE);
        add(scrollPane, BorderLayout.CENTER);

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
                minPrice, maxPrice, minBeds, maxBeds, minSize, maxSize,
                category, status, selectedAmenityIds.isEmpty() ? null : selectedAmenityIds);
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
            Window w = SwingUtilities.getWindowAncestor(ApartmentPanel.this);
            if (w instanceof MainFrame) {
                ((MainFrame) w).refreshDashboard();
            }
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
                Window w = SwingUtilities.getWindowAncestor(ApartmentPanel.this);
                if (w instanceof MainFrame) {
                    ((MainFrame) w).refreshDashboard();
                }
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
                Window w = SwingUtilities.getWindowAncestor(ApartmentPanel.this);
                if (w instanceof MainFrame) {
                    ((MainFrame) w).refreshDashboard();
                }
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
        adjustAllRowHeights();
    }

    private class StyledTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 12, 0, 12));

            int modelRow = table.convertRowIndexToModel(row);
            int aptId = (int) table.getModel().getValueAt(modelRow, 0);
            boolean isFav = favoriteApartmentIds.contains(aptId);

            if (isSelected) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.PRIMARY, 2),
                        new EmptyBorder(0, 12, 0, 12)));
                setBackground(isFav ? new Color(255, 255, 210) : (row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250)));
                setForeground(Theme.TEXT_PRIMARY);
            } else {
                setBackground(isFav ? new Color(255, 255, 210) : (row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250)));
                setForeground(Theme.TEXT_PRIMARY);
            }
            return this;
        }
    }

    private class AmenityCellRenderer extends JTextArea implements TableCellRenderer {
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
                int modelRow = table.convertRowIndexToModel(row);
                int aptId = (int) table.getModel().getValueAt(modelRow, 0);
                boolean isFav = favoriteApartmentIds.contains(aptId);

                if (isSelected) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.PRIMARY, 2),
                            new EmptyBorder(6, 12, 6, 12)));
                } else {
                    setBorder(new EmptyBorder(6, 12, 6, 12));
                }
                setBackground(isFav ? new Color(255, 255, 210) : (row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250)));
                setForeground(Theme.TEXT_PRIMARY);
                
            int colWidth = table.getColumnModel().getColumn(column).getWidth();
            setSize(new Dimension(colWidth, Integer.MAX_VALUE));
            int prefHeight = getPreferredSize().height;
            if (table.getRowHeight(row) < prefHeight) table.setRowHeight(row, prefHeight);
            return this;
        }
    }

    private class NotesButtonRenderer extends JButton implements TableCellRenderer {
        public NotesButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setContentAreaFilled(true);
            setFocusPainted(false);
            setFont(Theme.SMALL_FONT.deriveFont(Font.BOLD));
            setHorizontalTextPosition(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
           setText("Notes");
           if (isSelected) {
               setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2));
           } else {
               setBorder(null);
           }
           setBackground(row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250));
           setForeground(Theme.PRIMARY);
           return this;
       }
    }

    private class NotesButtonEditor extends DefaultCellEditor {
        private int selectedRow;
        public NotesButtonEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            JButton btn = new JButton("Notes");
            btn.addActionListener(e -> {
                int modelRow = apartmentTable.convertRowIndexToModel(selectedRow);
                int aptId = (int) tableModel.getValueAt(modelRow, 0);
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(ApartmentPanel.this);
                new NotesDialog(owner, aptId).setVisible(true);
                fireEditingStopped();
            });
            return btn;
        }
        @Override
        public Object getCellEditorValue() { return ""; }
    }

    private class FavButtonRenderer extends JButton implements TableCellRenderer {
        public FavButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
            setContentAreaFilled(true);
            setFocusPainted(false);
            setFont(Theme.SMALL_FONT.deriveFont(Font.BOLD));
            setHorizontalTextPosition(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            int modelRow = table.convertRowIndexToModel(row);
            int aptId = (int) table.getModel().getValueAt(modelRow, 0);
            boolean isFav = favoriteApartmentIds.contains(aptId);
            setText(isFav ? "\u2665" : "\u2661");
            if (isSelected) {
                setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2));
            } else {
                setBorder(null);
            }
            setBackground(row % 2 == 0 ? Theme.SURFACE : new Color(245, 248, 250));
            setForeground(isFav ? new Color(200, 0, 0) : Theme.TEXT_SECONDARY);
            return this;
        }
    }

    private class FavButtonEditor extends DefaultCellEditor {
        private int selectedRow;
        public FavButtonEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            JButton btn = new JButton();
            btn.addActionListener(e -> {
                int modelRow = apartmentTable.convertRowIndexToModel(selectedRow);
                int aptId = (int) tableModel.getValueAt(modelRow, 0);
                try {
                    boolean nowFav = favouriteService.toggle(aptId);
                    if (nowFav) favoriteApartmentIds.add(aptId);
                    else favoriteApartmentIds.remove(aptId);
                    apartmentTable.repaint();
                    Window window = SwingUtilities.getWindowAncestor(ApartmentPanel.this);
                    if (window instanceof MainFrame) {
                        ((MainFrame) window).refreshFavoritesTab();
                    }
                    Window w = SwingUtilities.getWindowAncestor(ApartmentPanel.this);
                    if (w instanceof MainFrame) {
                        ((MainFrame) w).refreshDashboard();
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ApartmentPanel.this,
                            "Error toggling favorite: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                fireEditingStopped();
            });
            return btn;
        }
        @Override
        public Object getCellEditorValue() { return ""; }
    }
}
