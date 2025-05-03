package org.covidreport.service;

import org.covidreport.model.Report;
import org.covidreport.model.ExecutionLog;
import org.covidreport.model.Region;
import org.covidreport.repository.ExecutionLogRepository;
import org.covidreport.repository.ReportRepository;
import org.covidreport.util.DBUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.TreeMap;

/**
 * Service class for handling business logic related to COVID-19 reports.
 * Implements core functionality for processing reports, preventing duplicates,
 * and providing data querying capabilities.
 */
public class ReportService {
    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
    
    // Configuración de la API
    private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/reports";
    private static final String API_KEY = "b444ef1fffmshdf0295dcb01d536p108cf1jsnea6d75e5c08d";
    private static final String API_HOST = "covid-19-statistics.p.rapidapi.com";
    
    // Configuración de reintentos
    private static final int MAX_RETRIES = 3;
    private static final int BASE_DELAY_MS = 3000;
    
    private final ReportRepository reportRepo = new ReportRepository();
    private final ExecutionLogRepository executionLogRepo = new ExecutionLogRepository();

    /**
     * Processes a report for the given country and date.
     * Checks for duplicates before processing and logs execution after success.
     * 
     * @param iso Country ISO code
     * @param date Date of the report
     */
    public void processReport(String iso, LocalDate date) {
        if (isProcessed(iso, date)) {
            LOGGER.info("Report already processed for " + iso + " on " + date);
            System.out.println("País " + iso + " omitido por ejecución previa.");
            return;
        }

        try {
            List<Report> reports = fetchFromAPI(iso, date);
            for (Report report : reports) {
                reportRepo.save(report);
            }
            logExecution(iso, date);
            System.out.println("Datos procesados y guardados para " + iso);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing report for " + iso + " on " + date, e);
            System.err.println("Error procesando el reporte: " + e.getMessage());
        }
    }

    /**
     * Fetches data from the external API for the given country and date.
     * Implements retry logic and timeout handling.
     * 
     * @param iso Country ISO code
     * @param date Date to fetch
     * @return List of Report objects
     * @throws IOException if API call fails
     */
    private List<Report> fetchFromAPI(String iso, LocalDate date) throws IOException {
        List<Report> reports = new ArrayList<>();
        String requestUrl = buildRequestUrl(iso, date);
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(requestUrl);
                connection = (HttpURLConnection) url.openConnection();
                setupConnection(connection);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String jsonResponse = readResponse(connection.getInputStream());
                    reports = parseReports(jsonResponse);
                    break; // Exit loop on success
                } else {
                    String errorResponse = readResponse(connection.getErrorStream());
                    System.err.println("Intento " + attempt + " fallido. HTTP " + responseCode + ": " + errorResponse);
                    
                    if (attempt == MAX_RETRIES) {
                        throw new IOException("Máximo de intentos alcanzado. Último error: " + errorResponse);
                    }
                    
                    // Exponential backoff
                    int delay = BASE_DELAY_MS * (int) Math.pow(2, attempt - 1);
                    System.out.println("Reintentando en " + (delay / 1000) + " segundos...");
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Hilo interrumpido durante reintento", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        
        return reports;
    }

    /**
     * Builds the API request URL with parameters.
     */
    private String buildRequestUrl(String iso, LocalDate date) {
        return API_URL + "?iso=" + iso + "&region_name=US&date=" + date.toString();
    }

