package com.oop.project.ui;

import java.awt.*;

public class Theme {
    private static final long serialVersionUID = 1L;

    // Primary colors
    public static final Color PRIMARY = new Color(33, 150, 243);      // Blue
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    public static final Color ACCENT = new Color(76, 175, 80);        // Green
    public static final Color WARNING = new Color(255, 152, 0);       // Orange
    public static final Color DANGER = new Color(244, 67, 54);        // Red

    // Neutrals
    public static final Color BACKGROUND = new Color(250, 250, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    // Fonts
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Borders
    public static final int BORDER_RADIUS = 8;
}