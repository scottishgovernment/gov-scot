package scot.gov.www.searchjournal;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publishing.searchjournal.FeatureFlag;
import scot.gov.publishing.searchjournal.SearchJournal;
import scot.gov.publishing.searchjournal.SearchJournalEntry;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;
import static scot.gov.publishing.searchjournal.FunnelbackCollection.NEWS;
import static scot.gov.publishing.searchjournal.FunnelbackCollection.POLICY;
import static scot.gov.publishing.searchjournal.FunnelbackCollection.getCollectionByPublicationType;

/**
 * Listen to publish and unpublish events in order to maintain the search journal.
 *
 * This is only done for certain collections for news and publicaitons since these are time sensitive.
 */
public class SearchJournalEventListener implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(SearchJournalEventListener.class);

    private static final String PUBLISH_INTERACTION = "default:handle:publish";

    private static final String DEPUBLISH_INTERACTION = "default:handle:depublish";

    private static final String PUBLICATIONS_PATH_PREFIX = "/content/documents/govscot/publications";

    private static final String NEWS_PATH_PREFIX = "/content/documents/govscot/news";

    private static final String POLICY_PATH_PREFIX = "/content/documents/govscot/policies";

    private static final String PAGES = "pages";

    private static final int PUBLICATION_FOLDER_DEPTH = 8;

    private HippoUtils hippoUtils = new HippoUtils();

    private Session session;

    private FeatureFlag featureFlag;

    private SearchJournal searchJournal;

    private UrlSource urlSource = new UrlSource();

    @Override
    public void initialize(Session session) throws RepositoryException {
        this.session = session;
        featureFlag = new FeatureFlag(session, "SearchJournalEventListener");
        searchJournal = new SearchJournal(session);
        HippoEventListenerRegistry.get().register(this);
    }

    @Override
    public void shutdown() {
        HippoEventListenerRegistry.get().unregister(this);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {

        if (!featureFlag.isEnabled()) {
            return;
        }

        try {
            if (!shouldHandleEvent(event)) {
                return;
            }

            List<SearchJournalEntry> entries = journalEntries(event);
            long sequence = 1;
            for (SearchJournalEntry entry : entries) {
                entry.setSequence(sequence++);
                searchJournal.record(entry);
            }
            session.save();
        } catch (RepositoryException e) {
            LOG.error("RepositoryException trying to index {}", event.subjectId(), e);
        }
    }


    /**
     * we are only interested in successful publish and depublish events for news, policy and publications
     */
    boolean shouldHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (!event.success()) {
            return false;
        }

        if (!startsWithAny(event.subjectPath(), PUBLICATIONS_PATH_PREFIX, NEWS_PATH_PREFIX, POLICY_PATH_PREFIX)) {
            return false;
        }

        // check if it is an excluded page (eg policy latest, publication contents
        if (isExcludedPage(event)) {
            return false;
        }

        // it is a publish or a depublish with no arguments.  If the even has arguments it means it is scheduled
        // when the page is actually published we will get a no arguments event.
        return equalsAny(event.interaction(), PUBLISH_INTERACTION, DEPUBLISH_INTERACTION) &&
                event.arguments() == null;
    }

    boolean isExcludedPage(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNodeByIdentifier(event.subjectId());
        Node variant = hippoUtils.getVariant(handle);
        if (variant == null) {
            return false;
        }
        return isPublicaitonContentsPage(variant) || isPolicyLatestPage(variant);
    }

    boolean isPolicyLatestPage(Node variant) throws RepositoryException {
        return variant.isNodeType("govscot:PolicyLatest");
    }

    Node getVariant(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNodeByIdentifier(event.subjectId());
        return hippoUtils.getVariant(handle);
    }

    List<SearchJournalEntry> journalEntries(HippoWorkflowEvent event) throws RepositoryException {
        Node variant = getVariant(event);

        if (variant.isNodeType("govscot:News")) {
            SearchJournalEntry entry = entry(event);
            entry.setUrl(urlSource.newsUrl(variant));
            entry.setCollection(NEWS.getCollectionName());
            return Collections.singletonList(entry);
        }

        if (isAnyNodeType(variant, "govscot:Policy", "govscot:PolicyInDetail")) {
            SearchJournalEntry entry = entry(event);
            entry.setUrl(urlSource.policyUrl(variant));
            entry.setCollection(POLICY.getCollectionName());
            return Collections.singletonList(entry);
        }

        return journalEntriesForPublication(variant, event);
    }

    List<SearchJournalEntry> journalEntriesForPublication(Node variant, HippoWorkflowEvent event) throws RepositoryException {
        Node publication = getPublication(variant);
        String publicationType = publication.getProperty("govscot:publicationType").getString();
        String collection = getCollectionByPublicationType(publicationType).getCollectionName();
        SearchJournalEntry entry = entry(event);
        entry.setUrl(urlSource.publicationUrl(publication, variant, event));
        entry.setCollection(collection);
        Node publicationFolder = publicationFolder(publication);
        boolean hasPages = hasPages(publication, publicationFolder);
        boolean hasDocuments = hasDocuments(publicationFolder);
        boolean needsDocumentsPage = hasPages && hasDocuments;

        if (isAnyNodeType(variant, "govscot:ComplexDocumentSection")) {
            return Arrays.asList(entry);
        }

        if (isAnyNodeType(variant, "govscot:PublicationPage")) {
            SearchJournalEntry documentsEntry = documentsEntry(event, collection, needsDocumentsPage, publication);
            SearchJournalEntry publicationEntry = publicationEntry(event, collection, publication);
            if (entry.getUrl().equals(publicationEntry.getUrl())) {
                entry.setAction(publicationEntry.getAction());
            }
            return Arrays.asList(entry, documentsEntry, publicationEntry);
        }

        if (isAnyNodeType(variant, "govscot:DocumentInformation")) {
            // update the documents page and the publication itself
            SearchJournalEntry documentsEntry = documentsEntry(event, collection, needsDocumentsPage, publication);
            SearchJournalEntry publicationEntry = publicationEntry(event, collection, publication);
            return Arrays.asList(documentsEntry, publicationEntry);
        }

        // just update the publication
        return Collections.singletonList(entry);
    }

    SearchJournalEntry documentsEntry(HippoWorkflowEvent event, String collection, boolean needsDocumentsPage, Node publication) throws RepositoryException {
        SearchJournalEntry entry = entry(event);
        entry.setAction(needsDocumentsPage ? "publish" : "depublish");
        entry.setCollection(collection);
        entry.setUrl(urlSource.documentsUrl(publication));
        return entry;
    }

    SearchJournalEntry publicationEntry(HippoWorkflowEvent event, String collection, Node publication) throws RepositoryException {
        SearchJournalEntry entry = entry(event);
        setActionDependingOnState(publication, entry);
        entry.setCollection(collection);
        entry.setUrl(urlSource.publicationUrl(publication));
        return entry;
    }

    void setActionDependingOnState(Node node, SearchJournalEntry entry) throws RepositoryException {
        Node publishedPublicationVariant = publishedVariant(node.getParent());
        String action = publishedPublicationVariant != null ? "publish" : "depublish";
        entry.setAction(action);
    }

    boolean hasPages(Node publication, Node publicationFolder) throws RepositoryException {
        if (publication.isNodeType("govscot:ComplexDocument2")) {
            return true;
        }

        if (!publication.getParent().getParent().hasNode(PAGES)) {
            return false;
        }

        boolean seenFirstPage = false;
        Node pagesFolder = publicationFolder.getNode(PAGES);
        NodeIterator it = pagesFolder.getNodes();
        while (it.hasNext()) {
            Node pageHandle = it.nextNode();
            if (publishedNonContentPage(pageHandle)) {
                if (!seenFirstPage) {
                    seenFirstPage = true;
                } else {
                    return true;
                }
            }
        }
        return seenFirstPage;
    }

    boolean publishedNonContentPage(Node handle) throws RepositoryException {
        Node publishedVariant = publishedVariant(handle);
        return publishedVariant != null && !isContentsPage(handle);
    }

    boolean isContentsPage(Node handle) throws RepositoryException {
        Node variant = new HippoUtils().getVariant(handle);
        return isPublicaitonContentsPage(variant);
    }

    boolean isPublicaitonContentsPage(Node variant) throws RepositoryException {
        return variant.hasProperty("govscot:contentsPage")
                && variant.getProperty("govscot:contentsPage").getBoolean();
    }
    boolean hasDocuments(Node publicationFolder) throws RepositoryException {
        if (!publicationFolder.hasNode("documents")) {
            return false;
        }

        // account for the fact that the documents folder can hav nested folders
        Node documentsFolder = publicationFolder.getNode("documents");
        return null != hippoUtils.find(documentsFolder.getNodes(),
                node -> isHandleWithPublishedVariant(node) || isFolderWithPublishedDocs(node));
    }

    boolean isFolderWithPublishedDocs(Node node) throws RepositoryException {
        if (!node.isNodeType("hippostd:folder")) {
            return false;
        }
        return null != hippoUtils.find(node.getNodes(), handle -> isHandleWithPublishedVariant(handle));
    }

    boolean isHandleWithPublishedVariant(Node node) throws RepositoryException {
        if (!node.isNodeType("hippo:handle")) {
            return false;
        }
        return publishedVariant(node) != null;
    }

    Node publishedVariant(Node handle) throws RepositoryException {
        return hippoUtils.find(handle.getNodes(handle.getName()),
                v -> hippoUtils.contains(v, "hippo:availability", "live"));
    }

    String getEventAction(HippoWorkflowEvent event) {
        if ("moveFolder".equals(event.action())) {
            return "publish";
        }

        return event.action();
    }

    SearchJournalEntry entry(HippoWorkflowEvent event) {
        SearchJournalEntry journalEntry = new SearchJournalEntry();
        journalEntry.setAttempt(0);
        journalEntry.setAction(getEventAction(event));
        journalEntry.setTimestamp(Calendar.getInstance());
        return journalEntry;
    }

    boolean isAnyNodeType(Node variant, String ... types) throws RepositoryException {
        for (String type : types) {
            if (variant.isNodeType(type)) {
                return true;
            }
        }
        return false;
    }

    Node getPublication(Node node) throws RepositoryException {
        if (node.isNodeType("govscot:Publication")) {
            return node;
        }

        Node publicationFolder = publicationFolder(node);
        Node handle = publicationFolder.getNode("index");
        return hippoUtils.getVariant(handle);
    }

    /**
     * find the publication folder.
     */
    Node publicationFolder(Node node) throws RepositoryException {
        int depth = StringUtils.countMatches(node.getPath(), "/");
        if (node.isNodeType("hippostd:folder") && depth == PUBLICATION_FOLDER_DEPTH) {
            return node;
        }
        return publicationFolder(node.getParent());
    }

}