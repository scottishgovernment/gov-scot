package scot.gov.www.importer.health;

import scot.gov.www.importer.Importer;

import java.time.ZonedDateTime;

public class ImporterStatus {

    private Importer importer;

    private ZonedDateTime lastrun;

    private ZonedDateTime lastSuccessfulRun;

    private boolean success = true;

    private String message;

    public Importer getImporter() {
        return importer;
    }

    public void setImporter(Importer importer) {
        this.importer = importer;
    }

    public ZonedDateTime getLastrun() {
        return lastrun;
    }

    public void setLastrun(ZonedDateTime lastrun) {
        this.lastrun = lastrun;
    }

    public ZonedDateTime getLastSuccessfulRun() {
        return lastSuccessfulRun;
    }

    public void setLastSuccessfulRun(ZonedDateTime lastSuccessfulRun) {
        this.lastSuccessfulRun = lastSuccessfulRun;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}