package scot.gov.www.linkrewriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Optional;

/**
 * Encapsulates one strategy for locating a JCR {@code hippo:handle} node for a given
 * normalised URL path.
 *
 * <p>Implementations receive a path that has already had its host prefix, query string,
 * fragment, and trailing slash stripped.  They return {@link Optional#empty()} when the
 * path does not fall within their domain, or when no matching node can be found.
 *
 * <p>The {@link #name()} is used in log messages and statistics.
 */
public interface PathLocatorStrategy {
    /** Short identifier used in log messages and stats (e.g. {@code "news"}, {@code "path"}). */
    String name();

    /**
     * Attempts to locate the JCR handle for {@code path}.
     *
     * @param session the current JCR session
     * @param path    normalised URL path (host/query/fragment/trailing-slash stripped)
     * @return the resolved {@code hippo:handle} node, or {@link Optional#empty()} if not found
     */
    Optional<Node> locate(Session session, String path) throws RepositoryException;
}
