package com.oop.project.ui.components;

import com.oop.project.model.CityStats;
import com.oop.project.service.DashboardService;
import com.oop.project.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BarChartPanel extends JPanel {
    private final DashboardService service = new DashboardService();
    private JComboBox<String> metricCombo;
    private JComboBox<String> sortCombo;
    private List<CityStats> cityStats = new ArrayList<>();
    private List<Double> targetValues = new ArrayList<>();
    private List<Double> currentValues = new ArrayList<>();
    private Timer animTimer;
    private int animationStep = 0;
    private static final int ANIM_STEPS = 20;

    private static final Color[] BAR_COLORS = {
        new Color(33, 150, 243), new Color(76, 175, 80), new Color(255, 152, 0),
        new Color(244, 67, 54), new Color(156, 39, 176), new Color(0, 188, 212),
        new Color(255, 87, 34), new Color(63, 81, 181)
    };

    public BarChartPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(new JLabel("Metric:"));
        metricCombo = new JComboBox<>(new String[]{"Average Price", "Average Size", "Apartment Count"});
        metricCombo.setFont(Theme.BODY_FONT);
        metricCombo.addActionListener(e -> refreshData());
        leftPanel.add(metricCombo);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(new JLabel("Sort:"));
        sortCombo = new JComboBox<>(new String[]{"Name A-Z", "Value Descending"});
        sortCombo.setFont(Theme.BODY_FONT);
        sortCombo.addActionListener(e -> refreshData());
        rightPanel.add(sortCombo);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    public void refreshData() {
        try {
            cityStats = service.getCityStats();

            targetValues.clear();
            int metricIdx = metricCombo.getSelectedIndex();
            for (CityStats cs : cityStats) {
                switch (metricIdx) {
                    case 0: targetValues.add(cs.getAvgPrice()); break;
                    case 1: targetValues.add(cs.getAvgSize()); break;
                    case 2: targetValues.add((double) cs.getApartmentCount()); break;
                }
            }

            if (sortCombo.getSelectedIndex() == 1) {
                List<Integer> indices = new ArrayList<>();
                for (int i = 0; i < cityStats.size(); i++) indices.add(i);
                indices.sort((a, b) -> Double.compare(targetValues.get(b), targetValues.get(a)));
                List<CityStats> sortedStats = new ArrayList<>();
                List<Double> sortedValues = new ArrayList<>();
                for (int idx : indices) {
                    sortedStats.add(cityStats.get(idx));
                    sortedValues.add(targetValues.get(idx));
                }
                cityStats = sortedStats;
                targetValues = sortedValues;
            } else {
                cityStats.sort(Comparator.comparing(CityStats::getCity));
                targetValues.clear();
                for (CityStats cs : cityStats) {
                    switch (metricIdx) {
                        case 0: targetValues.add(cs.getAvgPrice()); break;
                        case 1: targetValues.add(cs.getAvgSize()); break;
                        case 2: targetValues.add((double) cs.getApartmentCount()); break;
                    }
                }
            }

            currentValues = new ArrayList<>();
            for (int i = 0; i < targetValues.size(); i++) currentValues.add(0.0);
            animationStep = 0;
            if (animTimer != null && animTimer.isRunning()) animTimer.stop();
            animTimer = new Timer(20, e -> {
                animationStep++;
                double factor = Math.min(1.0, (double) animationStep / ANIM_STEPS);
                for (int i = 0; i < targetValues.size(); i++) {
                    currentValues.set(i, targetValues.get(i) * factor);
                }
                repaint();
                if (animationStep >= ANIM_STEPS) animTimer.stop();
            });
            animTimer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            cityStats.clear();
            targetValues.clear();
            currentValues.clear();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cityStats.isEmpty() || currentValues.isEmpty()) {
            g2.dispose();
            return;
        }

        // Increased margins to keep chart away from top border
        int marginLeft = 80;
        int marginRight = 40;
        int marginTop = 100;      // more space below the control panel
        int marginBottom = 60;

        int chartX = marginLeft;
        int chartY = marginTop;
        int chartW = getWidth() - marginLeft - marginRight;
        int chartH = getHeight() - marginTop - marginBottom;

        double maxVal = targetValues.stream().max(Double::compare).orElse(1.0);
        if (maxVal == 0) maxVal = 1;
        int barCount = Math.max(1, cityStats.size());
        int barWidth = Math.max(10, chartW / barCount - 10);

        // Axes
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);
        g2.drawLine(chartX, chartY, chartX, chartY + chartH);

        for (int i = 0; i < barCount; i++) {
            double val = currentValues.get(i);
            int barHeight = (int) (val / maxVal * chartH);
            int x = chartX + i * (chartW / barCount);
            int y = chartY + chartH - barHeight;

            GradientPaint gp = new GradientPaint(x, y, BAR_COLORS[i % BAR_COLORS.length],
                                                 x, y + barHeight, BAR_COLORS[i % BAR_COLORS.length].darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x + 5, y, barWidth, barHeight, 10, 10);

            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(x + 7, y + 2, barWidth, barHeight, 10, 10);

            // Value label above bar
            g2.setColor(Theme.TEXT_PRIMARY);
            Font valueFont = Theme.BODY_FONT.deriveFont(Font.BOLD, 14f);
            g2.setFont(valueFont);
            String valueStr;
            int metricIdx = metricCombo.getSelectedIndex();
            switch (metricIdx) {
                case 0: valueStr = String.format("%.1f", val); break;
                case 1: valueStr = String.format("%.0f", val); break;
                default: valueStr = String.valueOf((int) Math.round(val));
            }
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(valueStr);
            g2.drawString(valueStr, x + 5 + (barWidth - textW) / 2, y - 6);

            // City label below axis
            g2.setFont(Theme.BODY_FONT.deriveFont(13f));
            String city = cityStats.get(i).getCity();
            fm = g2.getFontMetrics();
            int labelW = fm.stringWidth(city);
            g2.drawString(city, x + 5 + (barWidth - labelW) / 2, chartY + chartH + 18);
        }
        g2.dispose();
    }
}