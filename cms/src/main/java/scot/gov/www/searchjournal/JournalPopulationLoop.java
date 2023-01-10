package scot.gov.www.searchjournal;

import org.apache.commons.lang.StringUtils;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.FeatureFlag;
import scot.gov.www.searchjournal.funnelback.FunnelbackCollection;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static javax.jcr.query.Query.XPATH;
import static scot.gov.www.searchjournal.FunnelbackReconciliationLoop.isReady;
import static scot.gov.www.searchjournal.UrlSource.URL_BASE;

/**
 * Scafolding for the launch - populate the searc h journal with all news and publicaitons so that they can be indexed.
 * This is done in this way to avoif having to have a long running migtration which would make a release take too long.
 */
public class JournalPopulationLoop implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(JournalPopulationLoop.class);

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        if (!isReady()) {
            return;
        }

        Session session = context.createSystemSession();
        FeatureFlag featureFlag = new FeatureFlag(session, "JournalPopulationLoop");
        if (!featureFlag.isEnabled()) {
            LOG.info("JournalPopulationLoop is disabled");
            session.logout();
            return;
        }

        LOG.info("Starting JournalPopulationLoop");
        // get the most recent journal entry, default to 1970 if none present

        SearchJournal journal = new SearchJournal(session);
        Calendar from = from(journal);
        LOG.info("populating from {}", dateFormat.format(from.getTime()));

        // get the next 500 publications and 500 news items and create journal entries for them
        try {
            addJournalEntriesForPublicaitons(journal, from);
            addJournalEntriesForNews(journal, from);
        } catch (RepositoryException e) {
            LOG.error("argh", e);
            throw e;
        }
    }

    Calendar from(SearchJournal journal) throws RepositoryException {
        SearchJournalEntry entry = journal.mostRecentEntry();
        if (entry == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            return cal;
        }
        return entry.getTimestamp();
    }

    void addJournalEntriesForPublicaitons(SearchJournal journal, Calendar from) throws RepositoryException {

        String query = pubsQuery(from);
        LOG.info("addJournalEntriesForPublicaitons query: {}", query);
        Query queryObj = journal.getSession().getWorkspace().getQueryManager().createQuery(query, XPATH);
        queryObj.setLimit(1000);
        QueryResult result = queryObj.execute();
        LOG.info("addJournalEntriesForPublicaitons {}", result.getNodes().getSize());
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            Node node = it.nextNode();
            LOG.info("addJournalEntriesForPublicaitons {}", node.getPath());
            addJournalEntriesForPublication(journal, node);
        }
    }

    String pubsQuery(Calendar from) {
        // 2022-12-20T00:00:00.000Z
        return String.format("//element(*, govscot:Publication)[" +
                "(@hippo:availability='live') and " +
                "not(@jcr:primaryType='nt:frozenNode') and " +
                "(@govscot:displayDate____day >= xs:dateTime('%s'))] " +
                "order by @govscot:displayDate ascending,@govscot:title ascending",
                dateFormat.format(from.getTime())
                );
    }

    void addJournalEntriesForNews(SearchJournal journal, Calendar from) throws RepositoryException {

        String query = newsQuery(from);
        Query queryObj = journal.getSession().getWorkspace().getQueryManager().createQuery(query, XPATH);
        queryObj.setLimit(1000);
        QueryResult result = queryObj.execute();
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            Node node = it.nextNode();
            Calendar timestamp = getTimestamp(node);
            String slug = node.getProperty("govscot:slug").getString();
            String url = newsUrl(slug);
            journal.record(SearchJournalEntry.publishEntry(url, FunnelbackCollection.NEWS.getCollectionName(), timestamp));
        }
    }

    String newsQuery(Calendar from) {
        return String.format("//element(*, govscot:News)[" +
                        "(@hippo:availability='live') and " +
                        "not(@jcr:primaryType='nt:frozenNode') and " +
                        "(@govscot:displayDate____day >= xs:dateTime('%s'))] " +
                        "order by @govscot:displayDate ascending,@govscot:title ascending",
                dateFormat.format(from.getTime())
        );
    }

    void addJournalEntriesForPublication(SearchJournal journal, Node publication) throws RepositoryException {

        String pushCollection = pushCollection(publication);
        // change this to prefer the publication date ... otherwise we get tons with the same date , presumably when migraiton happened?
        Calendar timestamp = getTimestamp(publication );
        String slug = publication.getProperty("govscot:slug").getString();
        String publicationUrl = publicationUrl(slug);
        Calendar now = Calendar.getInstance();
        timestamp.set(Calendar.SECOND, now.get(Calendar.SECOND));
        timestamp.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));

        journal.record(SearchJournalEntry.publishEntry(publicationUrl, pushCollection, timestamp));

        Node folder = publication.getParent().getParent();

        // do not index this if it is not present.
        // This will be if:
        //  - there is no documents folder
        //  - there is an empty documents folder
        //  - thete are no pages and so the documents are listed inline
        boolean hasPages = folder.hasNode("pages") && folder.getNode("pages").getNodes().hasNext();
        if (folder.hasNode("documents") && folder.getNode("documents").getNodes().hasNext() && hasPages) {
            journal.record(SearchJournalEntry.publishEntry(documentsPath(slug), pushCollection, timestamp));
        }

        if (hasPages) {
            Node pages = folder.getNode("pages");
            recordEntriesForNodes(journal, pages.getNodes(), publicationUrl, this::pagePath, pushCollection, timestamp);
        }

