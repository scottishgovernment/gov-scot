package scot.gov.www;

import org.apache.commons.lang3.StringUtils;
import scot.gov.publishing.sluglookup.SlugLookupPaths;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

/**
 * Created by z441571 on 22/04/2020.
 */
public abstract class SlugDaemonModule extends DaemonModuleBase {

    protected static final String GOVSCOT_SLUG_PROPERTY = "govscot:slug";

    protected static final String PREVIEW = "preview";

    protected static final String LIVE = "live";

    protected String allocate(String slug, String docType) throws RepositoryException {

        slug = removeDuplicateHyphens(slug);

        // If it does not already exist then just use this slug.
        if (!slugAlreadyExists(slug, docType)) {
            return slug;
        }

        // The slug is already used, try appending a number starting from 2
        // i.e. rather than try my-document-1 we will start from my-document-2.
        return disambiguate(slug, docType, 2);
    }

    /**
     * Recursively try adding a number to the end of the slug until we get a unique one.
     */
    private String disambiguate(String slug, String docType, int postfix) throws RepositoryException {
        String candidate = String.format("%s-%d", slug, postfix);

        if (!slugAlreadyExists(candidate, docType)) {
            // Base case: we have found a unique slug.
            return candidate;
        }

        // Recursive call to try the next number.
        return disambiguate(slug, docType, postfix + 1);
    }
    private boolean slugAlreadyExists(String slug, String type) throws RepositoryException {
        String slugPath = SlugLookupPaths.slugLookupPath(slug, "govscot", type, LIVE);
        return session.nodeExists(slugPath);
    }

    protected static Node getLatestVariant(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        Node variant = null;
        while (it.hasNext()) {
            variant = it.nextNode();
        }
        return variant;
    }

    private String removeDuplicateHyphens(String str) {
        return Arrays.stream(str.split("-"))
                .filter(StringUtils::isNotEmpty)
                .collect(joining("-"));
    }
}
