package scot.gov.www;

import org.apache.commons.lang3.StringUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Created by z441571 on 22/04/2020.
 */
public abstract class SlugDaemonModule extends DaemonModuleBase {

    protected static final String GOVSCOT_SLUG_PROPERTY = "govscot:slug";

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
    private boolean slugAlreadyExists(String slug, String docType) throws RepositoryException {
        String sql = String.format("SELECT * FROM %s WHERE govscot:slug = '%s'", docType, slug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL).execute();
        return result.getNodes().hasNext();
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