    /**
     * Sets up HTTP connection properties for the API request.
     */
    private void setupConnection(HttpURLConnection connection) {
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-rapidapi-host", API_HOST);
            connection.setRequestProperty("x-rapidapi-key", API_KEY);
            
            // Aumentar tiempos de espera
            connection.setConnectTimeout(10000);  // 10 segundos para conectar
            connection.setReadTimeout(10000);     // 10 segundos para leer
        } catch (ProtocolException e) {
            throw new RuntimeException("Error configurando la conexión HTTP", e);
        }
    }

    /**
     * Reads the response stream into a string.
     */
    private String readResponse(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    /**
     * Parses the JSON response into Report objects.
     */
    private List<Report> parseReports(String jsonResponse) {
        List<Report> reports = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            Report report = new Report();
            
            // Set basic report data
            report.setDate(LocalDate.parse(dataObj.getString("date")));
            report.setConfirmed(dataObj.getInt("confirmed"));
            report.setDeaths(dataObj.getInt("deaths"));
            report.setRecovered(dataObj.getInt("recovered"));
            report.setConfirmedDiff(dataObj.getInt("confirmed_diff"));
            report.setDeathsDiff(dataObj.getInt("deaths_diff"));
            report.setRecoveredDiff(dataObj.getInt("recovered_diff"));
            report.setLastUpdate(dataObj.getString("last_update").isEmpty() ? 
                LocalDateTime.now() : LocalDateTime.parse(dataObj.getString("last_update").replace(" ", "T")));
            report.setActive(dataObj.getInt("active"));
            report.setActiveDiff(dataObj.getInt("active_diff"));
            report.setFatalityRate(dataObj.getDouble("fatality_rate"));
            
            // Process region data
            JSONObject regionObj = dataObj.getJSONObject("region");
            Region region = new Region();
            
            region.setIso(regionObj.getString("iso"));
            region.setName(regionObj.getString("name"));
            region.setProvince(regionObj.getString("province"));
            
            // Process cities if available
            if (regionObj.has("cities")) {
                JSONArray citiesArray = regionObj.getJSONArray("cities");
                for (int j = 0; j < citiesArray.length(); j++) {
                    JSONObject cityObj = citiesArray.getJSONObject(j);
                    Region.City city = new Region.City();
                    
                    // Manejar campos nulos o inexistentes
                    city.setName(cityObj.optString("name", "Unknown"));
                    
                    // Manejar fips
                    if (cityObj.has("fips") && !cityObj.isNull("fips")) {
                        city.setFips(cityObj.getInt("fips"));
                    } else {
                        city.setFips(0);
                    }
                    
                    // Manejar coordenadas
                    if (cityObj.has("lat") && !cityObj.isNull("lat")) {
                        city.setLatitude(cityObj.getDouble("lat"));
                    } else {
                        city.setLatitude(0.0);
                    }
                    
                    if (cityObj.has("long") && !cityObj.isNull("long")) {
                        city.setLongitude(cityObj.getDouble("long"));
                    } else {
                        city.setLongitude(0.0);
                    }
                    
                    // Manejar estadísticas
                    city.setConfirmed(cityObj.optInt("confirmed", 0));
                    city.setDeaths(cityObj.optInt("deaths", 0));
                    city.setConfirmedDiff(cityObj.optInt("confirmed_diff", 0));
                    city.setDeathsDiff(cityObj.optInt("deaths_diff", 0));
                    
                    // Manejar última actualización
                    if (cityObj.has("last_update")) {
                        String lastUpdateStr = cityObj.getString("last_update");
                        city.setLastUpdate(lastUpdateStr.isEmpty() ? 
                            LocalDateTime.now() : LocalDateTime.parse(lastUpdateStr.replace(" ", "T")));
                    }
                    
                    region.addCity(city);
                }
            }
            
            report.setRegion(region);
            reports.add(report);
        }
        
        return reports;
    }

    /**
     * Retrieves and processes reports for a specific country and date.
     * Returns data ordered and deduplicated by province using TreeMap.
     * 
     * @param iso Country ISO code
     * @param date Date to query
     * @return TreeMap with provinces as keys and reports as values
     */
    public TreeMap<String, Report> getReportsByCountryAndDate(String iso, LocalDate date) {
        TreeMap<String, Report> reports = reportRepo.findByCountryAndDate(iso, date);
        
        // Log results
        LOGGER.info("Found " + reports.size() + " unique provinces for " + iso + " on " + date);
        System.out.println("Datos recuperados para " + iso + " en fecha " + date + ":");
        
        // Print results to console
        reports.forEach((province, report) -> 
            System.out.printf("Provincia: %s | Confirmados: %d | Muertes: %d%n", 
                province, report.getConfirmed(), report.getDeaths()));
        
        return reports;
    }

    /**
     * Checks if a report has been processed using the execution log repository.
     */
    private boolean isProcessed(String iso, LocalDate date) {
        return executionLogRepo.exists(iso, date);
    }

    /**
     * Logs a successful execution in the database.
     */
    private void logExecution(String iso, LocalDate date) {
      
          executionLogRepo.save(new org.covidreport.model.ExecutionLog(date,iso));
    }
}