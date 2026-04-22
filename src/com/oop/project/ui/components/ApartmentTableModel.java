package com.oop.project.ui.components;

import com.oop.project.model.Apartment;
import com.oop.project.service.ApartmentManagement;
import com.oop.project.service.ApartmentSearch;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApartmentTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "Address", "City", "Price (B VND)", "Bedrooms", "Size (m²)", "Category", "Status", "Amenities"
    };
    private List<Apartment> apartments = new ArrayList<>();
    private final ApartmentSearch searchService;
    private final ApartmentManagement managementService;

    public ApartmentTableModel() {
        this.searchService = new ApartmentSearch();
        this.managementService = new ApartmentManagement();
        refreshData();
    }

    public void refreshData() {
        try {
            apartments = managementService.getAllApartments();
            sortById(); // default sort by ID
            fireTableDataChanged();
        } catch (SQLException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public void filter(String city, Double minPrice, Double maxPrice,
                       Integer minBedrooms, Integer maxBedrooms,
                       Double minSize, Double maxSize,
                       String category, String status,
                       List<Integer> amenityIds) {
        try {
            apartments = searchService.filterApartments(city, minPrice, maxPrice,
                    minBedrooms, maxBedrooms, minSize, maxSize,
                    category, status, amenityIds);
            sortById();
            fireTableDataChanged();
        } catch (SQLException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private void sortById() {
        apartments.sort(Comparator.comparingInt(Apartment::getApartmentId));
    }

    public Apartment getApartmentAt(int rowIndex) {
        return apartments.get(rowIndex);
    }

    /**
     * Returns the currently displayed list (after filtering/sorting).
     * Used for exporting the current view.
     */
    public List<Apartment> getCurrentList() {
        return new ArrayList<>(apartments);
    }

    @Override
    public int getRowCount() {
        return apartments.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;  // now 9 columns
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Apartment apt = apartments.get(rowIndex);
        switch (columnIndex) {
            case 0: return apt.getApartmentId();
            case 1: return apt.getAddress();
            case 2: return apt.getCity();
            case 3: return String.format("%.2f", apt.getPrice());
            case 4: return apt.getBedrooms();
            case 5: return String.format("%.1f", apt.getSize());
            case 6: return apt.getCategory();
            case 7: return apt.getStatus();
            case 8: return apt.getAmenities();   // NEW
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class;
            case 3: return String.class;
            case 4: return Integer.class;
            case 5: return String.class;
            default: return String.class;
        }
    }
}