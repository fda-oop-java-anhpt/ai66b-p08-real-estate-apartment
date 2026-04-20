package com.oop.project.ui.components;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class StyledTextField extends JTextField {
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public StyledTextField() {
        this(null);
    }

    public StyledTextField(String text) {
        super(text);
        setFont(DEFAULT_FONT);
        setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }
}