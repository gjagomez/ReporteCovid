package org.covidreport.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a COVID-19 report for a specific country and date.
 * Contains statistics like confirmed cases, deaths, and regional breakdowns.
 */
public class Report {
    private LocalDate date;
    private int confirmed;
    private int deaths;
    private int recovered;
    private int confirmedDiff;
    private int deathsDiff;
    private int recoveredDiff;
    private LocalDateTime lastUpdate;
    private int active;
    private int activeDiff;
    private double fatalityRate;
    private Region region;

    /**
     * Default constructor
     */
    public Report() {}

    /**
     * Full constructor for all fields
     */
    public Report(LocalDate date, int confirmed, int deaths, int recovered,
                  int confirmedDiff, int deathsDiff, int recoveredDiff,
                  LocalDateTime lastUpdate, int active, int activeDiff,
                  double fatalityRate, Region region) {
        this.date = date;
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.confirmedDiff = confirmedDiff;
        this.deathsDiff = deathsDiff;
        this.recoveredDiff = recoveredDiff;
        this.lastUpdate = lastUpdate;
        this.active = active;
        this.activeDiff = activeDiff;
        this.fatalityRate = fatalityRate;
        this.region = region;
    }

    // Getters and Setters

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
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

    public int getRecoveredDiff() {
        return recoveredDiff;
    }

    public void setRecoveredDiff(int recoveredDiff) {
        this.recoveredDiff = recoveredDiff;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getActiveDiff() {
        return activeDiff;
    }

    public void setActiveDiff(int activeDiff) {
        this.activeDiff = activeDiff;
    }

    public double getFatalityRate() {
        return fatalityRate;
    }

    public void setFatalityRate(double fatalityRate) {
        this.fatalityRate = fatalityRate;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Report{" +
                "date=" + date +
                ", confirmed=" + confirmed +
                ", deaths=" + deaths +
                ", recovered=" + recovered +
                ", confirmedDiff=" + confirmedDiff +
                ", deathsDiff=" + deathsDiff +
                ", recoveredDiff=" + recoveredDiff +
                ", lastUpdate=" + lastUpdate +
                ", active=" + active +
                ", activeDiff=" + activeDiff +
                ", fatalityRate=" + fatalityRate +
                ", region=" + region +
                '}';
    }
}