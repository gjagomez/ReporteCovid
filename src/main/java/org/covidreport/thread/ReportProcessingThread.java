package org.covidreport.thread;


import org.covidreport.model.ExecutionLog;
import org.covidreport.service.ExecutionService;
import org.covidreport.service.ReportService;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Thread class for processing COVID-19 reports.
 * Ensures no duplicate processing for the same country and date.
 */
public class ReportProcessingThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ReportProcessingThread.class.getName());
    
    private final String countryISO;
    private final LocalDate reportDate;
    private final ReportService reportService;
    private final ExecutionService executionService;

    /**
     * Creates a new thread with specified parameters
     * 
     * @param countryISO ISO code of the country to process
     * @param reportDate Date for the report
     */
    public ReportProcessingThread(String countryISO, LocalDate reportDate) {
        this(countryISO, reportDate, new ReportService(), new ExecutionService());
    }

    /**
     * Package-private constructor for testing with mocks
     */
    ReportProcessingThread(String countryISO, LocalDate reportDate, ReportService reportService, ExecutionService executionService) {
        this.countryISO = countryISO;
        this.reportDate = reportDate;
        this.reportService = reportService;
        this.executionService = executionService;
    }

    @Override
    public void run() {
        LOGGER.info("Starting report processing for " + countryISO + " on " + reportDate);
        
        try {
            // 1. Verificación previa de ejecución
            if (executionService.isProcessed(countryISO, reportDate)) {
                System.out.println("País " + countryISO + " omitido por ejecución previa.");
                LOGGER.info("Skipping duplicate report for " + countryISO + " on " + reportDate);
                return;
            }

            // 2. Ejecutar flujo completo: Regiones → Provincias → Reporte
            reportService.processReport(countryISO, reportDate);
            
            // 3. Registro posterior a ejecución
            executionService.logExecution(countryISO, reportDate);
            
            LOGGER.info("Successfully completed processing for " + countryISO + " on " + reportDate);
            System.out.println("Datos procesados y guardados para " + countryISO);
            
        } catch (Exception e) {
            LOGGER.severe("Error processing report for " + countryISO + " on " + reportDate + ": " + e.getMessage());
            System.err.println("Error procesando el reporte para " + countryISO + ": " + e.getMessage());
        }
    }
}