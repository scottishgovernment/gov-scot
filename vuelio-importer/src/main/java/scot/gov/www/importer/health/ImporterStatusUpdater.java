package scot.gov.www.importer.health;

import org.hippoecm.repository.util.JcrUtils;

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

    private static final String SUCCESS_DATE_TIME_PROPERTY = "successDatetime";

    private static final String SUCCESS_PROPERTY = "success";

    private static final String MESSAGE_PROPERTY = "message";

    static Clock clock = Clock.system(ZoneId.of("Europe/London"));

    static Set<String> IMPORTERS = new HashSet<>(Arrays.asList("vuelio-importer"));

    public ImporterStatus getStatus(String importer, Session session) throws RepositoryException {
        if (!IMPORTERS.contains(importer)) {
            return null;
        }

        Node importerNode = importerNode(importer, session);
        Calendar lastRun = getDateWithDefault(importerNode, DATE_TIME_PROPERTY);
        Calendar lastSuccessfulRun = getDateWithDefault(importerNode, SUCCESS_DATE_TIME_PROPERTY);

        if (!importerNode.hasProperty(SUCCESS_DATE_TIME_PROPERTY)) {
            // if the importer has never successfully run then return a date in the past
            lastSuccessfulRun = defaultLastRuntime();
        }

        ImporterStatus importerStatus = new ImporterStatus();
        importerStatus.setImporter(importer);
        importerStatus.setLastrun(toZDT(lastRun));
        importerStatus.setLastSuccessfulRun(toZDT(lastSuccessfulRun));
        importerStatus.setSuccess(JcrUtils.getBooleanProperty(importerNode, SUCCESS_PROPERTY, true));
        importerStatus.setMessage(JcrUtils.getStringProperty(importerNode, MESSAGE_PROPERTY, ""));
        session.save();

        return importerStatus;
    }

    Calendar getDateWithDefault(Node node, String property) throws RepositoryException {
        return node.hasProperty(property) ? node.getProperty(property).getDate() : defaultLastRuntime();
    }

    public ImporterStatus saveStatus(ImporterStatus status, Session session) throws RepositoryException {
        if (!IMPORTERS.contains(status.getImporter())) {
            return null;
        }
        Node importerNode = importerNode(status.getImporter(), session);
        importerNode.setProperty(DATE_TIME_PROPERTY, GregorianCalendar.from(status.getLastrun()));
        importerNode.setProperty(SUCCESS_DATE_TIME_PROPERTY, GregorianCalendar.from(status.getLastSuccessfulRun()));
        importerNode.setProperty(SUCCESS_PROPERTY, status.isSuccess());
        importerNode.setProperty(MESSAGE_PROPERTY, status.getMessage());
        session.save();
        return status;
    }

    ZonedDateTime toZDT(Calendar calendar) {
        Instant instant = calendar.toInstant();
        ZoneId zoneId = calendar.getTimeZone().toZoneId();
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    Calendar defaultLastRuntime() {
        return GregorianCalendar.from(ZonedDateTime.now(clock).minusDays(7).plusHours(1).truncatedTo(ChronoUnit.SECONDS));
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
