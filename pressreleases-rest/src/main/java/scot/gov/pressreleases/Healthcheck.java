package scot.gov.pressreleases;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Healthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(Healthcheck.class);

    private static final String DATE_TIME_PROPERTY = "datetime";

    static Clock clock = Clock.system(ZoneId.of("Europe/London"));

    static Set<String> IMPORTERS = new HashSet<>(Arrays.asList("press-release-importer", "speech-briefing-importer", "tag-importer", "correspondence-importer"));

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
            health.setOk(false);
            health.getPerformanceData().add("RepositoryException collecting health data: " + e.getMessage());
            LOG.error("Failed to get health data for press release importer", e);
        }

        return health;
    }

    void collectHealthInformation(Health health) throws RepositoryException {
        health.setOk(true);
        for (Importer importer : Importer.values()) {
            ImporterStatus status = statusUpdater.getStatus(importer.name, session);
            ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(importer.minutesThreshold);
            boolean importerOk = status.getLastSuccessfulRun().isAfter(cutoff);
            health.getPerformanceData().add(message(status, importerOk, cutoff));
            if (!importerOk) {
                health.setOk(false);
            }
        }
    }

    String message(ImporterStatus status, boolean ok, ZonedDateTime cutoff) {
        if (ok) {
            return String.format("%s ok", status.getImporter());
        }
        return StringUtils.isBlank(status.getMessage())
                ? String.format("%s out of date by %s", status.getImporter(), duration(cutoff))
                : String.format("%s out of date by %s, message: %s", status.getImporter(), duration(cutoff), status.getMessage());
    }

    String duration(ZonedDateTime cutoff) {
        ZonedDateTime now = ZonedDateTime.now();

        // Calculate the duration in milliseconds
        Duration duration = Duration.between(cutoff, now);
        long millis = duration.toMillis();
        return DurationFormatUtils.formatDurationWords(millis, true, true);
    }

    public ImporterStatus getStatus(String importer, Session session) throws RepositoryException {
        if (!IMPORTERS.contains(importer)) {
            return null;
        }

        Node importerNode = importerNode(importer, session);
        Calendar calendar = importerNode.hasProperty(DATE_TIME_PROPERTY)
                ? importerNode.getProperty(DATE_TIME_PROPERTY).getDate()
                : defaultLastRuntime();
        ImporterStatus importerStatus = new ImporterStatus();
        importerStatus.setImporter(importer);
        importerStatus.setLastrun(toZDT(calendar));
        session.save();
        return importerStatus;
    }

    Calendar defaultLastRuntime() {
        return GregorianCalendar.from(ZonedDateTime.now(clock).minusDays(7).plusMinutes(1).truncatedTo(ChronoUnit.SECONDS));
    }

    ZonedDateTime toZDT(Calendar calendar) {
        Instant instant = calendar.toInstant();
        ZoneId zoneId = TimeZone.getDefault().toZoneId();
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    Node importerNode(String importer, Session session) throws RepositoryException {
        Node content = session.getNode("/content");
        Node importers = getOrCreate(content, "importers");
        return getOrCreate(importers, importer);
    }

    Node getOrCreate(Node parent, String name) throws RepositoryException {
        if (parent.hasNode(name)) {
            return parent.getNode(name);
        }
        return parent.addNode(name, "nt:unstructured");
    }
}
