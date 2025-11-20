package scot.gov.www.importer.health;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Healthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(Healthcheck.class);

    static final DateTimeFormatter DATE_FORMAT  = DateTimeFormatter.RFC_1123_DATE_TIME;

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
            health.setStatus(NagiosStatus.CRITICAL);
            health.setPerformanceData("Failed to read importer health: " + e.getMessage());
            LOG.error("Failed to get health data for press release importer", e);
        }
        return health;
    }

    void collectHealthInformation(Health health) throws RepositoryException {
        health.setStatus(NagiosStatus.OK);
        health.setMessage("OK");

        List<String> notOkImporters = new ArrayList<>();
        List<String> info = new ArrayList<>();
        for (Importer importer : Importer.values()) {
            ImporterStatus status = statusUpdater.getStatus(importer.node, session);
            ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(importer.minutesThreshold);
            boolean importerOk = status.getLastSuccessfulRun().isAfter(cutoff);
            String importerMessage =  messageForImporter(status);
            info.add(importerMessage);
            if (!importerOk) {
                health.setStatus(NagiosStatus.WARNING);
                notOkImporters.add(importer.name);
            }
        }

        if (!notOkImporters.isEmpty()) {
            health.setMessage("Importers out of date: " + String.join(",", notOkImporters));
        }
        health.setInfo(info);
    }

    String messageForImporter(ImporterStatus status) {
        String importerId = status.getImporter();
        Importer importer = Importer.forId(importerId);
        String name = importer.name;
        String lastRun = status.getLastSuccessfulRun().format(DATE_FORMAT);
        return StringUtils.isBlank(status.getMessage())
                ? String.format("%s: %s", name, lastRun)
                : String.format("%s: %s (%s)", name, lastRun, status.getMessage());
    }

}
