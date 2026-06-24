package scot.gov.www.linkrewriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.linkrewriter.locator.*;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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
public class GovScotLinkRewriteStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GovScotLinkRewriteStrategy.class);

    private static final String HIPPO_AVAILABILITY = "hippo:availability";

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
     * The query string (including leading {@code ?}) from a redirect target that was resolved
     * on the last successful {@link #findTargetNode} call, or empty string.  Non-empty only
     * when a redirect was followed and the redirect target URL carried query parameters.
     * Always reset to {@code ""} at the top of {@link #findTargetNode} so it never bleeds
     * across calls.
     */
    String lastRedirectQueryString = "";

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
                new PolicyPathLocator(),
                new GroupPathLocator(),
                new PublicationDocumentsPathLocator(),
                new SectionIndexPathLocator(),
                new DirectPathLocator()
        );
        primaryLocators = List.of(
                new SpecialCasePathLocator(),
                new NewsPathLocator(),
                new PublicationsPathLocator(),
                new PolicyPathLocator(),
                new GroupPathLocator(),
                new PublicationDocumentsPathLocator(),
                new SectionIndexPathLocator(),
                new DirectPathLocator()
        );
        redirectLocator = new RedirectPathLocator(coreLocators, this::extractPath, node -> {
            try {
                return hasPublishedVariant(node);
            } catch (RepositoryException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public Node findTargetNode(Session session, String href, String htmlPath) throws RepositoryException {
        String path = extractPath(href);
        if (path == null) {
            return null;
        }

        path = substringBefore(path, "?");
        path = substringBefore(path, "#");
        path = stripEnd(path, "/");

        lastRedirectQueryString = "";

        boolean foundUnpublished = false;
        String unpublishedLocatorName = null;
        String unpublishedNodePath = null;

        for (PathLocatorStrategy locator : primaryLocators) {
            Optional<Node> result = locator.locate(session, path);
            if (result.isPresent()) {
                boolean published = hasPublishedVariant(result.get());
                LOG.info("GovScotLinkRewriteStrategy: locator '{}' found '{}' for path='{}', published={}",
                        locator.name(), result.get().getPath(), path, published);
                logVariantDetails(result.get(), path);
                if (published) {
                    lastStrategyName = locator.name();
                    return result.get();
                }
                // Record but keep trying — fall through to the redirect locator
                foundUnpublished = true;
                unpublishedLocatorName = locator.name();
                unpublishedNodePath = result.get().getPath();
                LOG.info("GovScotLinkRewriteStrategy: unpublished: path='{}' resolved by {} to {} (no published variant) - trying redirect",
                        path, locator.name(), result.get().getPath());
            }
        }

        LOG.info("GovScotLinkRewriteStrategy: looking up redirect for path='{}' (foundUnpublished={})", path, foundUnpublished);
        Optional<Node> redirectResult = redirectLocator.locate(session, path);
        lastWasHistoricalRedirect = redirectLocator.isLastHistorical();
        lastRedirectQueryString = redirectLocator.getLastRedirectQueryString();
        LOG.info("GovScotLinkRewriteStrategy: redirect lookup for path='{}': present={}, historical={}",
                path, redirectResult.isPresent(), redirectLocator.isLastHistorical());
        return resolveRedirectResult(redirectResult, path, foundUnpublished, unpublishedLocatorName, unpublishedNodePath);
    }

    private Node resolveRedirectResult(
            Optional<Node> redirectResult, String path,
            boolean foundUnpublished, String unpublishedLocatorName, String unpublishedNodePath
    ) throws RepositoryException {
        if (!redirectResult.isPresent()) {
            if (foundUnpublished) {
                LOG.info("GovScotLinkRewriteStrategy: unpublished-no-redirect: path='{}' unpublished ({} -> {}) and no redirect exists",
                        path, unpublishedLocatorName, unpublishedNodePath);
            }
            return null;
        }
        boolean redirectPublished = hasPublishedVariant(redirectResult.get());
        LOG.info("GovScotLinkRewriteStrategy: redirect target for path='{}' -> '{}', published={}",
                path, redirectResult.get().getPath(), redirectPublished);
        logVariantDetails(redirectResult.get(), path);
        if (!redirectPublished) {
            if (foundUnpublished) {
                LOG.info("GovScotLinkRewriteStrategy: unpublished-redirect-also-unpublished: path='{}' unpublished ({} -> {}) and redirect target {} also has no published variant",
                        path, unpublishedLocatorName, unpublishedNodePath, redirectResult.get().getPath());
            } else {
                LOG.info("GovScotLinkRewriteStrategy: unpublished-redirect-target: path='{}' redirect resolves to {} but it has no published variant",
                        path, redirectResult.get().getPath());
            }
            return null;
        }
        if (foundUnpublished) {
            LOG.info("GovScotLinkRewriteStrategy: unpublished-redirect-success: path='{}' was unpublished ({} -> {}) but redirect resolves to published {}",
                    path, unpublishedLocatorName, unpublishedNodePath, redirectResult.get().getPath());
        }
        lastStrategyName = redirectLocator.name();
        return redirectResult.get();
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

    /**
     * Logs the state and availability of every variant of {@code handle} at INFO level.
     * Used to diagnose cases where a handle is unexpectedly treated as published (or not).
     */
    private static void logVariantDetails(Node handle, String path) throws RepositoryException {
        NodeIterator variants = handle.getNodes();
        while (variants.hasNext()) {
            Node variant = variants.nextNode();
            String availability = readAvailability(variant);
            String state;
            if (variant.hasProperty("hippostd:state")) {
                state = variant.getProperty("hippostd:state").getString();
            } else {
                state = "(no hippostd:state property)";
            }
            LOG.info("GovScotLinkRewriteStrategy: variant detail: path='{}' handle='{}' variant='{}' state='{}' availability='{}'",
                    path, handle.getPath(), variant.getName(), state, availability);
        }
    }

    private static String readAvailability(Node variant) throws RepositoryException {
        if (!variant.hasProperty(HIPPO_AVAILABILITY)) {
            return "(no hippo:availability property)";
        }
        StringBuilder avail = new StringBuilder();
        for (javax.jcr.Value v : variant.getProperty(HIPPO_AVAILABILITY).getValues()) {
            if (avail.length() > 0) {
                avail.append(',');
            }
            avail.append(v.getString());
        }
        return avail.length() > 0 ? avail.toString() : "(empty)";
    }

    static boolean hasPublishedVariant(Node handle) throws RepositoryException {
        NodeIterator variants = handle.getNodes();
        while (variants.hasNext()) {
            Node variant = variants.nextNode();
            if (isLive(variant)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the variant is available on the live site, indicated by
     * {@code hippo:availability} containing {@code "live"}.  Checking {@code hippostd:state}
     * alone is not sufficient — a variant can have {@code state=published} but an empty
     * {@code hippo:availability}, meaning it has been taken offline.
     */
    private static boolean isLive(Node variant) throws RepositoryException {
        if (!variant.hasProperty(HIPPO_AVAILABILITY)) {
            return false;
        }
        for (javax.jcr.Value value : variant.getProperty(HIPPO_AVAILABILITY).getValues()) {
            if ("live".equals(value.getString())) {
                return true;
            }
        }
        return false;
    }
}
