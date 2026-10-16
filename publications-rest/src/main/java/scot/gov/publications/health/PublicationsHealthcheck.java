package scot.gov.publications.health;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.repo.ListResult;
import scot.gov.publications.repo.Publication;
import scot.gov.publications.repo.PublicationListQuery;
import scot.gov.publications.repo.PublicationRepository;
import scot.gov.publications.repo.PublicationRepositoryException;
import scot.gov.publications.repo.State;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PublicationsHealthcheck {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsHealthcheck.class);

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;

    static final int MINUTES_THRESHOLD = 5;

    // comfortably more than the single-thread executor could ever leave pending at once
    static final int MAX_PENDING_TO_CHECK = 500;

    PublicationRepository repository;

    public PublicationsHealthcheck(PublicationRepository repository) {
        this.repository = repository;
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Health healthcheck() {
        Health health = new Health();
        try {
            collectHealthInformation(health);
        } catch (PublicationRepositoryException e) {
            health.setStatus(NagiosStatus.CRITICAL);
            health.setMessage("Failed to read publication job status: " + e.getMessage());
            LOG.error("Failed to get health data for publications importer", e);
        }
        return health;
    }

    void collectHealthInformation(Health health) throws PublicationRepositoryException {
        ListResult result = repository.list(PublicationListQuery.of(
                1, MAX_PENDING_TO_CHECK, null, null, null, null, State.PENDING.name(), "createddate", "asc"));

        ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(MINUTES_THRESHOLD);
        List<String> stuck = new ArrayList<>();
        for (Publication publication : result.getPublications()) {
            if (isStuck(publication, cutoff)) {
                stuck.add(describe(publication));
            }
        }

        if (stuck.isEmpty()) {
            health.setStatus(NagiosStatus.OK);
            health.setMessage("OK");
        } else {
            health.setStatus(NagiosStatus.CRITICAL);
            health.setMessage(stuck.size() + " publication job(s) pending for more than " + MINUTES_THRESHOLD + " minutes");
            health.setInfo(stuck);
        }
    }

    boolean isStuck(Publication publication, ZonedDateTime cutoff) {
        Timestamp createddate = publication.getCreateddate();
        if (createddate == null) {
            return false;
        }
        ZonedDateTime created = ZonedDateTime.ofInstant(createddate.toInstant(), ZoneId.systemDefault());
        return created.isBefore(cutoff);
    }

    String describe(Publication publication) {
        ZonedDateTime created = ZonedDateTime.ofInstant(publication.getCreateddate().toInstant(), ZoneId.systemDefault());
        return String.format("%s: isbn=%s filename=%s pending since %s",
                publication.getId(), publication.getIsbn(), publication.getFilename(), created.format(DATE_FORMAT));
    }
}
