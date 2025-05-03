package org.covidreport.repository;



import org.covidreport.model.Report;
import org.covidreport.model.Region;
import org.covidreport.util.DBUtil;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Repository class for managing Report entities in the database.
 * Handles saving and retrieving COVID-19 reports with region and city data.
 */
public class ReportRepository {
    private static final Logger LOGGER = Logger.getLogger(ReportRepository.class.getName());

    /**
     * Saves a report and its associated cities to the database.
     * Uses INSERT IGNORE to handle duplicates gracefully.
     * 
     * @param report The Report object to be saved
     * @throws RuntimeException if database operation fails
     */
    public void save(Report report) {
        String insertReportSQL = "INSERT IGNORE INTO reports " +
            "(date, confirmed, deaths, recovered, confirmed_diff, deaths_diff, recovered_diff, " +
            "last_update, active, active_diff, fatality_rate, region_iso, region_name, region_province) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String insertCitySQL = "INSERT IGNORE INTO cities " +
            "(report_date, region_province, city_name, fips, lat, lng, " +
            "confirmed, deaths, confirmed_diff, deaths_diff, last_update) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement reportStmt = connection.prepareStatement(insertReportSQL);
             PreparedStatement cityStmt = connection.prepareStatement(insertCitySQL)) {
            
            // Set parameters for report
            setReportParameters(reportStmt, report);
            
            int rowsAffected = reportStmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Saved report for " + report.getRegion().getProvince());
                
                // Save cities if available
                for (Region.City city : report.getRegion().getCities()) {
                    setCityParameters(cityStmt, report.getDate(), report.getRegion().getProvince(), city);
                    cityStmt.addBatch();
                }
                cityStmt.executeBatch();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            LOGGER.info("Duplicate report skipped: " + report.getRegion().getProvince() + " - " + report.getDate());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database operation failed", e);
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves reports for a specific country and date, grouped by province.
     * 
     * @param iso Country ISO code
     * @param date Date of the report
     * @return TreeMap with province names as keys and reports as values
     * @throws RuntimeException if database operation fails
     */
    public TreeMap<String, Report> findByCountryAndDate(String iso, LocalDate date) {
        String query = "SELECT * FROM reports WHERE region_iso = ? AND date = ?";
        TreeMap<String, Report> reportMap = new TreeMap<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, iso);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Report report = buildReportFromResultSet(rs);
                    reportMap.put(report.getRegion().getProvince(), report);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database query failed", e);
            throw new RuntimeException("Database query failed: " + e.getMessage(), e);
        }

        return reportMap;
    }

    /**
     * Sets parameters for the report prepared statement.
     */
    private void setReportParameters(PreparedStatement stmt, Report report) throws SQLException {
        Region region = report.getRegion();
        
        stmt.setDate(1, java.sql.Date.valueOf(report.getDate()));
        stmt.setInt(2, report.getConfirmed());
        stmt.setInt(3, report.getDeaths());
        stmt.setInt(4, report.getRecovered());
        stmt.setInt(5, report.getConfirmedDiff());
        stmt.setInt(6, report.getDeathsDiff());
        stmt.setInt(7, report.getRecoveredDiff());
        stmt.setTimestamp(8, Timestamp.valueOf(report.getLastUpdate()));
        stmt.setInt(9, report.getActive());
        stmt.setInt(10, report.getActiveDiff());
        stmt.setDouble(11, report.getFatalityRate());
        stmt.setString(12, region.getIso());
        stmt.setString(13, region.getName());
        stmt.setString(14, region.getProvince());
    }

    /**
     * Sets parameters for the city prepared statement.
     */
    private void setCityParameters(PreparedStatement stmt, LocalDate reportDate, String province, Region.City city) throws SQLException {
        stmt.setDate(1, java.sql.Date.valueOf(reportDate));
        stmt.setString(2, province);
        stmt.setString(3, city.getName());
        stmt.setInt(4, city.getFips());
        stmt.setDouble(5, city.getLatitude());
        stmt.setDouble(6, city.getLongitude());
        stmt.setInt(7, city.getConfirmed());
        stmt.setInt(8, city.getDeaths());
        stmt.setInt(9, city.getConfirmedDiff());
        stmt.setInt(10, city.getDeathsDiff());
        stmt.setTimestamp(11, Timestamp.valueOf(city.getLastUpdate()));
    }

    /**
     * Builds a Report object from a database result set.
     */
    private Report buildReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report();
        Region region = new Region();
        
        report.setDate(rs.getDate("date").toLocalDate());
        report.setConfirmed(rs.getInt("confirmed"));
        report.setDeaths(rs.getInt("deaths"));
        report.setRecovered(rs.getInt("recovered"));
        report.setConfirmedDiff(rs.getInt("confirmed_diff"));
        report.setDeathsDiff(rs.getInt("deaths_diff"));
        report.setRecoveredDiff(rs.getInt("recovered_diff"));
        report.setLastUpdate(rs.getTimestamp("last_update").toLocalDateTime());
        report.setActive(rs.getInt("active"));
        report.setActiveDiff(rs.getInt("active_diff"));
        report.setFatalityRate(rs.getDouble("fatality_rate"));
        
        region.setIso(rs.getString("region_iso"));
        region.setName(rs.getString("region_name"));
        region.setProvince(rs.getString("region_province"));
        
        report.setRegion(region);
        
        return report;
    }
}