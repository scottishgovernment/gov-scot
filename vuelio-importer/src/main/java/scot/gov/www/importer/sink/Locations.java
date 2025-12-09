package scot.gov.www.importer.sink;

import com.github.slugify.Slugify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static scot.gov.www.importer.sink.ContentNodes.GOVSCOT_NEWS;
import static scot.gov.www.importer.sink.ContentNodes.GOVSCOT_PUBLICATION;

public class Locations {

    private static final Logger LOG = LoggerFactory.getLogger(Locations.class);

    private Slugify slugify = new Slugify();

    public Locations() {
        slugify.withCustomReplacement("+", "");
        slugify.withCustomReplacement("'", "");
        slugify.withCustomReplacement("&", "");
        slugify.withCustomReplacement("-", "-");
    }

    public String newsLocation(PressRelease release, Session session) throws RepositoryException {
        // if a press release exists with this external id then use this as the path - this will ensure the item is reused instead
        // of a new one being created in the case where the title has changes.
        Node existing = getByExternalId(session, GOVSCOT_NEWS, release);
        if (existing != null) {
            return existing.getParent().getPath();
        }

        String slug = slugify.slugify(release.getTitle());
        String yearString = Integer.toString(release.getDateTime().getYear());
        String monthString = String.format("%02d", release.getDateTime().getMonthValue());
        String location = String.format("/content/documents/govscot/news/%s/%s/%s", yearString, monthString, slug);

        if (!locationAlreadyExists(session, location)) {
            // if a press release has a unique title for the month then return
            LOG.info("----> News location {}", location);
            return location;
        }

        return disambiguate(session, location, 2);
    }

    public String publicationLocation(PressRelease release, String publicationType, Session session) throws RepositoryException {
        // if a press release exists with this external id then ise this as the path - this will ensure the item is reused instead
        // of a new one being created in the case where the title has changes.
        Node existing = getByExternalId(session, GOVSCOT_PUBLICATION, release);
        if (existing != null) {
            return existing.getParent().getPath();
        }

        String slug = slugify.slugify(release.getTitle());
        String yearString = Integer.toString(release.getDateTime().getYear());
        String monthString = String.format("%02d", release.getDateTime().getMonthValue());
        String location = String.format("/content/documents/govscot/publications/%s/%s/%s/%s", publicationType, yearString, monthString, slug);

        if (!locationAlreadyExists(session, location)) {
            LOG.info("----> Publication location {}", location);
            return location + "/index";
        }

        return disambiguate(session, location, 2) + "/index";

    }

    public static Node getByExternalId(Session session, String type, PressRelease pressRelease) throws RepositoryException {
        String xpath = String.format("//element(*, %s)[govscot:externalId = '%s']", type, pressRelease.getId());
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        QueryResult result = query.execute();
        if (result.getNodes().hasNext()) {
            return result.getNodes().nextNode();
        } else {
            return null;
        }
    }

    private String disambiguate(Session session, String location, int postfix) throws RepositoryException {
        String candidate = String.format("%s-%d", location, postfix);

        if (!locationAlreadyExists(session, candidate)) {
            // Base case: we have found a unique location.
            LOG.info("----> Disambiguated location {}", candidate);
            return candidate;
        }

        // Recursive call to try the next number.
        return disambiguate(session, location,postfix + 1);
    }

    private boolean locationAlreadyExists(Session session, String location) throws RepositoryException {
        return session.nodeExists(location);
    }
}
