package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves legacy URLs that cannot be handled by any general strategy.
 *
 * <p>Each entry maps the normalised path component of a legacy URL directly to an absolute
 * JCR document path.  The key is the path exactly as passed to {@link #locate} (host/query/
 * fragment/trailing-slash already stripped).
 */
public class SpecialCasePathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialCasePathLocator.class);

    private static final String INDEX = "index";

    /**
     * Hard-coded path → JCR path overrides.  Value may be a {@code hippo:handle} node or a
     * folder whose {@code index} child is the handle.
     */
    static final Map<String, String> REWRITES = new HashMap<>();
    static {
        // Empty path = site root (https://www.gov.scot/) — resolves to the home page
        REWRITES.put("",
                "/content/documents/govscot/home");
        REWRITES.put("/search",
                "/content/documents/govscot/search/index");
        REWRITES.put("/Topics/Statistics/About",
                "/content/documents/govscot/about/how-government-is-run/statistics-and-research");
        REWRITES.put("/publications/scottish-marine-freshwater-science-volume-4-number-5-modelling-noise//downloads#res43396",
                "/content/documents/govscot/publications/progress-report/2013/09/scottish-marine-freshwater-science-volume-4-number-5-modelling-noise/documents");
    }

    @Override
    public String name() {
        return "special";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        String jcrPath = REWRITES.get(path);
        if (jcrPath == null) {
            return Optional.empty();
        }
        if (!session.nodeExists(jcrPath)) {
            LOG.warn("SpecialCasePathLocator: rewrite for '{}' points to missing node {}", path, jcrPath);
            return Optional.empty();
        }
        Node node = session.getNode(jcrPath);
        if (SlugLookup.isHandle(node)) {
            return Optional.of(node);
        }
        if (node.isNodeType("hippostd:folder") && node.hasNode(INDEX)) {
            return Optional.of(node.getNode(INDEX));
        }
        LOG.warn("SpecialCasePathLocator: target {} for '{}' is neither a handle nor an indexable folder",
                jcrPath, path);
        return Optional.empty();
    }
}
