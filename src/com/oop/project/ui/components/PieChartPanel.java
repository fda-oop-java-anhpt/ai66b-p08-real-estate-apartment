package com.oop.project.ui.components;

import com.oop.project.model.CategoryProportion;
import com.oop.project.service.DashboardService;
import com.oop.project.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DashboardService service = new DashboardService();
    private JComboBox<String> typeCombo;
    private JComboBox<String> cityCombo;
    private List<CategoryProportion> proportions = new ArrayList<>();
    private Timer animTimer;
    private int animationStep = 0;
    private static final int ANIM_STEPS = 20;
    private boolean loading = false;

    private static final List<String> CATEGORY_ORDER = List.of("luxury", "standard", "budget");

    private static final Color[] PIE_COLORS = {
        new Color(33, 150, 243), new Color(76, 175, 80), new Color(255, 152, 0),
        new Color(244, 67, 54), new Color(156, 39, 176)
    };

    public PieChartPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(new JLabel("Data: "));
        typeCombo = new JComboBox<>(new String[]{"Global Categories", "City Categories"});
        typeCombo.setFont(Theme.BODY_FONT);
        typeCombo.addActionListener(e -> { if (!loading) refreshData(); });

        cityCombo = new JComboBox<>();
        cityCombo.setFont(Theme.BODY_FONT);
        cityCombo.setVisible(false);
        cityCombo.addActionListener(e -> { if (!loading) refreshData(); });

        topPanel.add(typeCombo);
        topPanel.add(cityCombo);
        add(topPanel, BorderLayout.NORTH);
    }

    public void refreshData() {
        loading = true;
        try {
            if (typeCombo.getSelectedIndex() == 0) {
                proportions = service.getGlobalCategoryProportions();
                cityCombo.setVisible(false);
            } else {
                List<String> cities = service.getCityStats().stream()
                        .map(cs -> cs.getCity()).distinct().sorted().collect(Collectors.toList());
                String previous = (String) cityCombo.getSelectedItem();
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(cities.toArray(new String[0]));
                cityCombo.setModel(model);
                if (previous != null && cities.contains(previous)) {
                    cityCombo.setSelectedItem(previous);
                } else if (model.getSize() > 0) {
                    cityCombo.setSelectedIndex(0);
                }
                cityCombo.setVisible(true);
                String selectedCity = (String) cityCombo.getSelectedItem();
                proportions = selectedCity != null ?
                        service.getCityCategoryProportions(selectedCity) : new ArrayList<>();
            }

            Map<String, Integer> orderMap = Map.of("luxury", 0, "standard", 1, "budget", 2);
            proportions.sort(Comparator.comparingInt(cp -> orderMap.getOrDefault(cp.getCategory().toLowerCase(), 99)));

            startAnimation();
        } catch (Exception ex) {
            ex.printStackTrace();
            proportions.clear();
            repaint();
        } finally {
            loading = false;
        }
    }

    private void startAnimation() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        animationStep = 0;
        animTimer = new Timer(20, e -> {
            animationStep++;
            repaint();
            if (animationStep >= ANIM_STEPS) animTimer.stop();
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (proportions.isEmpty()) {
            g2.dispose();
            return;
        }

        int w = getWidth();
        int h = getHeight();
        int topMargin = 70;
        int bottomMargin = 40;
        int cx = w / 2;                    // pie remains horizontally centered
        int cy = topMargin + (h - topMargin - bottomMargin) / 2;
        int radius = Math.min(cx, cy) - 60; // unchanged

        int total = proportions.stream().mapToInt(CategoryProportion::getCount).sum();
        if (total == 0) {
            g2.dispose();
            return;
        }

        double angle = 90;
        double factor = Math.min(1.0, (double) animationStep / ANIM_STEPS);

        for (int i = 0; i < proportions.size(); i++) {
            CategoryProportion cp = proportions.get(i);
            double arc = (360.0 * cp.getCount() / total) * factor;
            double midAngle = angle + arc / 2.0;

            // Fill
            g2.setColor(PIE_COLORS[i % PIE_COLORS.length]);
            g2.fillArc(cx - radius, cy - radius, radius * 2, radius * 2, (int) angle, (int) arc);

            // Border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawArc(cx - radius, cy - radius, radius * 2, radius * 2, (int) angle, (int) arc);

            // Percentage inside slice
            double percent = 100.0 * cp.getCount() / total;
            String text = String.format("%.1f%%", percent);
            g2.setColor(Color.WHITE);
            Font percentFont = Theme.BODY_FONT.deriveFont(Font.BOLD, 15f);
            g2.setFont(percentFont);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent();
            double textAngle = Math.toRadians(midAngle);
            int textRadius = (int) (radius * 0.65);
            int tx = (int) (cx + textRadius * Math.cos(textAngle)) - textW / 2;
            int ty = (int) (cy - textRadius * Math.sin(textAngle)) + textH / 4;
            g2.drawString(text, tx, ty);

            angle += arc;
        }

        // Left legend – vertical alignment, larger bold font, fixed order
        Font legendFont = Theme.BODY_FONT.deriveFont(Font.BOLD, 16f);
        g2.setFont(legendFont);
        FontMetrics lfm = g2.getFontMetrics();
        int lineHeight = lfm.getHeight() + 8;
        int totalLegendHeight = proportions.size() * lineHeight;
        int legendStartY = cy - totalLegendHeight / 2 + lfm.getAscent() / 2;
        int legendX = 20;   // fixed left offset

        for (int i = 0; i < proportions.size(); i++) {
            CategoryProportion cp = proportions.get(i);
            int boxY = legendStartY + i * lineHeight;

            // Color box
            g2.setColor(PIE_COLORS[i % PIE_COLORS.length]);
            g2.fillRect(legendX, boxY - 12, 14, 14);

            // Category name and count
            g2.setColor(Theme.TEXT_PRIMARY);
            String labelText = cp.getCategory() + " (" + cp.getCount() + ")";
            g2.drawString(labelText, legendX + 20, boxY);
        }

        g2.dispose();
    }
}