//        if (folder.hasNode("chapters")) {
//            Node chapters = folder.getNode("chapters");
//            recordEntriesForNodes(journal, chapters.getNodes(), publicationUrl, this::complexDocPagePath, pushCollection, timestamp);
//        }
    }

    void recordEntriesForNodes(
            SearchJournal journal,
            NodeIterator it,
            String publicationUrl,
            UrlProvider urlProvider,
            String collection,
            Calendar timestamp) throws RepositoryException {
        while (it.hasNext()) {
            Node pageHandle = it.nextNode();
            Node published = findPublished(pageHandle);
            if (published != null && !isContentPage(published)) {
                String url = urlProvider.url(publicationUrl, published);
                SearchJournalEntry entry = SearchJournalEntry.publishEntry(url, collection, timestamp);
                journal.record(entry);
            }
        }
    }
    boolean isContentPage(Node page) throws RepositoryException {
        return page.hasProperty("govscot:contentsPage") && page.getProperty("govscot:contentsPage").getBoolean();
    }

    interface UrlProvider {
        String url(String publicationUrl, Node node) throws RepositoryException;
    }


    Calendar getTimestamp(Node node) throws RepositoryException {
        if (node.hasProperty("govscot:displayDate")) {
            return node.getProperty("govscot:displayDate").getDate();
        }
        return node.getProperty("hippostdpubwf:lastModificationDate").getDate();
    }

    String pushCollection(Node publicaiton) throws RepositoryException {

        String publicationType = publicaiton.getProperty("govscot:publicationType").getString();
        switch (publicationType) {
            case "foi-eir-release" :
                return "govscot~ds-foi-eir-releases-push";
            case "statistics" :
            case "research-and-analysis" :
                return "govscot~ds-statistics-research-push";
            default:
                return "govscot~ds-publications-push";
        }
    }

    Node findPublished(Node handle) throws RepositoryException {
        Node published = findPublished(handle.getNodes(handle.getName()));

        if (published == null) {
            return null;
        }

        if (!published.hasProperty("hippo:availability")) {
            LOG.info("No hippo:availability property for {}", published.getPath());
            return null;
        }

        Value[] availValues = published.getProperty("hippo:availability").getValues();
        for (Value value : availValues) {
            if (value.getString().equals("live")) {
                return published;
            }
        }

        return null;
    }

    public Node findPublished(NodeIterator nodeIt) throws RepositoryException {
        while (nodeIt.hasNext()) {
            Node node = nodeIt.nextNode();
            if ("published".equals(node.getProperty("hippostd:state").getString())) {
                LOG.debug("found published: {}", node.getPath());
                return node;
            }
        }
        return null;
    }

    String newsUrl(String slug) {
        return URL_BASE + "news/" + slug + "/";
    }

    String publicationUrl(String slug) {
        return URL_BASE + "publications/" + slug + "/";
    }

    String documentsPath(String slug) {
        return publicationUrl(slug) + "documents/";
    }

    String pagePath(String pubUrl, Node page) throws RepositoryException {
        return pubUrl + "pages/" + page.getName() + "/";
    }

    String complexDocPagePath(String pubUrl, Node handle) throws RepositoryException {
        return pubUrl + StringUtils.substringAfter(handle.getPath(), "/chapters/");
    }
}