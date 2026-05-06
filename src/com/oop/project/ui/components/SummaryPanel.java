package com.oop.project.ui.components;

import com.oop.project.model.OverallStats;
import com.oop.project.service.DashboardService;
import com.oop.project.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SummaryPanel extends JPanel {
    private DashboardService service = new DashboardService();
    private JLabel totalApts, emptyApts, rentedApts;
    private JLabel avgPrice, avgLuxPrice, avgStdPrice, avgBudPrice;
    private JLabel avgSize, avgLuxSize, avgStdSize, avgBudSize;

    public SummaryPanel() {
        setBackground(Theme.BACKGROUND);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        layoutComponents();
        refreshData();
    }

    private void initComponents() {
        // Create labels for each metric – they will be updated later
        totalApts = createValueLabel();
        emptyApts = createValueLabel();
        rentedApts = createValueLabel();
        avgPrice = createValueLabel();
        avgLuxPrice = createValueLabel();
        avgStdPrice = createValueLabel();
        avgBudPrice = createValueLabel();
        avgSize = createValueLabel();
        avgLuxSize = createValueLabel();
        avgStdSize = createValueLabel();
        avgBudSize = createValueLabel();
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(Theme.HEADER_FONT.deriveFont(Font.BOLD, 24f));
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setOpaque(false);
        return label;
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Row 1 – Counts
        addCard("Total Apartments", totalApts, new Color(33, 150, 243), 0, 0, gbc);
        addCard("Empty Apartments", emptyApts, new Color(76, 175, 80), 1, 0, gbc);
        addCard("Rented Apartments", rentedApts, new Color(255, 152, 0), 2, 0, gbc);

        // Row 2 – Average Prices
        addCard("Average Price", avgPrice, new Color(244, 67, 54), 0, 1, gbc);
        addCard("Avg Luxury Price", avgLuxPrice, new Color(156, 39, 176), 1, 1, gbc);
        addCard("Avg Standard Price", avgStdPrice, new Color(0, 188, 212), 2, 1, gbc);
        addCard("Avg Budget Price", avgBudPrice, new Color(255, 87, 34), 3, 1, gbc);

        // Row 3 – Average Sizes
        addCard("Average Size", avgSize, new Color(63, 81, 181), 0, 2, gbc);
        addCard("Avg Luxury Size", avgLuxSize, new Color(46, 125, 50), 1, 2, gbc);
        addCard("Avg Standard Size", avgStdSize, new Color(1, 87, 155), 2, 2, gbc);
        addCard("Avg Budget Size", avgBudSize, new Color(230, 81, 0), 3, 2, gbc);
    }

    private void addCard(String title, JLabel valueLabel, Color bgColor, int x, int y, GridBagConstraints gbc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        // Rounded corners via custom paint? We'll just use solid background for simplicity
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(Color.WHITE);
        valueLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        gbc.gridx = x;
        gbc.gridy = y;
        add(card, gbc);
    }

    public void refreshData() {
        try {
            OverallStats stats = service.getOverallStats();
            totalApts.setText(String.valueOf(stats.getTotalApartments()));
            emptyApts.setText(String.valueOf(stats.getEmptyApartments()));
            rentedApts.setText(String.valueOf(stats.getRentedApartments()));
            avgPrice.setText(String.format("%.2f", stats.getAvgPrice()));
            avgLuxPrice.setText(String.format("%.2f", stats.getAvgLuxuryPrice()));
            avgStdPrice.setText(String.format("%.2f", stats.getAvgStandardPrice()));
            avgBudPrice.setText(String.format("%.2f", stats.getAvgBudgetPrice()));
            avgSize.setText(String.format("%.1f", stats.getAvgSize()));
            avgLuxSize.setText(String.format("%.1f", stats.getAvgLuxurySize()));
            avgStdSize.setText(String.format("%.1f", stats.getAvgStandardSize()));
            avgBudSize.setText(String.format("%.1f", stats.getAvgBudgetSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}