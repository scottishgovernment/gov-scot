package scot.gov.pressreleases.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class ImporterStatus {

    private String importer;

    @JsonProperty("lastrun")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime lastrun;

    public String getImporter() {
        return importer;
    }

    public void setImporter(String importer) {
        this.importer = importer;
    }

    public ZonedDateTime getLastrun() {
        return lastrun;
    }

    public void setLastrun(ZonedDateTime lastrun) {
        this.lastrun = lastrun;
    }
}