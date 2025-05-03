package org.covidreport.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents geographical region information in a COVID-19 report.
 * Contains country-level information and nested city/province details.
 */
public class Region {
    /**
     * ISO country code (e.g., "USA")
     */
    private String iso;
    
    /**
     * Region name (e.g., "US")
     */
    private String name;
    
    /**
     * Province name (e.g., "Alabama")
     */
    private String province;
    
    /**
     * Latitude coordinate
     */
    private double latitude;
    
    /**
     * Longitude coordinate
     */
    private double longitude;
    
    /**
     * List of cities within the region
     */
    private List<City> cities = new ArrayList<>();

    /**
     * Default constructor for framework compatibility
     */
    public Region() {
    }

    /**
     * Creates a region with basic geographical information
     * 
     * @param iso        ISO country code
     * @param name       Region name
     * @param province   Province name
     * @param latitude   Latitude coordinate
     * @param longitude  Longitude coordinate
     */
    public Region(String iso, String name, String province, double latitude, double longitude) {
        this.iso = iso;
        this.name = name;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    /**
     * Adds a city to the region
     * @param city City object to add
     */
    public void addCity(City city) {
        this.cities.add(city);
    }

    /**
     * Returns a string representation of the region
     * @return String containing region details
     */
    @Override
    public String toString() {
        return "Region{" +
                "iso='" + iso + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", cities=" + cities +
                '}';
    }

    /**
     * Checks if two Region objects are equal
     * @param o Object to compare
     * @return boolean indicating equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;
        Region region = (Region) o;
        return Double.compare(region.latitude, latitude) == 0 &&
               Double.compare(region.longitude, longitude) == 0 &&
               Objects.equals(iso, region.iso) &&
               Objects.equals(name, region.name) &&
               Objects.equals(province, region.province) &&
               Objects.equals(cities, region.cities);
    }

    /**
     * Generates a hash code for the region
     * @return int hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(iso, name, province, latitude, longitude, cities);
    }

    /**
     * Represents a city within a region with detailed statistics
     */
    public static class City {
        /**
         * City name
         */
        private String name;
        
        /**
         * Federal Information Processing Standard code
         */
        private int fips;
        
        /**
         * Latitude coordinate
         */
        private double latitude;
        
        /**
         * Longitude coordinate
         */
        private double longitude;
        
        /**
         * Confirmed cases count
         */
        private int confirmed;
        
        /**
         * Death cases count
         */
        private int deaths;
        
        /**
         * Daily change in confirmed cases
         */
        private int confirmedDiff;
        
        /**
         * Daily change in death cases
         */
        private int deathsDiff;
        
        /**
         * Timestamp of last update
         */
        private LocalDateTime lastUpdate;

        /**
         * Default constructor for framework compatibility
         */
        public City() {
        }

        /**
         * Creates a city with all fields
         * 
         * @param name          City name
         * @param fips          FIPS code
         * @param latitude      Latitude coordinate
         * @param longitude     Longitude coordinate
         * @param confirmed     Confirmed cases count
         * @param deaths        Death cases count
         * @param confirmedDiff Daily change in confirmed cases
         * @param deathsDiff    Daily change in death cases
         * @param lastUpdate    Timestamp of last update
         */
        public City(String name, int fips, double latitude, double longitude, 
                   int confirmed, int deaths, int confirmedDiff, 
                   int deathsDiff, LocalDateTime lastUpdate) {
            this.name = name;
            this.fips = fips;
            this.latitude = latitude;
            this.longitude = longitude;
            this.confirmed = confirmed;
            this.deaths = deaths;
            this.confirmedDiff = confirmedDiff;
            this.deathsDiff = deathsDiff;
            this.lastUpdate = lastUpdate;
        }

        // Getters and Setters for City class

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFips() {
            return fips;
        }

        public void setFips(int fips) {
            this.fips = fips;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getConfirmed() {
            return confirmed;
        }

        public void setConfirmed(int confirmed) {
            this.confirmed = confirmed;
        }

        public int getDeaths() {
            return deaths;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }

        public int getConfirmedDiff() {
            return confirmedDiff;
        }

        public void setConfirmedDiff(int confirmedDiff) {
            this.confirmedDiff = confirmedDiff;
        }

        public int getDeathsDiff() {
            return deathsDiff;
        }

        public void setDeathsDiff(int deathsDiff) {
            this.deathsDiff = deathsDiff;
        }

        public LocalDateTime getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        /**
         * Returns a string representation of the city
         * @return String containing city details
         */
        @Override
        public String toString() {
            return "City{" +
                    "name='" + name + '\'' +
                    ", fips=" + fips +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", confirmed=" + confirmed +
                    ", deaths=" + deaths +
                    ", confirmedDiff=" + confirmedDiff +
                    ", deathsDiff=" + deathsDiff +
                    ", lastUpdate=" + lastUpdate +
                    '}';
        }

        /**
         * Checks if two City objects are equal
         * @param o Object to compare
         * @return boolean indicating equality
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof City)) return false;
            City city = (City) o;
            return fips == city.fips &&
                   Double.compare(city.latitude, latitude) == 0 &&
                   Double.compare(city.longitude, longitude) == 0 &&
                   confirmed == city.confirmed &&
                   deaths == city.deaths &&
                   confirmedDiff == city.confirmedDiff &&
                   deathsDiff == city.deathsDiff &&
                   Objects.equals(name, city.name) &&
                   Objects.equals(lastUpdate, city.lastUpdate);
        }

        /**
         * Generates a hash code for the city
         * @return int hash code value
         */
        @Override
        public int hashCode() {
            return Objects.hash(name, fips, latitude, longitude, confirmed, deaths, 
                               confirmedDiff, deathsDiff, lastUpdate);
        }
    }
}