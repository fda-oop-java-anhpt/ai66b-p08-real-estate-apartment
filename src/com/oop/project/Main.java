package com.oop.project;
import com.oop.project.ui.*;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApartmentWorkspaceFrame app = new ApartmentWorkspaceFrame();
            app.setVisible(true);
        });
    }
}
