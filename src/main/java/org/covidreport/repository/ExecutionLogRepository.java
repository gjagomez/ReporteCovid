package org.covidreport.repository;



import org.covidreport.model.ExecutionLog;
import org.covidreport.util.DBUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Repository class for managing execution logs in the database.
 * Prevents duplicate report processing by tracking completed executions.
 */
public class ExecutionLogRepository {
    private static final Logger LOGGER = Logger.getLogger(ExecutionLogRepository.class.getName());

    /**
     * Checks if a report has already been processed for the given country and date
     * 
     * @param iso Country ISO code
     * @param date Date to check
     * @return true if the report was already processed, false otherwise
     * @throws RuntimeException if database operation fails
     */
    public boolean exists(String iso, LocalDate date) {
        String query = "SELECT COUNT(*) FROM executed_reports WHERE execution_date = ? AND country_iso = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setString(2, iso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        LOGGER.info("Report already processed for " + iso + " on " + date);
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking execution log", e);
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        
        return false;
    }

    /**
     * Saves a new execution log to the database
     * 
     * @param log ExecutionLog object to save
     * @throws RuntimeException if database operation fails
     */
    public void save(ExecutionLog log) {
        String insertSQL = "INSERT INTO executed_reports (execution_date, country_iso) VALUES (?, ?)";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(log.getExecutionDate()));
            stmt.setString(2, log.getCountryISO());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Execution log saved for " + log.getCountryISO() + " on " + log.getExecutionDate());
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            LOGGER.info("Duplicate execution log skipped: " + log.getCountryISO() + " - " + log.getExecutionDate());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving execution log", e);
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }
}