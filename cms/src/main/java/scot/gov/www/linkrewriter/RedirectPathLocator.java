package scot.gov.www.linkrewriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.JcrRedirectRepository;
import scot.gov.publishing.hippo.redirects.Redirect;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Resolves URLs by looking them up in the {@link JcrRedirectRepository} and then passing the
 * redirect target back through a set of delegate {@link PathLocatorStrategy} instances.
 *
 * <p>Historical redirects are ignored.  Redirect chains are not followed — the target is
 * passed through the delegates exactly once.
 */
class RedirectPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectPathLocator.class);

    private final List<PathLocatorStrategy> delegates;
    private final Function<String, String> pathExtractor;

    /**
     * @param delegates     strategies to try after resolving the redirect target URL to a path
     * @param pathExtractor function that strips host/scheme from a URL and returns the path
     *                      component (may return {@code null} for non-govscot URLs)
     */
    RedirectPathLocator(List<PathLocatorStrategy> delegates, Function<String, String> pathExtractor) {
        this.delegates     = delegates;
        this.pathExtractor = pathExtractor;
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
    boolean lastWasHistorical = false;

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        lastWasHistorical = false;
        JcrRedirectRepository redirects = new JcrRedirectRepository(session);
        Optional<Redirect> redirect = redirects.lookup(path);

        if (redirect.isEmpty()) {
            return Optional.empty();
        }
        if (redirect.get().isHistoricalUrl()) {
            lastWasHistorical = true;
            return Optional.empty();
        }

        String redirectTo   = redirect.get().getTo();
        String redirectPath = pathExtractor.apply(redirectTo);
        if (redirectPath == null) {
            return Optional.empty();
        }
        if (redirectPath.contains("?")) {
            LOG.info("RedirectPathLocator: skipping '{}' — redirect target '{}' contains query parameters",
                    path, redirectTo);
            return Optional.empty();
        }
        redirectPath = stripEnd(substringBefore(redirectPath, "#"), "/");

        for (PathLocatorStrategy delegate : delegates) {
            Optional<Node> result = delegate.locate(session, redirectPath);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
