package org.covidreport;

import org.covidreport.config.AppConfig;
import org.covidreport.thread.ReportProcessingThread;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        try {
            
            String iso = AppConfig.getCountryISO(); // Por defecto: USA
            String fechaStr = AppConfig.getReportDate(); // Formato: yyyy-MM-dd
            
           
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(fechaStr, formatter);

           
            ReportProcessingThread hilo = new ReportProcessingThread(iso, fecha);
            hilo.start();

            
            hilo.join();

            System.out.println("\n✅ Proceso terminado.");
        } catch (Exception e) {
            System.err.println("❌ Error en la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
}