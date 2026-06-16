package scot.gov.www.linkrewriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Resolves legacy gov.scot links to JCR {@code hippo:handle} nodes by delegating to a
 * chain of {@link PathLocatorStrategy} implementations.
 *
 * <p>Resolution order:
 * <ol>
 *   <li>{@link SpecialCasePathLocator} — explicit hard-coded path overrides.</li>
 *   <li>{@link NewsPathLocator} — {@code /news/<slug>} via slug-lookup table.</li>
 *   <li>{@link PublicationsPathLocator} — {@code /publications/<slug>} and
 *       {@code /isbn/<isbn>} via slug-lookup table or ISBN query.</li>
 *   <li>{@link DirectPathLocator} — direct JCR path mapping, with topic/siteitem
 *       fallback for single-segment paths.</li>
 *   <li>{@link RedirectPathLocator} — looks up the path in the redirect table and
 *       passes the target back through strategies 2–4.</li>
 * </ol>
 *
 * <p>Accepted href forms:
 * <ul>
 *   <li>{@code https://www.gov.scot/…} / {@code http://www.gov.scot/…}</li>
 *   <li>{@code https://www2.gov.scot/…} / {@code http://www2.gov.scot/…}</li>
 *   <li>{@code https://beta.gov.scot/…} / {@code http://beta.gov.scot/…}</li>
 *   <li>Protocol-relative: {@code //www.gov.scot/…}, etc.</li>
 *   <li>No-scheme: {@code www.gov.scot/…}, etc.</li>
 *   <li>Site-relative paths: {@code /some/path} or {@code some/path}</li>
 * </ul>
 *
 * <p>Query strings, fragment identifiers, and trailing slashes are stripped before any
 * lookup.
 */
public class GovScotLinkRewriteStrategy implements LinkRewriteStrategy {

    /**
     * All gov.scot host prefixes whose path component should be resolved.
     * Listed most-specific first so the first match in {@link #extractPath} is always the
     * longest.  Package-private so {@link LinkRewriteJob#needsRewriting} can reuse it.
     */
    static final String[] GOV_SCOT_BASES = {
            "https://www.gov.scot",
            "http://www.gov.scot",
            "https://www2.gov.scot",
            "http://www2.gov.scot",
            "https://beta.gov.scot",
            "http://beta.gov.scot",
            "//www.gov.scot",
            "//www2.gov.scot",
            "//beta.gov.scot",
            "www.gov.scot",
            "www2.gov.scot",
            "beta.gov.scot",
    };

    /** Name of the strategy that resolved the last successful call to {@link #findTargetNode}. */
    String lastStrategyName;

    /**
     * Set to {@code true} after a {@link #findTargetNode} call that failed because the only
     * redirect entry for the path was marked historical.
     */
    boolean lastWasHistoricalRedirect;

    /**
     * Strategies tried in order for each incoming path (excluding redirect, which is the final
     * fallback and delegates back to these).
     */
    private final List<PathLocatorStrategy> primaryLocators;
    private final RedirectPathLocator redirectLocator;

    public GovScotLinkRewriteStrategy() {
        List<PathLocatorStrategy> coreLocators = List.of(
                new NewsPathLocator(),
                new PublicationsPathLocator(),
                new DirectPathLocator()
        );
        primaryLocators = List.of(
                new SpecialCasePathLocator(),
                new NewsPathLocator(),
                new PublicationsPathLocator(),
                new DirectPathLocator()
        );
        redirectLocator = new RedirectPathLocator(coreLocators, this::extractPath);
    }

    @Override
    public Node findTargetNode(Session session, String href, String htmlPath) throws RepositoryException {
        String path = extractPath(href);
        if (path == null) {
            return null;
        }

        path = substringBefore(path, "?");
        path = substringBefore(path, "#");
        path = stripEnd(path, "/");

        for (PathLocatorStrategy locator : primaryLocators) {
            Optional<Node> result = locator.locate(session, path);
            if (result.isPresent()) {
                lastStrategyName = locator.name();
                return result.get();
            }
        }

        Optional<Node> redirectResult = redirectLocator.locate(session, path);
        lastWasHistoricalRedirect = redirectLocator.lastWasHistorical;
        if (redirectResult.isPresent()) {
            lastStrategyName = redirectLocator.name();
            return redirectResult.get();
        }

        return null;
    }

    /**
     * Strips a known gov.scot host prefix from {@code href} and returns the path component,
     * or {@code null} for hrefs that should not be resolved (other hosts, mailto, tel, etc.).
     */
    String extractPath(String href) {
        if (href == null || href.isEmpty()) {
            return null;
        }
        for (String base : GOV_SCOT_BASES) {
            if (href.startsWith(base)) {
                return href.substring(base.length());
            }
        }
        if (href.startsWith("//")) {
            return null;
        }
        if (href.contains("://") || href.startsWith("mailto:") || href.startsWith("tel:")) {
            return null;
        }
        if (href.startsWith("#")) {
            return null;
        }
        return href;
    }
}
