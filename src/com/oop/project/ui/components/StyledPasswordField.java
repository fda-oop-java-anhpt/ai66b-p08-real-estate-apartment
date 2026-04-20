package com.oop.project.ui.components;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class StyledPasswordField extends JPasswordField {
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private boolean echoCharSet = true;

    public StyledPasswordField() {
        setFont(DEFAULT_FONT);
        setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        setEchoChar('•');
    }

    public void setPasswordVisible(boolean visible) {
        if (visible) {
            setEchoChar((char) 0);
            echoCharSet = false;
        } else {
            setEchoChar('•');
            echoCharSet = true;
        }
    }

    public boolean isPasswordVisible() {
        return !echoCharSet;
    }
}