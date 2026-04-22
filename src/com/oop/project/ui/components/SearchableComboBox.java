package com.oop.project.ui.components;

import com.oop.project.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

public class SearchableComboBox extends JComboBox<String> {
    private DefaultComboBoxModel<String> fullModel;
    private JTextField editorField;
    private String lastValidSelection;

    public SearchableComboBox(List<String> items) {
        fullModel = new DefaultComboBoxModel<>(new Vector<>(items));
        setModel(fullModel);
        setEditable(true);
        setFont(Theme.BODY_FONT);
        setBackground(Color.WHITE);

        editorField = (JTextField) getEditor().getEditorComponent();
        editorField.setFont(Theme.BODY_FONT);
        editorField.setBorder(new EmptyBorder(4, 8, 4, 8));

        // Filter on typing
        editorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filter(editorField.getText());
                showPopup();
            }
        });

        // Revert to last valid selection if focus lost with invalid text
        editorField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String currentText = editorField.getText().trim();
                if (!isValidCity(currentText)) {
                    // Revert to last valid selection or first item
                    if (lastValidSelection != null && !lastValidSelection.isEmpty()) {
                        setSelectedItem(lastValidSelection);
                    } else if (fullModel.getSize() > 0) {
                        setSelectedItem(fullModel.getElementAt(0));
                        lastValidSelection = fullModel.getElementAt(0);
                    }
                }
            }
        });
    }

    private void filter(String text) {
        DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>();
        String lowerText = text.toLowerCase();
        for (int i = 0; i < fullModel.getSize(); i++) {
            String item = fullModel.getElementAt(i);
            if (item.toLowerCase().contains(lowerText)) {
                filteredModel.addElement(item);
            }
        }
        setModel(filteredModel);
        editorField.setText(text);
    }

    private boolean isValidCity(String text) {
        for (int i = 0; i < fullModel.getSize(); i++) {
            if (fullModel.getElementAt(i).equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        if (item != null) {
            lastValidSelection = item.toString();
            editorField.setText(lastValidSelection);
        }
    }

    public String getSelectedCity() {
        Object selected = getSelectedItem();
        return selected != null ? selected.toString().trim() : "";
    }

    public void setSelectedCity(String city) {
        setSelectedItem(city);
    }
}