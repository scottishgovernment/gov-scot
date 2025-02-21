package scot.gov.pressreleases;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Healthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(Healthcheck.class);

    static final DateTimeFormatter DATE_FORMAT  = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    Session session;

    ImporterStatusUpdater statusUpdater = new ImporterStatusUpdater();

    public Healthcheck(Session session) {
        this.session = session;
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Health healthcheck() {
        Health health = new Health();
        try {
            collectHealthInformation(health);
        } catch (RepositoryException e) {
            health.setMessage("critical");
            health.setPerformanceData("RepositoryException collecting health data: " + e.getMessage());
            LOG.error("Failed to get health data for press release importer", e);
        }

        return health;
    }

    void collectHealthInformation(Health health) throws RepositoryException {
        health.setStatus("ok");
        health.setMessage("ok");

        List<String> notOkImporters = new ArrayList<>();
        List<String> performanceData = new ArrayList<>();
        for (Importer importer : Importer.values()) {
            ImporterStatus status = statusUpdater.getStatus(importer.name, session);
            ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(importer.minutesThreshold);
            boolean importerOk = status.getLastSuccessfulRun().isAfter(cutoff);
            String importerMessage =  messageForImporter(status, importerOk);
            performanceData.add(importerMessage);
            if (!importerOk) {
                health.setStatus("warning");
                notOkImporters.add(importer.name);
            }
        }

        if (!notOkImporters.isEmpty()) {
            health.setMessage("importers out of date: " + String.join(",", notOkImporters));
        }
        health.setPerformanceData(String.join(", ", performanceData));
    }

    String messageForImporter(ImporterStatus status, boolean ok) {
        String lastRun = status.getLastSuccessfulRun().format(DATE_FORMAT);
        return StringUtils.isBlank(status.getMessage())
                ? String.format("%s=%s", status.getImporter(), lastRun)
                : String.format("%s=%s, message: %s", status.getImporter(), lastRun, status.getMessage());
    }

}
