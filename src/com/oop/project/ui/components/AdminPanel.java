package com.oop.project.ui.components;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new BorderLayout());
        JTabbedPane adminTabs = new JTabbedPane();
        adminTabs.addTab("Audit Logs", new AuditLogPanel());
        adminTabs.addTab("Login History", new LoginHistoryPanel());
        add(adminTabs, BorderLayout.CENTER);
    }
}package com.oop.project.ui.components;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new BorderLayout());
        JTabbedPane adminTabs = new JTabbedPane();
        adminTabs.addTab("Audit Logs", new AuditLogPanel());
        adminTabs.addTab("Login History", new LoginHistoryPanel());
        add(adminTabs, BorderLayout.CENTER);
    }
}
