package org.covidreport.model;


import java.time.LocalDate;

/**
 * Represents a record of completed report executions to prevent duplicate processing.
 * This class maps to the 'executed_reports' database table and contains information
 * about when a specific country's report was last processed.
 */
public class ExecutionLog {
    /**
     * The date when the report execution was completed
     */
    private LocalDate executionDate;
    
    /**
     * The ISO code of the country whose report was processed
     * Example: "USA", "GTM"
     */
    private String countryISO;

    /**
     * Default constructor for framework compatibility
     */
    public ExecutionLog() {
        // Default constructor for frameworks (e.g., ORMs, serialization)
    }

    /**
     * Creates a new execution log record
     * 
     * @param executionDate The date when the execution completed
     * @param countryISO    The ISO code of the processed country
     */
    public ExecutionLog(LocalDate executionDate, String countryISO) {
        this.executionDate = executionDate;
        this.countryISO = countryISO;
    }

    // Getters and Setters

    /**
     * Gets the execution date
     * @return LocalDate representing when the report was processed
     */
    public LocalDate getExecutionDate() {
        return executionDate;
    }

    /**
     * Sets the execution date
     * @param executionDate LocalDate to set
     */
    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    /**
     * Gets the country ISO code
     * @return String representing the country code
     */
    public String getCountryISO() {
        return countryISO;
    }

    /**
     * Sets the country ISO code
     * @param countryISO String to set
     */
    public void setCountryISO(String countryISO) {
        this.countryISO = countryISO;
    }

    /**
     * Returns a string representation of the execution log
     * @return String containing execution details
     */
    @Override
    public String toString() {
        return "ExecutionLog{" +
                "executionDate=" + executionDate +
                ", countryISO='" + countryISO + '\'' +
                '}';
    }

    /**
     * Checks if two ExecutionLog objects are equal
     * @param obj Object to compare
     * @return boolean indicating equality
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ExecutionLog that = (ExecutionLog) obj;
        
        // Null checks could be added in production code
        return executionDate.equals(that.executionDate) && 
               countryISO.equals(that.countryISO);
    }

    /**
     * Generates a hash code for the execution log
     * @return int hash code value
     */
    @Override
    public int hashCode() {
        int result = executionDate.hashCode();
        result = 31 * result + countryISO.hashCode();
        return result;
    }
}