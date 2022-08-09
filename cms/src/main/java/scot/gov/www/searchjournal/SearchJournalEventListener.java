package scot.gov.www.searchjournal;

import javax.jcr.Node;
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
import scot.gov.www.FeatureFlag;
import scot.gov.www.searchjournal.funnelback.FunnelbackCollection;

import java.util.Calendar;

import static org.apache.commons.lang3.StringUtils.startsWithAny;

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

        if (!shouldHandleEvent(event)) {
            return;
        }

        try {
            SearchJournalEntry entry = journalEntry(event);
            if (entry != null) {
                searchJournal.record(entry);
            }
        } catch (RepositoryException e) {
            LOG.error("RepositoryException trying to index {}", event.subjectId(), e);
        }
    }

    /**
     * we are only interested in successful publish and depublish events for news and publications
     */
    boolean shouldHandleEvent(HippoWorkflowEvent event) {
        if (!event.success()) {
            return false;
        }

        if (!startsWithAny(event.subjectPath(), PUBLICATIONS_PATH_PREFIX, NEWS_PATH_PREFIX)) {
            return false;
        }

        return StringUtils.equalsAny(event.interaction(), PUBLISH_INTERACTION, DEPUBLISH_INTERACTION);
    }

    SearchJournalEntry journalEntry(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNodeByIdentifier(event.subjectId());
        Node variant = hippoUtils.getVariant(handle);
        SearchJournalEntry journalEntry = new SearchJournalEntry();
        journalEntry.setAttempt(0);
        journalEntry.setAction(event.action());
        journalEntry.setTimestamp(Calendar.getInstance());
        if (variant.isNodeType("govscot:News")) {
            journalEntry.setUrl(urlSource.newsUrl(variant));
            journalEntry.setCollection(FunnelbackCollection.NEWS.getCollectionName());
        } else {
            Node publication = getPublication(variant);
            String publicationType = publication.getProperty("govscot:publicationType").getString();
            FunnelbackCollection collection = getCollectionByPublicationType(publicationType);
            journalEntry.setCollection(collection.getCollectionName());
            journalEntry.setUrl(urlSource.publicationUrl(publication, variant, event));
        }
        return journalEntry;
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
     * Go through through the parents to find the publication folder.
     */
    Node publicationFolder(Node node) throws RepositoryException {
        // folder tpe is empty for publicaiton folders.  Or better to depend on depth?
        int depth = StringUtils.countMatches(node.getPath(), "/");
        if (node.isNodeType("hippostd:folder") && depth == PUBLICATION_FOLDER_DEPTH) {
            return node;
        }
        return publicationFolder(node.getParent());
    }

    FunnelbackCollection getCollectionByPublicationType(String publicationType) {

        switch (publicationType) {
            case "foi-eir-release":
                return FunnelbackCollection.FOI;
            case "statistics":
            case "research-and-analysis":
                return FunnelbackCollection.STATS_AND_RESEARCH;
            default:
                return FunnelbackCollection.PUBLICATIONS;
        }
    }

}