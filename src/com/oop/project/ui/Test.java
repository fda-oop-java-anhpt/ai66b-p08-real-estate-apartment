package com.oop.project.ui;

import javax.swing.SwingUtilities;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApartmentWorkspaceFrame app = new ApartmentWorkspaceFrame();
            app.setVisible(true);
        });
    }
}
