package org.covidreport.service;



import org.covidreport.model.ExecutionLog;
import org.covidreport.repository.ExecutionLogRepository;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Service class for managing execution logs to prevent duplicate report processing.
 * Provides methods to check if a report has been processed and to log new executions.
 */
public class ExecutionService {
    private static final Logger LOGGER = Logger.getLogger(ExecutionService.class.getName());
    private final ExecutionLogRepository executionLogRepo = new ExecutionLogRepository();

    /**
     * Checks if a report for the given country and date has already been processed.
     * 
     * @param iso Country ISO code
     * @param date Date of the report
     * @return true if the report was already processed, false otherwise
     */
    public boolean isProcessed(String iso, LocalDate date) {
        boolean processed = executionLogRepo.exists(iso, date);
        if (processed) {
            LOGGER.info("Report already processed for " + iso + " on " + date);
        }
        return processed;
    }

    /**
     * Logs a successful execution in the database.
     * 
     * @param iso Country ISO code
     * @param date Date of the report
     */
    public void logExecution(String iso, LocalDate date) {
        ExecutionLog log = new ExecutionLog(date, iso);
        executionLogRepo.save(log);
        LOGGER.info("Logged execution for " + iso + " on " + date);
    }
}