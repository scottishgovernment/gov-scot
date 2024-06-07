package scot.gov.pressreleases;

import scot.gov.pressreleases.domain.ImporterStatus;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ImporterStatusUpdater {

    private static final String DATE_TIME_PROPERTY = "datetime";

    static Clock clock = Clock.system(ZoneId.of("Europe/London"));

    static Set<String> IMPORTERS = new HashSet<>(Arrays.asList("press-release-importer", "speech-briefing-importer", "tag-importer", "correspondence-importer"));

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

    ImporterStatus setStatus(ImporterStatus status, Session session) throws RepositoryException {
        if (!IMPORTERS.contains(status.getImporter())) {
            return null;
        }
        Node importerNode = importerNode(status.getImporter(), session);
        importerNode.setProperty(DATE_TIME_PROPERTY, GregorianCalendar.from(status.getLastrun()));
        session.save();
        return status;
    }

    ZonedDateTime toZDT(Calendar calendar) {
        Instant instant = calendar.toInstant();
        ZoneId zoneId = TimeZone.getDefault().toZoneId();
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    Calendar defaultLastRuntime() {
        return GregorianCalendar.from(ZonedDateTime.now(clock).minusDays(7).plusMinutes(1).truncatedTo(ChronoUnit.SECONDS));
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
