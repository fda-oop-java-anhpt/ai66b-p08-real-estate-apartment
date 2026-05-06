package com.oop.project.model;

public class OverallStats implements DTO {
    private final int totalApartments;
    private final int emptyApartments;
    private final int rentedApartments;
    private final double avgPrice;
    private final double avgSize;
    private final double avgLuxuryPrice;
    private final double avgStandardPrice;
    private final double avgBudgetPrice;
    private final double avgLuxurySize;
    private final double avgStandardSize;
    private final double avgBudgetSize;

    public OverallStats(int totalApartments, int emptyApartments, int rentedApartments,
                        double avgPrice, double avgSize,
                        double avgLuxuryPrice, double avgStandardPrice, double avgBudgetPrice,
                        double avgLuxurySize, double avgStandardSize, double avgBudgetSize) {
        this.totalApartments = totalApartments;
        this.emptyApartments = emptyApartments;
        this.rentedApartments = rentedApartments;
        this.avgPrice = avgPrice;
        this.avgSize = avgSize;
        this.avgLuxuryPrice = avgLuxuryPrice;
        this.avgStandardPrice = avgStandardPrice;
        this.avgBudgetPrice = avgBudgetPrice;
        this.avgLuxurySize = avgLuxurySize;
        this.avgStandardSize = avgStandardSize;
        this.avgBudgetSize = avgBudgetSize;
    }

    public int getTotalApartments() { return totalApartments; }
    public int getEmptyApartments() { return emptyApartments; }
    public int getRentedApartments() { return rentedApartments; }
    public double getAvgPrice() { return avgPrice; }
    public double getAvgSize() { return avgSize; }
    public double getAvgLuxuryPrice() { return avgLuxuryPrice; }
    public double getAvgStandardPrice() { return avgStandardPrice; }
    public double getAvgBudgetPrice() { return avgBudgetPrice; }
    public double getAvgLuxurySize() { return avgLuxurySize; }
    public double getAvgStandardSize() { return avgStandardSize; }
    public double getAvgBudgetSize() { return avgBudgetSize; }
}