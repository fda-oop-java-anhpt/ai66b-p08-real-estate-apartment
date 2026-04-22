package com.oop.project.ui.components;

import com.oop.project.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FavoritesPanel extends JPanel {
    private JTable favoritesTable;
    private StyledButton removeButton;

    public FavoritesPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        favoritesTable = new JTable();
        favoritesTable.setFont(Theme.BODY_FONT);
        favoritesTable.setRowHeight(30);
        removeButton = new StyledButton("Remove", Theme.DANGER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Theme.SURFACE);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(favoritesTable), BorderLayout.CENTER);
    }
}