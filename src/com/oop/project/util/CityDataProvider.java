package com.oop.project.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides city data from environment configuration.
 */
public class CityDataProvider {
    private static List<String> cities;

    static {
        loadCities();
    }

    private static void loadCities() {
        cities = new ArrayList<>();
        try {
            ReadEnv reader = new ReadEnv("/com/oop/project/config/Cities.env");
            Map<String, String> env = reader.load();
            String cityList = env.get("CITY");
            if (cityList != null && !cityList.isEmpty()) {
                cities = Arrays.asList(cityList.split(","));
                // Trim each city name
                cities.replaceAll(String::trim);
            }
        } catch (Exception e) {
            System.err.println("Failed to load cities: " + e.getMessage());
            // Fallback default cities
            cities = Arrays.asList("Hanoi", "Ho Chi Minh City", "Da Nang", "Hue", "Nha Trang", 
                                   "Can Tho", "Ha Long", "Da Lat", "Hoi An");
        }
    }

    /**
     * Returns the list of available cities.
     */
    public static List<String> getCities() {
        return new ArrayList<>(cities);
    }

    /**
     * Reloads cities from the configuration file.
     */
    public static void reload() {
        loadCities();
    }
}