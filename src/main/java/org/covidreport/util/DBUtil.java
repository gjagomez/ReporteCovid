package org.covidreport.util;


import org.covidreport.config.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Utility class for managing database connections using HikariCP connection pool.
 * Provides centralized access to database resources with connection pooling.
 */
public class DBUtil {
    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());
    private static final HikariDataSource dataSource;
    
    static {
        try {
            // Initialize connection pool during class loading
            HikariConfig config = new HikariConfig();
            
            // Load database configuration from properties
            config.setJdbcUrl(AppConfig.getDatabaseProperty("db.url"));
            config.setUsername(AppConfig.getDatabaseProperty("db.user"));
            config.setPassword(AppConfig.getDatabaseProperty("db.password"));
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);
            
            // Performance optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            LOGGER.info("Database connection pool initialized successfully");
            
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize database connection pool: " + e.getMessage());
            throw new ExceptionInInitializerError("Database connection initialization failed: " + e.getMessage());
        }
    }

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private DBUtil() {
        // Utility class should not be instantiated
    }

    /**
     * Gets a database connection from the connection pool
     * 
     * @return Connection object
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.severe("Failed to get database connection: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Returns the HikariDataSource for direct access if needed
     * 
     * @return HikariDataSource instance
     */
    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Closes the connection pool (should be called on application shutdown)
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("Database connection pool closed");
        }
    }
}