package scot.gov.www.pressreleases.sink;

import com.github.slugify.Slugify;
import scot.gov.www.pressreleases.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static scot.gov.www.pressreleases.sink.ContentNodes.GOVSCOT_NEWS;
import static scot.gov.www.pressreleases.sink.ContentNodes.GOVSCOT_PUBLICATION;

public class Locations {

    private Slugify slugify = new Slugify();

    public Locations() {
        slugify.withCustomReplacement("+", "");
        slugify.withCustomReplacement("'", "");
        slugify.withCustomReplacement("&", "");
        slugify.withCustomReplacement("-", "-");
    }

    public String newsLocation(PressRelease release, Session session) throws RepositoryException {
        // if a press release exists with this external id then ise this as the path - this will ensure the item is reused instead
        // of a new one being created in the case where the title has changes.
        Node existing = getByExternalId(session, GOVSCOT_NEWS, release);
        if (existing != null) {
            return existing.getParent().getPath();
        }

        String slug = slugify.slugify(release.getTitle());
        String yearString = Integer.toString(release.getDateTime().getYear());
        String monthString = String.format("%02d", release.getDateTime().getMonthValue());
        return String.format("/content/documents/govscot/news/%s/%s/%s", yearString, monthString, slug);
    }

    public String publicationLocation(PressRelease release, String publicationType, Session session) throws RepositoryException {
        // if a press release exists with this external id then ise this as the path - this will ensure the item is reusled instead
        // of a new one being created in the case where the title has changes.
        Node existing = getByExternalId(session, GOVSCOT_PUBLICATION, release);
        if (existing != null) {
            return existing.getParent().getPath();
        }

        String slug = slugify.slugify(release.getTitle());
        String yearString = Integer.toString(release.getDateTime().getYear());
        String monthString = String.format("%02d", release.getDateTime().getMonthValue());
        return String.format("/content/documents/govscot/publications/%s/%s/%s/%s/index", publicationType, yearString, monthString, slug);
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
}
