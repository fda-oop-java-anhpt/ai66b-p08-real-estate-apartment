package com.oop.project.ui.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class StyledComboBox<E> extends JComboBox<E> {
    public StyledComboBox(E[] items) {
        super(items);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBackground(Color.WHITE);
        setBorder(new LineBorder(new Color(200, 200, 200), 1));
        ((JLabel) getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }
}