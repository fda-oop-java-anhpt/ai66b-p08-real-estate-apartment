package com.oop.project.ui.components;

import com.oop.project.ui.Theme;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTabbedPane chartTabs;
    private SummaryPanel summaryPanel;
    private BarChartPanel barChart;
    private PieChartPanel pieChart;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        summaryPanel = new SummaryPanel();
        barChart = new BarChartPanel();
        pieChart = new PieChartPanel();

        chartTabs = new JTabbedPane(JTabbedPane.LEFT);
        chartTabs.setFont(Theme.TITLE_FONT);
        chartTabs.setBackground(Theme.SURFACE);

        // Summary first, then the charts
        chartTabs.addTab("Summary", summaryPanel);
        chartTabs.addTab("Bar Chart", barChart);
        chartTabs.addTab("Pie Chart", pieChart);

        add(chartTabs, BorderLayout.CENTER);
    }

    public void refreshDashboard() {
        summaryPanel.refreshData();
        barChart.refreshData();
        pieChart.refreshData();
    }
}