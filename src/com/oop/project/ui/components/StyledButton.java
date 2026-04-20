package com.oop.project.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {
    private Color normalColor;
    private Color hoverColor;

    public StyledButton(String text, Color backgroundColor) {
        super(text);
        this.normalColor = backgroundColor;
        this.hoverColor = backgroundColor.darker();

        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setBackground(normalColor);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
            }
        });
    }

    public void setNormalColor(Color color) {
        this.normalColor = color;
        this.hoverColor = color.darker();
        setBackground(normalColor);
    }
}
