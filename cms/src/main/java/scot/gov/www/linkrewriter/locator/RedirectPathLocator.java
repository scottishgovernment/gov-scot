package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.JcrRedirectRepository;
import scot.gov.publishing.hippo.redirects.Redirect;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Resolves URLs by looking them up in the {@link JcrRedirectRepository} and then passing the
 * redirect target back through a set of delegate {@link PathLocatorStrategy} instances.
 *
 * <p>Redirect chains are followed: if the immediate redirect target cannot be resolved by the
 * delegates, the target path is itself looked up in the redirect table and the process repeats.
 * Chains are followed up to {@value #MAX_CHAIN_DEPTH} hops; a cycle or depth overflow causes
 * the lookup to return empty and log a warning.
 *
 * <p>Historical redirects stop the chain at the hop where they are encountered.
 */
public class RedirectPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectPathLocator.class);

    /** Safety cap on redirect chain depth to guard against runaway chains. */
    static final int MAX_CHAIN_DEPTH = 10;

    private final List<PathLocatorStrategy> delegates;
    private final Function<String, String> pathExtractor;
    private final Predicate<Node> isPublished;

    /**
     * @param delegates     strategies to try after resolving the redirect target URL to a path
     * @param pathExtractor function that strips host/scheme from a URL and returns the path
     *                      component (may return {@code null} for non-govscot URLs)
     * @param isPublished   predicate that returns {@code true} if a resolved handle has a
     *                      live published variant; unpublished handles are treated as
     *                      unresolvable and the redirect chain continues
     */
    public RedirectPathLocator(List<PathLocatorStrategy> delegates, Function<String, String> pathExtractor,
                        Predicate<Node> isPublished) {
        this.delegates     = delegates;
        this.pathExtractor = pathExtractor;
        this.isPublished   = isPublished;
    }

    @Override
    public String name() {
        return "redirect";
    }

    /**
     * Set to {@code true} after a {@link #locate} call that found a redirect entry but
     * skipped it because it was marked historical.  {@code false} for all other outcomes
     * (no redirect entry, redirect found and resolved, redirect target not resolvable).
     */
    private boolean lastWasHistorical = false;

    /**
     * The query string (including the leading {@code ?}) from the redirect target that was
     * ultimately resolved, or an empty string.  Set after each {@link #locate} call.
     */
    private String lastRedirectQueryString = "";

    public boolean isLastHistorical() {
        return lastWasHistorical;
    }

    public String getLastRedirectQueryString() {
        return lastRedirectQueryString;
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        lastWasHistorical = false;
        lastRedirectQueryString = "";
        JcrRedirectRepository redirects = new JcrRedirectRepository(session);

        // visited tracks every path whose redirect entry we have looked up, in order.
        // It is used both to detect cycles and to produce useful chain-logging.
        LinkedHashSet<String> visited = new LinkedHashSet<>();
        String currentPath = path;

        while (visited.size() < MAX_CHAIN_DEPTH) {
            if (!visited.add(currentPath)) {
                LOG.warn("RedirectPathLocator: cycle detected for original path='{}': chain was {}",
                        path, chainString(visited, currentPath));
                return Optional.empty();
            }

            Optional<Redirect> redirect = redirects.lookup(currentPath);
            if (redirect.isEmpty()) {
                logChainEndedNoRedirect(path, currentPath, visited);
                return Optional.empty();
            }
            if (redirect.get().isHistoricalUrl()) {
                lastWasHistorical = true;
                LOG.info("RedirectPathLocator: path='{}' chain step '{}' is a historical redirect, stopping (chain: {})",
                        path, currentPath, chainString(visited, null));
                return Optional.empty();
            }

            String redirectTo   = redirect.get().getTo();
            String redirectPath = pathExtractor.apply(redirectTo);
            LOG.info("RedirectPathLocator: path='{}' redirects to='{}' extractedPath='{}'",
                    currentPath, redirectTo, redirectPath);
            if (redirectPath == null) {
                LOG.info("RedirectPathLocator: path='{}' redirect target '{}' could not be extracted as a gov.scot path",
                        currentPath, redirectTo);
                return Optional.empty();
            }

            String hopQueryString = extractHopQueryString(redirectPath, currentPath);
            redirectPath = cleanRedirectPath(redirectPath);
            LOG.info("RedirectPathLocator: path='{}' resolved redirect path='{}'", currentPath, redirectPath);

            Optional<Node> result = tryDelegates(session, currentPath, redirectPath);
            if (result.isPresent()) {
                if (testIsPublished(result.get())) {
                    lastRedirectQueryString = hopQueryString;
                    logChainResolved(path, visited, redirectPath);
                    return result;
                }
                LOG.info("RedirectPathLocator: path='{}' redirect target '{}' resolved to unpublished '{}', following chain",
                        currentPath, redirectPath, result.get().getPath());
            }
            LOG.info("RedirectPathLocator: path='{}' redirect target '{}' not resolved by any delegate, following chain",
                    currentPath, redirectPath);
            currentPath = redirectPath;
        }

        LOG.warn("RedirectPathLocator: path='{}' exceeded max redirect chain depth of {} (chain: {})",
                path, MAX_CHAIN_DEPTH, chainString(visited, currentPath));
        return Optional.empty();
    }

    private Optional<Node> tryDelegates(Session session, String currentPath,
                                        String redirectPath) throws RepositoryException {
        for (PathLocatorStrategy delegate : delegates) {
            Optional<Node> result = delegate.locate(session, redirectPath);
            if (result.isPresent()) {
                LOG.info("RedirectPathLocator: path='{}' redirect target '{}' resolved to '{}' by delegate '{}'",
                        currentPath, redirectPath, result.get().getPath(), delegate.name());
                return result;
            }
        }
        return Optional.empty();
    }

    private void logChainEndedNoRedirect(String path, String currentPath, LinkedHashSet<String> visited) {
        if (visited.size() > 1) {
            LOG.info("RedirectPathLocator: path='{}' chain ended at '{}' with no further redirect (chain: {})",
                    path, currentPath, chainString(visited, null));
        }
    }

    private void logChainResolved(String path, LinkedHashSet<String> visited, String redirectPath) {
        if (visited.size() > 1) {
            LOG.info("RedirectPathLocator: path='{}' resolved after {} redirect hop(s): {}",
                    path, visited.size(), chainString(visited, redirectPath));
        }
    }

    /** Extracts the query string (including leading {@code ?}) from a redirect target URL, logging if non-empty. */
    private static String extractHopQueryString(String redirectPath, String currentPath) {
        int qIdx = redirectPath.indexOf('?');
        if (qIdx < 0) {
            return "";
        }
        String after = redirectPath.substring(qIdx);
        int hIdx = after.indexOf('#');
        String qs = hIdx < 0 ? after : after.substring(0, hIdx);
        LOG.info("RedirectPathLocator: path='{}' redirect target has query string '{}', stripping for lookup",
                currentPath, qs);
        return qs;
    }

    /** Strips the query string and fragment from a redirect target path, and trims trailing slashes. */
    private static String cleanRedirectPath(String redirectPath) {
        int qIdx = redirectPath.indexOf('?');
        if (qIdx >= 0) {
            redirectPath = redirectPath.substring(0, qIdx);
        }
        return stripEnd(substringBefore(redirectPath, "#"), "/");
    }

    private boolean testIsPublished(Node node) throws RepositoryException {
        try {
            return isPublished.test(node);
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof RepositoryException) {
                throw (RepositoryException) e.getCause();
            }
            throw e;
        }
    }

    /** Formats the visited set (plus an optional pending next hop) as {@code a -> b -> c}. */
    private static String chainString(LinkedHashSet<String> visited, String next) {
        StringBuilder sb = new StringBuilder();
        for (String hop : visited) {
            if (sb.length() > 0) {
                sb.append(" -> ");
            }
            sb.append(hop);
        }
        if (next != null) {
            if (sb.length() > 0) {
                sb.append(" -> ");
            }
            sb.append(next);
        }
        return sb.toString();
    }
}
