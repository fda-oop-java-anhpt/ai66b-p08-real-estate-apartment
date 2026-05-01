package com.oop.project.ui.components;

import com.oop.project.model.Apartment;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class FavoritesTableModel extends AbstractTableModel {
    private final String[] columns = {
        "ID", "Address", "City", "Price (B VND)", "Bedrooms", "Size (m²)", "Category", "Status", "Amenities", "Notes", ""
    };
    private List<Apartment> apartments = new ArrayList<>();

    public void setApartments(List<Apartment> list) {
        apartments = list;
        fireTableDataChanged();
    }

    public List<Apartment> getCurrentList() {
        return new ArrayList<>(apartments);
    }

    @Override
    public int getRowCount() {
        return apartments.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Apartment apt = apartments.get(row);
        switch (col) {
            case 0: return apt.getApartmentId();
            case 1: return apt.getAddress();
            case 2: return apt.getCity();
            case 3: return String.format("%.2f", apt.getPrice());
            case 4: return apt.getBedrooms();
            case 5: return String.format("%.1f", apt.getSize());
            case 6: return apt.getCategory();
            case 7: return apt.getStatus();
            case 8: return apt.getAmenities();
            case 9: return "";      // Notes button placeholder
            case 10: return "";     // Remove button placeholder
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 9 || col == 10;   // Only Notes and Remove buttons are editable
    }
}