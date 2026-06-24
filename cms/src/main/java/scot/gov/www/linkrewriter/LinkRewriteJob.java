package scot.gov.www.linkrewriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.jcr.FeatureFlag;
import scot.gov.publishing.jcr.SessionSaver;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Scheduled job that rewrites legacy links in {@code hippostd:html} rich-text nodes to
 * Bloomreach portable {@code hippo:facetselect} links.
 *
 * <p>Content migrated from the previous CMS may contain two forms of non-portable link:
 * <ol>
 *   <li>Absolute URLs: {@code https://www.gov.scot/some/path}</li>
 *   <li>Site-relative paths: {@code /some/path}</li>
 * </ol>
 *
 * <p>For each matching {@code href} the configured {@link LinkRewriteStrategy} is asked to
 * resolve it to a JCR {@code hippo:handle} node.  When a handle is found a
 * {@code hippo:facetselect} child node is created (or reused) on the {@code hippostd:html}
 * node and the href in the stored HTML is replaced with the facetselect node's name (the
 * handle's UUID).  The resulting link is portable: if the target document is moved within
 * the repository Bloomreach will keep the link intact.
 *
 * <p>Links that cannot be resolved are logged at DEBUG level and left unchanged.
 *
 * <p><b>Scalability</b>: the job walks the JCR content tree one top-level content area at a
 * time (news, publications, policies, …) using recursive {@link NodeIterator} traversal.
 * No large query result-set is held open; at most one level of child nodes is in memory at
 * any point.  Saves are batched via {@link SessionSaver} (batch size {@value #SAVE_BATCH_SIZE},
 * throttle delay {@value #SAVE_DELAY_MS} ms) to avoid overwhelming the repository.
 *
 * <p><b>Control flag</b> (boolean property under {@code /content/featureflags/}):
 * {@code LinkRewriteJob} — set to {@code true} to start the job.  The flag is re-checked
 * at each top-level content area boundary and every {@value #FLAG_CHECK_INTERVAL} html
 * nodes processed, so setting it to {@code false} mid-run stops the job cleanly.  The job
 * self-disables the flag on full completion.
 *
 * <p><b>Versioned nodes</b>: if modifying a {@code hippostd:html} node throws a
 * {@link VersionException} (because its parent document variant is checked in), the job
 * performs a checkout of the parent variant, applies the changes, saves, and checks the
 * variant back in.
 *
 * <p><b>Idempotency</b>: hrefs that already look like UUIDs are skipped, so re-running
 * after a partial stop will not double-rewrite any link.
 */
public class LinkRewriteJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(LinkRewriteJob.class);

    static final int  SAVE_BATCH_SIZE     = 100;
    static final long SAVE_DELAY_MS       = 1_000L;
    static final int  FLAG_CHECK_INTERVAL = 100;
    static final int  LOG_INTERVAL        = 1_000;

    static final String CONTENT_ROOT = "/content/documents/govscot";

    static final String PARTIAL_RUN_FLAG = "LinkRewriteJobPartialRun";

    static final String HIPPOSTD_FOLDER   = "hippostd:folder";
    static final String HIPPOSTD_CONTENT  = "hippostd:content";
    static final String HIPPO_FACETSELECT = "hippo:facetselect";
    static final String HIPPO_DOCBASE     = "hippo:docbase";
    static final String HIPPO_FACETS      = "hippo:facets";
    static final String HIPPO_MODES       = "hippo:modes";
    static final String HIPPO_VALUES      = "hippo:values";

    private static final String[] EMPTY_STRINGS = new String[0];

    /**
     * UUID pattern: 8-4-4-4-12 hex digits.  Matches hrefs that have already been rewritten
     * to Bloomreach portable facetselect links and should not be touched again.
     */
    private static final Pattern UUID_PATTERN =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}(#.*)?",
                    Pattern.CASE_INSENSITIVE);

    /**
     * Matches hrefs that are safe to pass to {@link javax.jcr.Node#hasNode}: only
     * alphanumerics, hyphens, and underscores — the characters found in UUIDs and simple
     * node names.  Excludes {@code =}, {@code [}, spaces, and other characters that
     * Jackrabbit cannot parse as a JCR path segment.
     */
    private static final Pattern SIMPLE_NODE_NAME =
            Pattern.compile("[A-Za-z0-9_\\-]+");

    GovScotLinkRewriteStrategy strategy = new GovScotLinkRewriteStrategy();

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        Session session = context.createSystemSession();
        try {
            FeatureFlag flag = new FeatureFlag(session, LinkRewriteJob.class.getSimpleName());
            if (!flag.isEnabled()) {
                LOG.debug("LinkRewriteJob is not enabled, skipping");
                return;
            }
            doExecute(session, flag);
        } catch (Exception e) {
            LOG.error("LinkRewriteJob: unexpected error", e);
        } finally {
            session.logout();
        }
    }

    void doExecute(Session session, FeatureFlag flag) throws RepositoryException {
        SessionSaver saver = new SessionSaver(session, SAVE_BATCH_SIZE, SAVE_DELAY_MS);
        Stats stats = new Stats();
        long startMs = System.currentTimeMillis();

        try {
            walkContentAreas(session, saver, stats, flag);
        } catch (JobStoppedException e) {
            saver.forceSave();
            logStats(stats);
            LOG.debug("JobStoppedException", e);
            LOG.info("LinkRewriteJob: stopped early by operator after {}", formatDuration(System.currentTimeMillis() - startMs));
            return;
        } catch (RepositoryException e) {
            LOG.error("LinkRewriteJob: error during processing after {}; saving progress",
                    formatDuration(System.currentTimeMillis() - startMs), e);
            logStats(stats);
            saver.forceSave();
            return;
        }

        saver.forceSave();
        logFinalStats(stats, System.currentTimeMillis() - startMs);
        flag.setEnabled(false);
        session.save();
        LOG.info("LinkRewriteJob: self-disabled feature flag");
    }

    private static String formatDuration(long ms) {
        long hours   = ms / 3_600_000;
        long minutes = (ms % 3_600_000) / 60_000;
        long seconds = (ms % 60_000) / 1_000;
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        }
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }

    // ---- Tree walk -------------------------------------------------------------------------

    /**
     * Iterates the direct children of {@value #CONTENT_ROOT} (news, publications, policies,
     * etc.) and walks each subtree in turn.  The feature flag is checked before each area so
     * the job can be stopped on an area boundary as well as during processing.
     *
     * <p>When the {@value #PARTIAL_RUN_FLAG} feature flag is enabled only the {@code about}
     * area, the February ({@code 02}) month folder across every year of {@code news}, and the
     * February folder across every year within each {@code publications} type subfolder are
     * processed — a cross-year sample rather than a single recent snapshot.
     */
    private void walkContentAreas(Session session, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        if (!session.nodeExists(CONTENT_ROOT)) {
            LOG.warn("LinkRewriteJob: content root {} not found, aborting", CONTENT_ROOT);
            return;
        }

        if (new FeatureFlag(session, PARTIAL_RUN_FLAG).isEnabled()) {
            walkPartialContentAreas(session, saver, stats, flag);
            return;
        }

        NodeIterator areas = session.getNode(CONTENT_ROOT).getNodes();
        while (areas.hasNext()) {
            Node area = areas.nextNode();
            if (!area.isNodeType(HIPPOSTD_FOLDER)) {
                continue;
            }
            if (!flag.isEnabled()) {
                throw new JobStoppedException();
            }
            LOG.info("LinkRewriteJob: starting content area {}", area.getPath());
            walkNode(area, saver, stats, flag);
            LOG.info("LinkRewriteJob: finished content area {}", area.getPath());
            logStats(stats);
        }
    }

    /**
     * Partial-run variant of {@link #walkContentAreas}: processes only the {@code about}
     * section in full, plus the most-recent calendar year under {@code news} and under
     * each type subfolder of {@code publications}.
     */
    private void walkPartialContentAreas(Session session, SessionSaver saver, Stats stats,
                                         FeatureFlag flag) throws RepositoryException {
        LOG.info("LinkRewriteJob [partial]: running in partial mode");

        // about — walk in full
        walkAreaIfExists(session, saver, stats, flag, CONTENT_ROOT + "/about");

        // news — January across all years
        walkJanuaryAcrossAllYears(session, saver, stats, flag, CONTENT_ROOT + "/news");

        // publications — January across all years, under each type subfolder
        String pubPath = CONTENT_ROOT + "/publications";
        if (session.nodeExists(pubPath)) {
            NodeIterator types = session.getNode(pubPath).getNodes();
            while (types.hasNext()) {
                Node type = types.nextNode();
                if (!flag.isEnabled()) {
                    throw new JobStoppedException();
                }

                if (type.isNodeType(HIPPOSTD_FOLDER)) {
                    walkJanuaryAcrossAllYears(session, saver, stats, flag, type.getPath());
                }
            }
        }
    }

    private void walkAreaIfExists(Session session, SessionSaver saver, Stats stats,
                                  FeatureFlag flag, String path) throws RepositoryException {
        if (!flag.isEnabled()) {
            throw new JobStoppedException();
        }
        if (!session.nodeExists(path)) {
            LOG.warn("LinkRewriteJob [partial]: {} not found, skipping", path);
            return;
        }
        Node area = session.getNode(path);
        LOG.info("LinkRewriteJob [partial]: starting {}", path);
        walkNode(area, saver, stats, flag);
        LOG.info("LinkRewriteJob [partial]: finished {}", path);
        logStats(stats);
    }

    /**
     * Walks the {@code 02} (February) subfolder of every year folder found under
     * {@code parentPath}.  Year folders are identified by a four-digit numeric name.
     */
    private void walkJanuaryAcrossAllYears(Session session, SessionSaver saver, Stats stats,
                                           FeatureFlag flag, String parentPath)
            throws RepositoryException {
        if (!session.nodeExists(parentPath)) {
            LOG.warn("LinkRewriteJob [partial]: {} not found, skipping", parentPath);
            return;
        }
        NodeIterator children = session.getNode(parentPath).getNodes();
        while (children.hasNext()) {
            Node child = children.nextNode();
            if (child.isNodeType(HIPPOSTD_FOLDER) && isYearName(child.getName())) {
                walkAreaIfExists(session, saver, stats, flag, child.getPath() + "/02");
            }
        }
    }

    private static boolean isYearName(String name) {
        if (name.length() != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Visits {@code node}: if it is a {@code hippostd:html} node it is processed for link
     * rewriting; otherwise all child nodes are visited recursively.
     *
     * <p>JCR system-namespace children ({@code jcr:*}, {@code rep:*}) and
     * {@code hippo:facetselect} nodes are skipped — the latter avoids descending into
     * child nodes that this job itself adds to html nodes.
     */
    private void walkNode(Node node, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        stats.visited++;
        stats.lastVisitedPath = node.getPath();
        logProgress(stats);

        if (node.isNodeType("hippostd:html")) {
            processHtmlNode(node, saver, stats, flag);
            return;
        }

        NodeIterator children = node.getNodes();
        while (children.hasNext()) {
            Node child = children.nextNode();
            String name = child.getName();
            if (!name.startsWith("jcr:") && !name.startsWith("rep:") && !child.isNodeType(HIPPO_FACETSELECT)) {
                walkNode(child, saver, stats, flag);
            }
        }
    }

    // ---- Per-node processing ---------------------------------------------------------------

    private void processHtmlNode(Node htmlNode, SessionSaver saver, Stats stats, FeatureFlag flag)
            throws RepositoryException {
        stats.processed++;

        String html = htmlNode.getProperty(HIPPOSTD_CONTENT).getString();
        Document doc = Jsoup.parse(html);

        List<LinkReplacement> replacements = collectReplacements(htmlNode, doc, stats);
        if (replacements.isEmpty()) {
            return;
        }

        applyReplacements(htmlNode, replacements, stats);
        saver.save();
        checkStopFlag(flag, stats);
    }

    // ---- Link collection -------------------------------------------------------------------

    private List<LinkReplacement> collectReplacements(Node htmlNode, Document doc, Stats stats)
            throws RepositoryException {
        List<LinkReplacement> replacements = new ArrayList<>();
        for (Element link : doc.select("a[href]")) {
            LinkReplacement replacement = processLink(htmlNode, link.attr("href"), stats);
            if (replacement != null) {
                replacements.add(replacement);
            }
        }
        return replacements;
    }

    private LinkReplacement processLink(Node htmlNode, String href, Stats stats) throws RepositoryException {
        // Bare names pointing to an existing facetselect child are already in the correct format
        if (isSafeNodeName(href) && htmlNode.hasNode(href) && htmlNode.getNode(href).isNodeType(HIPPO_FACETSELECT)) {
            LOG.debug("LinkRewriteJob: skipping href='{}' in {} — already a facetselect child node",
                    href, htmlNode.getPath());
            return null;
        }
        if (!needsRewriting(href)) {
            return null;
        }
        stats.considered++;
        Node target;
        try {
            target = strategy.findTargetNode(htmlNode.getSession(), href, htmlNode.getPath());
        } catch (RepositoryException e) {
            LOG.warn("LinkRewriteJob: error resolving href='{}' in {}, skipping", href, htmlNode.getPath(), e);
            recordUnresolvable(href, false, stats);
            return null;
        }
        if (target == null) {
            if (isBinariesHref(href)) {
                LOG.debug("LinkRewriteJob: cannot resolve href='{}' in {}", href, htmlNode.getPath());
            } else {
                LOG.info("LinkRewriteJob: cannot resolve href='{}' in {}", href, htmlNode.getPath());
            }
            recordUnresolvable(href, strategy.lastWasHistoricalRedirect, stats);
            return null;
        }
        String queryString = extractQueryString(href);
        if (queryString.isEmpty()) {
            queryString = strategy.lastRedirectQueryString;
        }
        String fragment = extractFragment(href);
        return new LinkReplacement(href, target, strategy.lastStrategyName, queryString + fragment);
    }

    private static void recordUnresolvable(String href, boolean wasHistoricalRedirect, Stats stats) {
        stats.notRewritten++;
        if (isBinariesHref(href)) {
            stats.binariesUnresolvable++;
            addUnresolvableExample(stats.binariesExamples, href);
        }
        if (wasHistoricalRedirect) {
            stats.historicalRedirectUnresolvable++;
            addUnresolvableExample(stats.historicalRedirectExamples, href);
        }
        if (isHtmlExtensionHref(href)) {
            stats.htmlExtensionUnresolvable++;
            addUnresolvableExample(stats.htmlExtensionExamples, href);
        }
        stats.unresolvableByHref.merge(href, 1, Integer::sum);
    }

    private static void addUnresolvableExample(Set<String> examples, String href) {
        if (examples.size() < MAX_EXAMPLES) {
            examples.add(href);
        }
    }

    private static boolean isHtmlExtensionHref(String href) {
        // Matches old SCT-style URLs that carry a .htm or .html file extension
        String lower = href.toLowerCase();
        int query = lower.indexOf('?');
        String pathPart = query >= 0 ? lower.substring(0, query) : lower;
        // trim whitespace so embedded newlines (e.g. href='\n  SCT001.htm') are handled
        pathPart = pathPart.trim();
        return pathPart.endsWith(".html") || pathPart.endsWith(".htm");
    }

    private static final int MAX_EXAMPLES = 50;

    private static void addExample(Stats stats, String strategyName, String from, String toJcrPath,
                                   String location) {
        LinkedHashMap<String, String> examples =
                stats.examplesByStrategy.computeIfAbsent(strategyName, k -> new LinkedHashMap<>());
        if (examples.size() < MAX_EXAMPLES) {
            // putIfAbsent keeps uniqueness by from+to; location is recorded from first occurrence
            examples.putIfAbsent(from + " → " + toJcrPath, location);
        }
    }

    private static boolean isSafeNodeName(String href) {
        return !href.startsWith("/") && !href.contains(":") && SIMPLE_NODE_NAME.matcher(href).matches();
    }

    private static boolean isBinariesHref(String href) {
        return href.contains("/binaries/");
    }

    /**
     * Returns the fragment identifier from {@code href} including the leading {@code #},
     * or an empty string if there is no fragment.
     */
    static String extractFragment(String href) {
        int hIdx = href.indexOf('#');
        return hIdx < 0 ? "" : href.substring(hIdx);
    }

    /**
     * Returns the query string from {@code href} including the leading {@code ?},
     * or an empty string if there is no query string.  A fragment ({@code #…}) is not included.
     */
    static String extractQueryString(String href) {
        int qIdx = href.indexOf('?');
        if (qIdx < 0) {
            return "";
        }
        String after = href.substring(qIdx);
        int hIdx = after.indexOf('#');
        return hIdx < 0 ? after : after.substring(0, hIdx);
    }


    /**
     * Returns {@code true} when {@code href} is a legacy link that should be rewritten:
     * any absolute or protocol-relative or scheme-less gov.scot URL, or a site-relative path.
     *
     * <p>Returns {@code false} for:
     * <ul>
     *   <li>Bare fragment identifiers ({@code #section})</li>
     *   <li>Protocol-relative URLs to non-gov.scot hosts ({@code //otherdomain.com/…})</li>
     *   <li>Any other URI scheme — detected by a colon appearing before the first slash, which
     *       covers well-formed URLs ({@code http://…}), malformed ones ({@code http:}),
     *       and other schemes ({@code mailto:}, {@code tel:}, {@code javascript:}, …)</li>
     *   <li>Hrefs that already look like Bloomreach UUIDs</li>
     * </ul>
     */
    boolean needsRewriting(String href) {
        if (href == null || href.isEmpty() || href.startsWith("#")) {
            return false;
        }
        if (UUID_PATTERN.matcher(href).matches()) {
            return false;
        }
        // Accept any known gov.scot URL form (any scheme, protocol-relative, or no scheme)
        for (String base : GovScotLinkRewriteStrategy.GOV_SCOT_BASES) {
            if (href.startsWith(base)) {
                return true;
            }
        }
        // Protocol-relative URLs to non-gov.scot hosts should not be rewritten
        if (href.startsWith("//")) {
            return false;
        }
        // Reject any other URI scheme: a colon before the first slash covers
        // http://, mailto:, tel:, javascript:, etc.
        int colonPos = href.indexOf(':');
        if (colonPos >= 0) {
            int slashPos = href.indexOf('/');
            return slashPos >= 0 && colonPos >= slashPos;
        }
        return true;
    }

    // ---- Replacement application -----------------------------------------------------------

    /**
     * Applies all collected link replacements to {@code htmlNode}, handling the case where
     * the parent document variant is checked in by performing a checkout/checkin cycle on
     * the variant.
     */
    private void applyReplacements(Node htmlNode, List<LinkReplacement> replacements, Stats stats)
            throws RepositoryException {
        try {
            doApplyReplacements(htmlNode, replacements, stats);
        } catch (VersionException e) {
            LOG.trace("VersionException on {}", htmlNode.getPath(), e);
            String variantPath = htmlNode.getParent().getPath();
            LOG.trace("LinkRewriteJob: variant is checked in, using checkout/checkin for {}", variantPath);
            Session session = htmlNode.getSession();
            VersionManager vm = session.getWorkspace().getVersionManager();
            vm.checkout(variantPath);
            doApplyReplacements(htmlNode, replacements, stats);
            session.save();
            vm.checkin(variantPath);
            LOG.trace("LinkRewriteJob: checkout/checkin complete for {}", variantPath);
        }
    }

    private void doApplyReplacements(Node htmlNode, List<LinkReplacement> replacements, Stats stats)
            throws RepositoryException {
        // Re-read the current HTML in case an earlier batch already touched this node
        String html = htmlNode.getProperty(HIPPOSTD_CONTENT).getString();

        for (LinkReplacement r : replacements) {
            Node facet = ensureFacetSelect(htmlNode, r.targetHandle);
            String facetName = facet.getName();
            html = html.replace("\"" + r.originalHref + "\"", "\"" + facetName + r.queryString + "\"");
            stats.rewritten++;
            stats.rewrittenByStrategy.merge(r.strategyName, 1, Integer::sum);
            addExample(stats, r.strategyName, r.originalHref, r.targetHandle.getPath(), htmlNode.getPath());
            String pageUrl = jcrPathToPageUrl(htmlNode.getPath());
            LOG.info("LinkRewriteJob rewrite: {},{},{},{},{}", r.strategyName, contentArea(htmlNode.getPath()), pageUrl != null ? pageUrl : "", r.originalHref, htmlNode.getPath());
        }

        htmlNode.setProperty(HIPPOSTD_CONTENT, html);
    }

    /**
     * Returns an existing {@code hippo:facetselect} child of {@code htmlNode} whose
     * {@code hippo:docbase} already points to {@code targetHandle}, or creates a new one.
     *
     * <p>The node is named with the handle's UUID, matching the convention the CMS uses when
     * editors insert internal links through the link picker.
     */
    private Node ensureFacetSelect(Node htmlNode, Node targetHandle) throws RepositoryException {
        String uuid = targetHandle.getIdentifier();

        NodeIterator children = htmlNode.getNodes();
        while (children.hasNext()) {
            Node child = children.nextNode();
            if (HIPPO_FACETSELECT.equals(child.getPrimaryNodeType().getName())
                    && uuid.equals(child.getProperty(HIPPO_DOCBASE).getString())) {
                return child;
            }
        }

        Node facet = htmlNode.addNode(uuid, HIPPO_FACETSELECT);
        facet.setProperty(HIPPO_DOCBASE, uuid);
        facet.setProperty(HIPPO_FACETS, EMPTY_STRINGS);
        facet.setProperty(HIPPO_MODES, EMPTY_STRINGS);
        facet.setProperty(HIPPO_VALUES, EMPTY_STRINGS);
        return facet;
    }

    // ---- Flow control ----------------------------------------------------------------------

    private void checkStopFlag(FeatureFlag flag, Stats stats) {
        if (stats.processed > 0 && stats.processed % FLAG_CHECK_INTERVAL == 0 && !flag.isEnabled()) {
            throw new JobStoppedException();
        }
    }

    private void logProgress(Stats stats) {
        if (stats.visited % LOG_INTERVAL == 0) {
            logStats(stats);
        }
    }

    private void logFinalStats(Stats stats, long elapsedMs) {
        LOG.info("LinkRewriteJob: complete in {}", formatDuration(elapsedMs));
        logStats(stats);

        // Examples per strategy (up to 50 unique from→to pairs, with location of first occurrence)
        stats.examplesByStrategy.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String examples = entry.getValue().entrySet().stream()
                            .map(e -> "  " + e.getKey() + " [in " + e.getValue() + "]")
                            .collect(Collectors.joining("\n"));
                    LOG.info("LinkRewriteJob: examples for strategy '{}':\n{}", entry.getKey(), examples);
                });

        // Unresolvable examples per category
        logUnresolvableExamples("binaries",          stats.binariesExamples);
        logUnresolvableExamples("historical-redirect", stats.historicalRedirectExamples);
        logUnresolvableExamples("html-extension",    stats.htmlExtensionExamples);
        String repeatedMisses = stats.unresolvableByHref.entrySet().stream()
                .filter(e -> e.getValue() > 20)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> "  " + e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining("\n"));
        if (!repeatedMisses.isEmpty()) {
            LOG.info("LinkRewriteJob: unresolvable hrefs appearing >20 times:\n{}", repeatedMisses);
        }

        logChangedPageUrls(stats);
    }

    /**
     * Logs the public-facing URLs of pages whose HTML was rewritten, derived from the
     * JCR paths recorded in {@link Stats#examplesByStrategy}.
     *
     * <p>Only publications, news, and about paths can be reliably converted to URLs;
     * paths in other content areas are logged separately so they can be investigated.
     */
    private void logChangedPageUrls(Stats stats) {
        LinkedHashSet<String> urls    = new LinkedHashSet<>();
        LinkedHashSet<String> skipped = new LinkedHashSet<>();

        stats.examplesByStrategy.values().stream()
                .flatMap(examples -> examples.values().stream())
                .forEach(location -> {
                    String url = jcrPathToPageUrl(location);
                    if (url != null) {
                        urls.add(url);
                    } else {
                        skipped.add(location);
                    }
                });

        if (!urls.isEmpty()) {
            String lines = urls.stream()
                    .map(u -> "  " + u)
                    .collect(Collectors.joining("\n"));
            LOG.info("LinkRewriteJob: changed pages (sample):\n{}", lines);
        }
        if (!skipped.isEmpty()) {
            String lines = skipped.stream()
                    .map(p -> "  " + p)
                    .collect(Collectors.joining("\n"));
            LOG.info("LinkRewriteJob: changed pages - could not derive URL for {} location(s):\n{}", skipped.size(), lines);
        }
    }

    /**
     * Derives a best-effort public URL from a JCR html-node path.
     *
     * <ul>
     *   <li><b>publications</b>: {@code .../publications/<type>/<year>/<month>/<slug>/…}
     *       → {@code https://www.gov.scot/publications/<slug>/}</li>
     *   <li><b>news</b>: {@code .../news/<year>/<month>/<slug>/…}
     *       → {@code https://www.gov.scot/news/<slug>/}</li>
     *   <li><b>about</b>, <b>groups</b>, <b>policies</b>: the JCR path maps directly to the
     *       URL path — trailing namespace-prefixed segments (variant node, html field) are
     *       stripped and the remainder is used as-is.</li>
     *   <li><b>others</b>: returns {@code null}.</li>
     * </ul>
     */
    static String contentArea(String jcrPath) {
        String prefix = CONTENT_ROOT + "/";
        if (!jcrPath.startsWith(prefix)) {
            return "other";
        }
        String remainder = jcrPath.substring(prefix.length());
        int slash = remainder.indexOf('/');
        return slash < 0 ? remainder : remainder.substring(0, slash);
    }

    static String jcrPathToPageUrl(String jcrPath) {
        String prefix = CONTENT_ROOT + "/";
        if (!jcrPath.startsWith(prefix)) {
            return null;
        }
        String[] segments = jcrPath.substring(prefix.length()).split("/");
        if (segments.length == 0 || segments[0].isEmpty()) {
            return null;
        }
        String area = segments[0];
        switch (area) {
            case "publications":
                return buildPublicationUrl(segments);
            case "news":
                return buildNewsUrl(segments);
            case "about":
            case "groups":
                return buildDirectMappingUrl(segments);
            case "policies":
                return buildPoliciesUrl(segments);
            default:
                return null;
        }
    }

    /** Builds a URL for a publication: {@code publications/<type>/<year>/<month>/<slug>/…}. */
    private static String buildPublicationUrl(String[] segments) {
        // layout: publications / <type> / <year> / <month> / <slug> / ...
        if (segments.length < 5) {
            return null;
        }
        return "https://www.gov.scot/publications/" + segments[4] + "/";
    }

    /** Builds a URL for a news article: {@code news/<year>/<month>/<slug>/…}. */
    private static String buildNewsUrl(String[] segments) {
        // layout: news / <year> / <month> / <slug> / ...
        if (segments.length < 4) {
            return null;
        }
        return "https://www.gov.scot/news/" + segments[3] + "/";
    }

    /**
     * Builds a URL for a policy page.  Policies are folders containing an {@code index}
     * handle for the top-level page and sibling handles for sub-pages.  The {@code index}
     * segment is dropped from the URL so that
     * {@code .../policies/mypolicy/index/govscot:…} → {@code /policies/mypolicy/} and
     * {@code .../policies/mypolicy/mysubpolicy/govscot:…} → {@code /policies/mypolicy/mysubpolicy/}.
     */
    private static String buildPoliciesUrl(String[] segments) {
        int end = segments.length;
        while (end > 0 && segments[end - 1].contains(":")) {
            end--;
        }
        if (end > 0 && "index".equals(segments[end - 1])) {
            end--;
        }
        if (end == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("https://www.gov.scot/");
        for (int i = 0; i < end; i++) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(segments[i]);
        }
        sb.append('/');
        return sb.toString();
    }

    /**
     * Builds a URL for areas where the JCR path maps directly to the URL path
     * (about, groups).  Trailing namespace-prefixed segments — the Hippo
     * variant node name and html-field name (both contain {@code :}) — are stripped,
     * and the remaining segments are joined to form the URL path.
     */
    private static String buildDirectMappingUrl(String[] segments) {
        int end = segments.length;
        while (end > 0 && segments[end - 1].contains(":")) {
            end--;
        }
        if (end == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("https://www.gov.scot/");
        for (int i = 0; i < end; i++) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(segments[i]);
        }
        sb.append('/');
        return sb.toString();
    }

    private void logUnresolvableExamples(String category, Set<String> examples) {
        if (examples.isEmpty()) {
            return;
        }
        String lines = examples.stream()
                .map(e -> "  " + e)
                .collect(Collectors.joining("\n"));
        LOG.info("LinkRewriteJob: unresolvable examples [{}]:\n{}", category, lines);
    }

    private void logStats(Stats stats) {
        LOG.info("LinkRewriteJob: nodesVisited={}, htmlNodes={}, considered={}, rewritten={}, notRewritten={} lastPath={}",
                stats.visited, stats.processed, stats.considered, stats.rewritten,
                stats.notRewritten, stats.lastVisitedPath);
        if (stats.notRewritten > 0) {
            int repeatedCount = (int) stats.unresolvableByHref.values().stream().filter(n -> n > 20).count();
            LOG.info("LinkRewriteJob: unresolvable by category: binaries={}, historicalRedirect={}, htmlExtension={}, repeated>20={}",
                    stats.binariesUnresolvable, stats.historicalRedirectUnresolvable,
                    stats.htmlExtensionUnresolvable, repeatedCount);
        }
        if (!stats.rewrittenByStrategy.isEmpty()) {
            String byStrategy = stats.rewrittenByStrategy.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(", "));
            LOG.info("LinkRewriteJob: rewritten by strategy: {}", byStrategy);
        }
    }

    // ---- Inner types -----------------------------------------------------------------------

    private static class Stats {
        // tree walk counters
        int visited          = 0;
        int processed        = 0;
        // link counters
        int considered       = 0;
        int rewritten        = 0;
        int notRewritten     = 0;
        // unresolvable categories (counts + up to 50 examples each for end-of-job investigation)
        int binariesUnresolvable           = 0;
        int historicalRedirectUnresolvable = 0;
        int htmlExtensionUnresolvable      = 0;
        Set<String> binariesExamples           = new LinkedHashSet<>();
        Set<String> historicalRedirectExamples = new LinkedHashSet<>();
        Set<String> htmlExtensionExamples      = new LinkedHashSet<>();
        /** Rewrite counts keyed by strategy name (e.g. {@code "news"}, {@code "path"}). */
        Map<String, Integer> rewrittenByStrategy = new HashMap<>();
        /**
         * Up to 50 example rewrites per strategy.  Key: strategy name.  Value: LinkedHashMap
         * whose key is {@code "from → toJcrPath"} (uniqueness) and whose value is the JCR path
         * of the html node in which the rewrite was made (location).
         */
        Map<String, LinkedHashMap<String, String>> examplesByStrategy = new HashMap<>();
        String lastVisitedPath = null;
        /** Exact hrefs that could not be resolved, counted across all documents. */
        Map<String, Integer> unresolvableByHref = new HashMap<>();
    }

    private static class LinkReplacement {
        final String originalHref;
        final Node   targetHandle;
        final String strategyName;
        /** Query string to preserve, including the leading {@code ?}, or empty string. */
        final String queryString;

        LinkReplacement(String originalHref, Node targetHandle, String strategyName, String queryString) {
            this.originalHref = originalHref;
            this.targetHandle = targetHandle;
            this.strategyName = strategyName;
            this.queryString  = queryString;
        }
    }

    private static class JobStoppedException extends RuntimeException {
        JobStoppedException() {
            super("LinkRewriteJob stopped by operator (flag disabled)");
        }
    }
}
