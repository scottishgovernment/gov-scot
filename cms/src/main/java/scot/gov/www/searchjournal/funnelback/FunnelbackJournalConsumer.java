package scot.gov.www.searchjournal.funnelback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjournal.JournalConsumer;
import scot.gov.publishing.searchjournal.JournalConsumerException;
import scot.gov.publishing.searchjournal.SearchJournalEntry;
import scot.gov.publishing.searchjournal.SiteContentFetcher;
import scot.gov.publishing.searchjournal.funnelback.Funnelback;
import scot.gov.publishing.searchjournal.funnelback.FunnelbackCollection;
import scot.gov.publishing.searchjournal.funnelback.FunnelbackException;

import org.apache.commons.lang3.StringUtils;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Set;

/**
 * {@link JournalConsumer} that pushes content to Funnelback's push API.
 *
 * <p>HTML is fetched via the injected {@link SiteContentFetcher}. The Funnelback collection
 * is determined at consume time by inspecting the document's JCR node type, so that
 * collection assignment (a Funnelback-specific detail) is not mixed into the journal
 * recording layer.
 */
public class FunnelbackJournalConsumer implements JournalConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackJournalConsumer.class);

    private static final Set<String> NEWS_TYPES = Set.of("govscot:News");

    private static final Set<String> POLICY_TYPES = Set.of("govscot:Policy", "govscot:PolicyInDetail");

    private static final Set<String> PUBLICATION_TYPES = Set.of(
            "govscot:Publication",
            "govscot:ComplexDocument2",
            "govscot:Consultation",
            "govscot:FOI",
            "govscot:Minutes",
            "govscot:SpeechOrStatement"
    );

    /**
     * Child content types that do not carry {@code govscot:publicationType} themselves.
     * For these the collection is determined by navigating up to the parent publication folder
     * and reading the type from the {@code index} handle.
     */
    private static final Set<String> CHILD_TYPES = Set.of(
            "govscot:PublicationPage",
            "govscot:DocumentInformation",
            "govscot:ComplexDocumentSection"
    );

    /**
     * Number of {@code /} characters in the path of a publication folder, e.g.
     * {@code /content/documents/govscot/publications/2024/01/my-publication} = 8.
     */
    private static final int PUBLICATION_FOLDER_DEPTH = 8;

    private final Funnelback funnelback;

    private final SiteContentFetcher fetcher;

    private final Session session;

    public FunnelbackJournalConsumer(Funnelback funnelback, SiteContentFetcher fetcher, Session session) {
        this.funnelback = funnelback;
        this.fetcher = fetcher;
        this.session = session;
    }

    @Override
    public boolean isReady() {
        return fetcher.isPingResponding();
    }

    @Override
    public void publish(SearchJournalEntry entry) throws JournalConsumerException {
        try {
            String collection = collectionFor(entry);
            if (collection == null) {
                LOG.warn("Could not determine collection for {}, skipping publish", entry.getUrl());
                return;
            }
            String html = fetcher.getHtml(entry.getUrl());
            if (html == null) {
                // non-200 response already logged by the fetcher
                return;
            }
            funnelback.publish(collection, entry.getUrl(), html);
        } catch (IOException e) {
            throw new JournalConsumerException("Failed to fetch HTML for " + entry.getUrl(), e);
        } catch (FunnelbackException e) {
            throw new JournalConsumerException("Failed to publish " + entry.getUrl(), e);
        } catch (RepositoryException e) {
            throw new JournalConsumerException("Failed to determine collection for " + entry.getUrl(), e);
        }
    }

    @Override
    public void depublish(SearchJournalEntry entry) throws JournalConsumerException {
        try {
            String collection = collectionFor(entry);
            if (collection == null) {
                LOG.warn("Could not determine collection for {}, skipping depublish", entry.getUrl());
                return;
            }
            funnelback.depublish(collection, entry.getUrl());
        } catch (FunnelbackException e) {
            throw new JournalConsumerException("Failed to depublish " + entry.getUrl(), e);
        } catch (RepositoryException e) {
            throw new JournalConsumerException("Failed to determine collection for " + entry.getUrl(), e);
        }
    }

    @Override
    public void close() {
        funnelback.close();
        try {
            fetcher.close();
        } catch (IOException e) {
            LOG.error("Failed to close site content fetcher", e);
        }
    }

    /**
     * Determines the Funnelback collection for the given journal entry.
     *
     * <p>When a {@code contentId} (handle UUID) is present, the collection is resolved by
     * inspecting the document's JCR node type — the authoritative source. When no
     * {@code contentId} is set (e.g. path-derived depublish entries for moved folders),
     * the collection is inferred from the URL pattern.
     *
     * <p>Returns {@code null} if the collection cannot be determined, which causes the
     * entry to be skipped.
     */
    String collectionFor(SearchJournalEntry entry) throws RepositoryException {
        String handleUuid = entry.getContentId();
        if (handleUuid == null) {
            return collectionForUrl(entry.getUrl());
        }
        try {
            Node handle = session.getNodeByIdentifier(handleUuid);
            return collectionForHandle(handle);
        } catch (ItemNotFoundException e) {
            LOG.error("Handle {} not found when determining collection", handleUuid, e);
            return null;
        }
    }

    String collectionForHandle(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes(handle.getName());
        if (!it.hasNext()) {
            return null;
        }
        Node variant = it.nextNode();
        String primaryType = variant.getPrimaryNodeType().getName();

        if (NEWS_TYPES.contains(primaryType)) {
            return FunnelbackCollection.NEWS.getCollectionName();
        }

        if (POLICY_TYPES.contains(primaryType)) {
            return FunnelbackCollection.POLICY.getCollectionName();
        }

        if (PUBLICATION_TYPES.contains(primaryType)) {
            return publicationCollection(variant);
        }

        if (CHILD_TYPES.contains(primaryType)) {
            return collectionForChildNode(handle);
        }

        LOG.warn("collectionForHandle: unrecognised node type '{}' for handle {}", primaryType, handle.getPath());
        return null;
    }

    /**
     * Resolves the Funnelback collection for a child node (page, document, or chapter) by
     * navigating up to the parent publication folder, loading the {@code index} handle, and
     * reading the publication type from there.
     *
     * <p>Publication folder structure:
     * <pre>
     *   {pub-folder}/           (hippostd:folder, depth 8)
     *     index                 (hippo:handle -> govscot:Publication / govscot:ComplexDocument2 / …)
     *     pages/
     *       {page-handle}       (govscot:PublicationPage)
     *     documents/
     *       [{subfolder}/]
     *         {doc-handle}      (govscot:DocumentInformation)
     *     chapters/
     *       {group}/
     *         {chapter-handle}  (govscot:ComplexDocumentSection)
     * </pre>
     */
    private String collectionForChildNode(Node handle) throws RepositoryException {
        Node pubFolder = publicationFolder(handle);
        if (pubFolder == null) {
            LOG.warn("collectionForChildNode: could not find publication folder for {}", handle.getPath());
            return null;
        }
        if (!pubFolder.hasNode("index")) {
            LOG.warn("collectionForChildNode: no index handle in publication folder {}", pubFolder.getPath());
            return null;
        }
        Node indexHandle = pubFolder.getNode("index");
        NodeIterator it = indexHandle.getNodes(indexHandle.getName());
        if (!it.hasNext()) {
            return null;
        }
        return publicationCollection(it.nextNode());
    }

    /**
     * Returns the Funnelback collection name for a publication variant node by reading
     * {@code govscot:publicationType}.
     */
    private String publicationCollection(Node variant) throws RepositoryException {
        String publicationType = variant.hasProperty("govscot:publicationType")
                ? variant.getProperty("govscot:publicationType").getString()
                : "";
        return FunnelbackCollection.getCollectionByPublicationType(publicationType).getCollectionName();
    }

    /**
     * Walks up the JCR tree from {@code node} until it finds the publication folder —
     * the {@code hippostd:folder} node at path depth 8 (i.e. eight {@code /} characters).
     * Returns {@code null} if the root is reached without finding a matching folder.
     */
    private Node publicationFolder(Node node) throws RepositoryException {
        int depth = StringUtils.countMatches(node.getPath(), "/");
        if (depth == 0) {
            return null;
        }
        if (node.isNodeType("hippostd:folder") && depth == PUBLICATION_FOLDER_DEPTH) {
            return node;
        }
        return publicationFolder(node.getParent());
    }

    String collectionForUrl(String url) {
        if (url == null) {
            return null;
        }
        if (url.contains("/news/")) {
            return FunnelbackCollection.NEWS.getCollectionName();
        }
        if (url.contains("/policies/")) {
            return FunnelbackCollection.POLICY.getCollectionName();
        }
        if (url.contains("/publications/")) {
            return FunnelbackCollection.PUBLICATIONS.getCollectionName();
        }
        return null;
    }
}
