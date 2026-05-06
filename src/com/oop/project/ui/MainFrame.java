package com.oop.project.ui;

import com.oop.project.model.User;
import com.oop.project.ui.components.*;
import com.oop.project.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private User currentUser;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private JButton logoutButton;

    private ApartmentPanel apartmentPanel;
    private FavoritesPanel favoritesPanel;
    private AdminPanel adminPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Session expired. Please login again.");
            new LoginScreen().setVisible(true);
            dispose();
            return;
        }

        setTitle("Real Estate Manager");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);

        initComponents();
        layoutComponents();
        styleComponents();
        applyRoleBasedVisibility();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        welcomeLabel = new JLabel();
        updateWelcomeMessage();

        logoutButton = new StyledButton("Logout", Theme.DANGER, 6);
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.addActionListener(e -> confirmLogout());

        apartmentPanel = new ApartmentPanel();
        favoritesPanel = new FavoritesPanel();
        adminPanel = new AdminPanel();
        dashboardPanel = new DashboardPanel();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Top bar with welcome message and logout
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.SURFACE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
                new EmptyBorder(10, 20, 10, 20)
        ));

        welcomeLabel.setFont(Theme.HEADER_FONT);
        welcomeLabel.setForeground(Theme.TEXT_PRIMARY);
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Apartments", apartmentPanel);
        tabbedPane.addTab("Favorites", favoritesPanel);
        add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) { // Dashboard
                dashboardPanel.refreshDashboard();
            } else if (idx == 2) { // Favorites
                favoritesPanel.loadFavorites();
            }
        });   
    }

    private void styleComponents() {
        // Custom tab styling
        tabbedPane.setFont(Theme.TITLE_FONT);
        tabbedPane.setBackground(Theme.SURFACE);
        tabbedPane.setForeground(Theme.TEXT_PRIMARY);
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = Theme.PRIMARY;
                lightHighlight = Theme.PRIMARY_DARK;
                shadow = new Color(200, 200, 200);
                darkShadow = new Color(180, 180, 180);
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(Theme.SURFACE);
                    g2.fillRoundRect(x, y, w, h, 8, 8);
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRect(x, y + h - 3, w, 3);
                } else {
                    g2.setColor(Theme.BACKGROUND);
                    g2.fillRoundRect(x, y, w, h, 8, 8);
                }
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                // No border, handled in paintTabBackground
            }
        });
    }

    private void applyRoleBasedVisibility() {
        if (currentUser.isAdmin()) {
            tabbedPane.addTab("Admin", adminPanel);
        }
    }

    private void updateWelcomeMessage() {
        welcomeLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
    }

    private void confirmLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            performLogout();
        }
    }

    private void performLogout() {
        SessionManager.clearSession();
        dispose();
        new LoginScreen().setVisible(true);
    }
    
    public void refreshFavoritesTab() {
        if (favoritesPanel != null) {
            favoritesPanel.loadFavorites();
        }
    }
    
    public void refreshMainListingFavorites() {
        if (apartmentPanel != null) {
            apartmentPanel.loadFavoriteIds();
            apartmentPanel.repaint();  // repaint table to update heart icons & golden rows
        }
    }
    
    public void refreshDashboard() {
        if (dashboardPanel != null) {
            dashboardPanel.refreshDashboard();
        }
    }
}
