package com.oop.project.util;

import java.util.Map;

/**
 * Utility class for automatic apartment categorization
 * based on price (in billions VND) and size (in square meters).
 */
public class ApartmentCategorizer {
    
    private static final Map<String, String> envMap = new ReadEnv("/com/oop/project/config/Connection.env").load();

    // Threshold constants (adjustable based on business rules)
    private static final double LUXURY_PRICE_MIN = Double.parseDouble(envMap.getOrDefault("LUXURY_PRICE_MIN", "10.0"));   // ≥ 10 billion VND
    private static final double LUXURY_SIZE_MIN = Double.parseDouble(envMap.getOrDefault("LUXURY_SIZE_MIN", "90.0"));  // ≥ 90 m²
    private static final double STANDARD_PRICE_MIN = Double.parseDouble(envMap.getOrDefault("STANDARD_PRICE_MIN", "4.0"));  // ≥ 4 billion VND
    private static final double STANDARD_SIZE_MIN = Double.parseDouble(envMap.getOrDefault("STANDARD_SIZE_MIN", "60.0"));  // ≥ 60 m²

    /**
     * Determines the apartment category based on price and size.
     *
     * @param price price in billions VND (must be > 0)
     * @param size  size in square meters (must be > 0)
     * @return "luxury", "standard", or "budget"
     */
    public static String categorize(double price, double size) {
        if (price >= LUXURY_PRICE_MIN && size >= LUXURY_SIZE_MIN) {
            return "luxury";
        } else if (price >= STANDARD_PRICE_MIN && size >= STANDARD_SIZE_MIN) {
            return "standard";
        } else {
            return "budget";
        }
    }

    // Optional overload for integer/float inputs if needed
    public static String categorize(float price, float size) {
        return categorize((double) price, (double) size);
    }
}
