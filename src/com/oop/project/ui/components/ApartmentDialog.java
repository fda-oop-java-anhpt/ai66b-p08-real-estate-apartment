package com.oop.project.ui.components;

import com.oop.project.service.ApartmentAmenity;
import com.oop.project.service.ApartmentManagement;
import com.oop.project.ui.Theme;
import com.oop.project.util.CityDataProvider;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.util.List;

public class ApartmentDialog extends JDialog {
    private JTextField addressField, priceField, bedroomsField, sizeField;
    private JComboBox<String> statusCombo;
    private SearchableComboBox cityCombo;
    private AmenitySelectionPanel amenityPanel;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;
    private ApartmentManagement aptService;
    private ApartmentAmenity amenityService;
    private int editApartmentId = -1;

    public ApartmentDialog(Frame owner) {
        super(owner, "Add Apartment", true);
        initServices();
        initComponents();
        layoutComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    public ApartmentDialog(Frame owner, int apartmentId, String address, String city, double price,
                           int bedrooms, double size, String status, List<Integer> amenityIds) {
        super(owner, "Edit Apartment", true);
        this.editApartmentId = apartmentId;
        initServices();
        initComponents();
        layoutComponents();
        populateFields(address, city, price, bedrooms, size, status, amenityIds);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initServices() {
        aptService = new ApartmentManagement();
        amenityService = new ApartmentAmenity();
    }

    private void initComponents() {
        addressField = new JTextField(20);
        cityCombo = new SearchableComboBox(CityDataProvider.getCities());
        priceField = new JTextField(20);
        bedroomsField = new JTextField(20);
        sizeField = new JTextField(20);
        statusCombo = new JComboBox<>(new String[]{"empty", "rented"});

        amenityPanel = new AmenitySelectionPanel();

        saveButton = new StyledButton("Save", Theme.PRIMARY);
        cancelButton = new StyledButton("Cancel", Theme.TEXT_SECONDARY);
        saveButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> dispose());
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Theme.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, gbc, 0, "Address:", addressField);
        addRow(panel, gbc, 1, "City:", cityCombo);
        addRow(panel, gbc, 2, "Price (B VND):", priceField);
        addRow(panel, gbc, 3, "Bedrooms:", bedroomsField);
        addRow(panel, gbc, 4, "Size (m²):", sizeField);
        addRow(panel, gbc, 5, "Status:", statusCombo);
        addRow(panel, gbc, 6, "Amenities:", amenityPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.SURFACE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent comp) {
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.BODY_FONT);
        label.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST; // align label to top
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; // allow vertical expansion
        panel.add(comp, gbc);
        gbc.weighty = 0; // reset
    }

    private void populateFields(String address, String city, double price, int bedrooms, double size,
                                String status, List<Integer> amenityIds) {
        addressField.setText(address);
        cityCombo.setSelectedCity(city);   // Changed
        priceField.setText(String.valueOf(price));
        bedroomsField.setText(String.valueOf(bedrooms));
        sizeField.setText(String.valueOf(size));
        statusCombo.setSelectedItem(status);
        amenityPanel.setSelectedAmenityIds(amenityIds);
    }

    private void save() {
        try {
            String address = addressField.getText().trim();
            String city = cityCombo.getSelectedCity();
            double price = Double.parseDouble(priceField.getText().trim());
            int bedrooms = Integer.parseInt(bedroomsField.getText().trim());
            double size = Double.parseDouble(sizeField.getText().trim());
            String status = (String) statusCombo.getSelectedItem();

            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (city.isEmpty() || !CityDataProvider.getCities().contains(city)) {
                JOptionPane.showMessageDialog(this, "Please select a valid city from the list.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (price <= 0 || bedrooms <= 0 || size <= 0) {
                JOptionPane.showMessageDialog(this, "Price, bedrooms, and size must be positive.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Integer> selectedAmenities = amenityPanel.getSelectedAmenityIds();

            if (editApartmentId == -1) {
                aptService.createApartment(address, city, price, bedrooms, size, status, selectedAmenities);
            } else {
                aptService.updateApartment(editApartmentId, address, city, price, bedrooms, size, status, selectedAmenities);
            }
            confirmed = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
