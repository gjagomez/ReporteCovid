package org.covidreport.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for application properties
 * Loads settings from application.properties file
 */
public class AppConfig {
    /**
     * Singleton instance of application properties
     */
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties file not found in resources");
            }
            // Load properties from file
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration file", ex);
        }
    }

    /**
     * Gets the configured report date
     * @return String containing the date in yyyy-MM-dd format
     */
    public static String getReportDate() {
        String date = properties.getProperty("covid.report.date");
        if (date == null || date.isEmpty()) {
            throw new RuntimeException("Missing required configuration: covid.report.date");
        }
        return date;
    }

    /**
     * Gets the configured country ISO code
     * @return String containing the ISO code (default: USA)
     */
    public static String getCountryISO() {
        return properties.getProperty("covid.report.iso", "USA");
    }

    /**
     * Gets a database configuration property
     * @param key Property name to retrieve
     * @return Property value as String
     */
    public static String getDatabaseProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Missing database configuration: " + key);
        }
        return value;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private AppConfig() {
        // Utility class should not be instantiated
    }

    /**
     * Prints all loaded properties (for debugging purposes)
     */
    public static void printProperties() {
        properties.forEach((key, value) -> 
            System.out.println(key + ": " + value));
    }
}