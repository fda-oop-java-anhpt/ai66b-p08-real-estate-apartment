package com.oop.project.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
/*
 * Establishes a connection to MySQL database using credentials from a environment file.
 */
public class ReadEnv {
    private String env_path = "";
    
    public ReadEnv(String env_path) {
        this.env_path = env_path;
    }

    /*
     * Reads the environment file and populates connection parameters.
     */
    public Map load() {
        Map<String, String> envVars = new HashMap<>();
        
        try (InputStream inputStream = getClass().getResourceAsStream(env_path)) {
            if (inputStream == null) {
                System.err.println("Warning: .env resource not found at " + env_path);
                // Fallback to system environment variables
                envVars = System.getenv();
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            envVars.put(key, value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
            envVars = System.getenv();
        }
        
        return envVars;
    }
}
