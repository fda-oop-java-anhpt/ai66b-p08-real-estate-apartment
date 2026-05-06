package com.oop.project.model;

public class CityStats implements DTO {
    private final String city;
    private final int apartmentCount;
    private final double avgPrice;
    private final double avgSize;

    public CityStats(String city, int apartmentCount, double avgPrice, double avgSize) {
        this.city = city;
        this.apartmentCount = apartmentCount;
        this.avgPrice = avgPrice;
        this.avgSize = avgSize;
    }

    public String getCity() { return city; }
    public int getApartmentCount() { return apartmentCount; }
    public double getAvgPrice() { return avgPrice; }
    public double getAvgSize() { return avgSize; }
}