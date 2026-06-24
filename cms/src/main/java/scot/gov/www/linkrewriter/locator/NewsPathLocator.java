package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Resolves {@code /news/<slug>} paths via the pre-built slug-lookup table.
 */
public class NewsPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(NewsPathLocator.class);

    static final String NEWS_PREFIX = "/news/";

    @Override
    public String name() {
        return "news";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        if (!path.startsWith(NEWS_PREFIX)) {
            return Optional.empty();
        }
        String slug = path.substring(NEWS_PREFIX.length());
        if (slug.contains("/")) {
            LOG.warn("NewsPathLocator: path '{}' has additional segments after the slug, skipping", path);
            return Optional.empty();
        }
        return SlugLookup.findBySlug(session, "news", slug);
    }
}
