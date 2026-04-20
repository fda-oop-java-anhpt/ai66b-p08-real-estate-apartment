package com.oop.project.ui;

import com.oop.project.model.User;
import com.oop.project.service.AuthenticationController;
import com.oop.project.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    // Input fields
    private StyledTextField usernameField;
    private StyledPasswordField passwordField;
    private StyledPasswordField confirmPasswordField;
    private StyledComboBox<String> roleComboBox;
    private JCheckBox showPasswordCheckBox;

    // Buttons
    private StyledButton loginButton;
    private StyledButton registerButton;

    // Toggle between login and register modes
    private JLabel toggleModeLabel;
    private boolean isRegisterMode = false;

    private final AuthenticationController authController;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public LoginScreen() {
        authController = new AuthenticationController();

        setTitle("Real Estate Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        layoutComponents();
        applyStyling();
        updateModeUI(); // Start in login mode
    }

    private void initComponents() {
        usernameField = new StyledTextField();
        passwordField = new StyledPasswordField();
        confirmPasswordField = new StyledPasswordField();
        roleComboBox = new StyledComboBox<>(new String[]{"admin", "agent"});

        loginButton = new StyledButton("Login", PRIMARY_COLOR);
        registerButton = new StyledButton("Register", ACCENT_COLOR);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setForeground(TEXT_COLOR);
        showPasswordCheckBox.setBackground(Color.WHITE);
        showPasswordCheckBox.addActionListener(e -> {
            boolean show = showPasswordCheckBox.isSelected();
            passwordField.setPasswordVisible(show);
            confirmPasswordField.setPasswordVisible(show);
        });

        // Toggle link
        toggleModeLabel = new JLabel("Don't have an account? Register here");
        toggleModeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toggleModeLabel.setForeground(PRIMARY_COLOR);
        toggleModeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleModeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                isRegisterMode = !isRegisterMode;
                updateModeUI();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                toggleModeLabel.setText(isRegisterMode ? "Already have an account? Login here" : "Don't have an account? Register here");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                toggleModeLabel.setText(isRegisterMode ? "← Back to Login" : "Don't have an account? Register here");
            }
        });

        // Action listeners
        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(new RegisterAction());

        // Enter key triggers appropriate action
        usernameField.addActionListener(e -> submitAction());
        passwordField.addActionListener(e -> submitAction());
        confirmPasswordField.addActionListener(e -> submitAction());
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Title
        JLabel titleLabel = new JLabel("🏠 Real Estate Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        addLabelAndField(mainPanel, gbc, 1, "Username:", usernameField);

        // Password
        addLabelAndField(mainPanel, gbc, 2, "Password:", passwordField);

        // Confirm Password (only visible in register mode)
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmLabel.setForeground(TEXT_COLOR);
        confirmLabel.setName("confirmLabel");
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(confirmLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);
        confirmPasswordField.setName("confirmField");

        // Show password checkbox
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(showPasswordCheckBox, gbc);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(roleComboBox, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 5, 10, 5);
        mainPanel.add(buttonPanel, gbc);

        // Toggle mode link
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(toggleModeLabel, gbc);

        add(mainPanel);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void applyStyling() {
        getContentPane().setBackground(Color.WHITE);
    }

    private void updateModeUI() {
        // Show/hide confirm password field and label
        Component[] components = getContentPane().getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel) c).getComponents()) {
                    if ("confirmLabel".equals(inner.getName()) || "confirmField".equals(inner.getName())) {
                        inner.setVisible(isRegisterMode);
                    }
                }
            }
        }

        // Update button texts and toggle label
        if (isRegisterMode) {
            loginButton.setText("Cancel");
            registerButton.setText("Create Account");
            toggleModeLabel.setText("← Back to Login");
            setTitle("Real Estate Management System - Register");
        } else {
            loginButton.setText("Login");
            registerButton.setText("Register");
            toggleModeLabel.setText("Don't have an account? Register here");
            setTitle("Real Estate Management System - Login");
        }

        // Clear password fields when switching modes
        passwordField.setText("");
        confirmPasswordField.setText("");
        showPasswordCheckBox.setSelected(false);
        passwordField.setPasswordVisible(false);
        confirmPasswordField.setPasswordVisible(false);

        // Re-layout
        revalidate();
        repaint();
    }

    private void submitAction() {
        if (isRegisterMode) {
            registerButton.doClick();
        } else {
            loginButton.doClick();
        }
    }

    // ---------- Validation ----------
    private boolean validateLoginInput() {
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username cannot be empty.", "Validation Error");
            usernameField.requestFocus();
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            showError("Password cannot be empty.", "Validation Error");
            passwordField.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateRegisterInput() {
        if (!validateLoginInput()) return false;

        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirm)) {
            showError("Passwords do not match.", "Validation Error");
            confirmPasswordField.requestFocus();
            return false;
        }
        if (password.length() < 4) {
            showError("Password must be at least 4 characters long.", "Validation Error");
            passwordField.requestFocus();
            return false;
        }
        return true;
    }

    // ---------- UI Helpers ----------
    private void setControlsEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        confirmPasswordField.setEnabled(enabled);
        roleComboBox.setEnabled(enabled);
        showPasswordCheckBox.setEnabled(enabled);
        toggleModeLabel.setEnabled(enabled);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean confirmRegistration(String username, String role) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Register new user:\nUsername: " + username + "\nRole: " + role,
                "Confirm Registration",
                JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }

    private void proceedToMainApp(User user) {
        // TODO: Replace with actual main application window
        JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getUsername() + "! Main application not yet implemented.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Action Listeners ----------
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isRegisterMode) {
                // Cancel registration mode
                isRegisterMode = false;
                updateModeUI();
                return;
            }

            if (!validateLoginInput()) return;

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            setControlsEnabled(false);

            SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
                @Override
                protected User doInBackground() throws Exception {
                    return authController.login(username, password, role);
                }

                @Override
                protected void done() {
                    try {
                        User user = get();
                        showInfo("Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")",
                                 "Success");
                        proceedToMainApp(user);
                    } catch (Exception ex) {
                        String message = ex.getMessage();
                        if (ex.getCause() != null) message = ex.getCause().getMessage();
                        showError("Login failed: " + message, "Login Error");
                    } finally {
                        setControlsEnabled(true);
                        passwordField.setText("");
                        showPasswordCheckBox.setSelected(false);
                        passwordField.setPasswordVisible(false);
                    }
                }
            };
            worker.execute();
        }
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isRegisterMode) {
                // Switch to register mode
                isRegisterMode = true;
                updateModeUI();
                return;
            }

            if (!validateRegisterInput()) return;

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (!confirmRegistration(username, role)) return;

            setControlsEnabled(false);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    authController.register(username, password, role);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        showInfo("User registered successfully! You can now log in.", "Registration Success");
                        // Switch back to login mode
                        isRegisterMode = false;
                        updateModeUI();
                        usernameField.requestFocus();
                    } catch (Exception ex) {
                        String message = ex.getMessage();
                        if (ex.getCause() != null) message = ex.getCause().getMessage();
                        if (message != null && message.toLowerCase().contains("duplicate")) {
                            message = "Username already exists. Please choose another.";
                        }
                        showError("Registration failed: " + message, "Registration Error");
                    } finally {
                        setControlsEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
