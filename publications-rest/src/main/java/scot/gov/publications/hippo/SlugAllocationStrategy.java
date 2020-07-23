package scot.gov.publications.hippo;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Decide what slug we should use for a publication based on its title.
 *
 * The strategy used to avoid clashes is simply to add a number to the end of the slug starting with 2.
 */
public class SlugAllocationStrategy {

    Session session;

    HippoPaths paths;

    HippoUtils hippoUtils;

    public SlugAllocationStrategy(Session session) {
        this.session = session;
        this.paths = new HippoPaths(session);
        this.hippoUtils = new HippoUtils();
    }

    /**
     * Allocate a govscot:slug for a publication with this title.
     *
     * @param title The title of the publication
     * @return A unique slug to use for this publication
     */
    public String allocate(String title) throws RepositoryException {
        // turn the title into a slug
        String slug = paths.slugify(title);

        // if it does not already exist then just use this slug
        if (!slugAlreadyExists(slug)) {
            return slug;
        }

        // the slug is already used, try appending a number starting from 2
        // i.e. rather than try my-document-1 we will start from my-document-2
        return disambiguate(slug, 2);
    }

    private String disambiguate(String slug, int postfix) throws RepositoryException {
        String candidate = String.format("%s-%d", slug, postfix);
        if (!slugAlreadyExists(candidate)) {
            // base case: we have found a unique slug
            return candidate;
        }

        // recursive call to try the next number
        return disambiguate(slug, postfix + 1);
    }

    private boolean slugAlreadyExists(String slug) throws RepositoryException {
        String sql = "SELECT * FROM govscot:SimpleContent WHERE govscot:slug = '%s'";
        Node node = hippoUtils.findFirst(session, sql, slug);
        return node != null;
    }
}
