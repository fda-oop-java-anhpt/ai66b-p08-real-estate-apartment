package com.oop.project.ui.components;

import com.oop.project.model.Amenity;
import com.oop.project.service.ApartmentAmenity;
import com.oop.project.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel that displays a searchable list of amenities with checkboxes.
 */
public class AmenitySelectionPanel extends JPanel {
    private JTextField searchField;
    private JList<AmenityItem> amenityList;
    private DefaultListModel<AmenityItem> listModel;
    private List<Amenity> allAmenities;
    private List<Integer> selectedIds;

    public AmenitySelectionPanel() {
        this.selectedIds = new ArrayList<>();
        loadAmenities();
        initComponents();
        layoutComponents();
        populateList("");
    }

    public AmenitySelectionPanel(List<Integer> previouslySelectedIds) {
        this.selectedIds = new ArrayList<>(previouslySelectedIds);
        loadAmenities();
        initComponents();
        layoutComponents();
        populateList("");
    }

    private void loadAmenities() {
        try {
            allAmenities = new ApartmentAmenity().getAllAmenities();
        } catch (SQLException | SecurityException e) {
            allAmenities = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Failed to load amenities: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        searchField = new JTextField(20);
        searchField.setFont(Theme.BODY_FONT);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                populateList(searchField.getText());
            }
        });

        listModel = new DefaultListModel<>();
        amenityList = new JList<>(listModel);
        amenityList.setFont(Theme.BODY_FONT);
        amenityList.setCellRenderer(new AmenityCheckboxRenderer());
        amenityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        amenityList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = amenityList.locationToIndex(evt.getPoint());
                if (index >= 0) {
                    AmenityItem item = listModel.get(index);
                    item.setSelected(!item.isSelected());
                    amenityList.repaint();
                }
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Theme.SURFACE);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Theme.SURFACE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Scrollable list
        JScrollPane scrollPane = new JScrollPane(amenityList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateList(String filter) {
        listModel.clear();
        String lowerFilter = filter.toLowerCase();
        for (Amenity a : allAmenities) {
            if (lowerFilter.isEmpty() || a.getName().toLowerCase().contains(lowerFilter)) {
                AmenityItem item = new AmenityItem(a.getId(), a.getName());
                item.setSelected(selectedIds.contains(a.getId()));
                listModel.addElement(item);
            }
        }
    }

    /**
     * Returns the list of selected amenity IDs.
     */
    public List<Integer> getSelectedAmenityIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            AmenityItem item = listModel.get(i);
            if (item.isSelected()) {
                ids.add(item.getId());
            }
        }
        return ids;
    }

    /**
     * Sets the selected amenity IDs (e.g., when editing an existing apartment).
     */
    public void setSelectedAmenityIds(List<Integer> ids) {
        this.selectedIds = new ArrayList<>(ids);
        populateList(searchField.getText());
    }

    // Inner class for checkbox items
    private static class AmenityItem {
        private final int id;
        private final String name;
        private boolean selected;

        public AmenityItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }

        @Override
        public String toString() {
            return name;
        }
    }

    // Renderer for checkbox list
    private static class AmenityCheckboxRenderer extends JCheckBox
            implements ListCellRenderer<AmenityItem> {
        @Override
        public Component getListCellRendererComponent(JList<? extends AmenityItem> list,
                                                      AmenityItem value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value.getName());
            setSelected(value.isSelected());
            setFont(Theme.BODY_FONT);
            setBackground(isSelected ? Theme.SURFACE : Theme.BACKGROUND);
            setForeground(Theme.TEXT_PRIMARY);
            return this;
        }
    }
}