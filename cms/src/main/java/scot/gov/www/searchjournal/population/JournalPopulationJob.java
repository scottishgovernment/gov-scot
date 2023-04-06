package scot.gov.www.searchjournal.population;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publishing.searchjounal.FeatureFlag;
import scot.gov.publishing.searchjounal.FunnelbackCollection;
import scot.gov.publishing.searchjounal.SearchJournal;
import scot.gov.publishing.searchjounal.SearchJournalEntry;
import scot.gov.www.searchjournal.UrlSource;
import scot.gov.www.searchjournal.funnelback.Funnelback;
import scot.gov.www.searchjournal.funnelback.FunnelbackException;
import scot.gov.www.searchjournal.funnelback.FunnelbackFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

public class JournalPopulationJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(scot.gov.www.searchjournal.population.JournalPopulationJob.class);

    private static final String PAGES = "pages";

    private HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        Session session = context.createSystemSession();
        FeatureFlag featureFlag = new FeatureFlag(session, "PublicationsJournalPopulationJob");
        if (!featureFlag.isEnabled()) {
            LOG.info("PublicationsJournalPopulationJob disabled");
            session.logout();
            return;
        }

        LOG.info("PublicationsJournalPopulationJob enabled ...");
        try {
            resetJournalPosition(context);
            populate(session, context);
            deactivateJob(context);
        } catch (FunnelbackException e) {
            LOG.error("FunnelbackException", e);
        } catch (RepositoryException e) {
            LOG.error("RepositoryException", e);
        } finally {
            session.logout();
        }
    }

    void resetJournalPosition(RepositoryJobExecutionContext context) throws FunnelbackException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(0));
        Funnelback funnelback = FunnelbackFactory.newFunnelback(context);
        funnelback.storeJournalPosition(cal);
    }

    void deactivateJob(RepositoryJobExecutionContext context) throws RepositoryException {
        LOG.info("Deactivating JournalPopulationJob");
        Session session = context.createSystemSession();
        FeatureFlag featureFlag = new FeatureFlag(session, "PublicationsJournalPopulationJob");
        featureFlag.setEnabled(false);
        session.save();
    }

    void populate(Session session, RepositoryJobExecutionContext context) throws RepositoryException {
        for (String path : getPublicationMonthFolders(session)) {
            populatePublicationsFolder(path, context);
        }
    }

    List<String> getPublicationMonthFolders(Session session) throws RepositoryException {
        List<String> paths = new ArrayList<>();
        hippoUtils.executeXpathQuery(
                session,
                "/jcr:root/content/documents/govscot/publications/*/*/element(*, hippostd:folder)",
                n -> paths.add(n.getPath()));
        session.logout();
        LOG.info("found {} paths", paths.size());
        return paths;
    }

    void populatePublicationsFolder(String path, RepositoryJobExecutionContext context) throws RepositoryException {
        Session session = context.createSystemSession();
        SearchJournal journal = new SearchJournal(session, 250);
        LOG.info("populatePublicationsFolder {}", path);
        try {
            Node monthfolder = session.getNode(path);
            hippoUtils.apply(monthfolder.getNodes(), pubfolder -> populatePublication(pubfolder, journal));
        } catch (RepositoryException e) {
            LOG.error("RepositoryException for {}", path, e);
        } finally {
            journal.getSession().save();
            session.logout();
        }
    }

    void populatePublication(Node folder, SearchJournal searchJournal) throws RepositoryException {
        Node publicationHandle = getPublicationHandle(folder);
        if (publicationHandle == null) {
            return;
        }

        Node publishedVariant = publishedVariant(publicationHandle);
        if (publishedVariant == null) {
            return;
        }
        processPublicationOrComplexDocument(publishedVariant, searchJournal);
    }

    Node publishedVariant(Node handle) throws RepositoryException {
        return hippoUtils.find(handle.getNodes(handle.getName()),
                v -> hippoUtils.contains(v, "hippo:availability", "live"));
    }

    Node getPublicationHandle(Node folder) throws RepositoryException {
        return hippoUtils.find(folder.getNodes(), this::hasPublicationChild);
    }

    boolean hasPublicationChild(Node handle) throws RepositoryException {
        return hippoUtils.find(handle.getNodes(), this::isPublication) != null;
    }

    boolean isPublication(Node variant) throws RepositoryException {
         return variant.isNodeType("govscot:Publication");
    }

    void processPublicationOrComplexDocument(Node publication, SearchJournal journal) throws RepositoryException {
        String publicationType = publication.getProperty("govscot:publicationType").getString();
        String collection = FunnelbackCollection.getCollectionByPublicationType(publicationType).getCollectionName();
        Calendar timestamp = getTimestamp(publication);
        String slug = publication.getProperty("govscot:slug").getString();
        String publicationUrl = publicationUrl(slug);
        Calendar now = Calendar.getInstance();
        timestamp.set(Calendar.SECOND, now.get(Calendar.SECOND));
        timestamp.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));
        journal.record(publishEntry(publicationUrl, collection, timestamp));
        Node folder = publication.getParent().getParent();

        if (publication.isNodeType("govscot:Publication")) {
            boolean addedPages = processPublicationPages(publication, slug, collection, timestamp, journal);
            if (addedPages && hasDocuments(folder)) {
                String url = publicationUrl + "documents/";
                journal.record(publishEntry(url, collection, timestamp));
            }
        } else {
            processComplexDocumentChapters(publication, slug, collection, timestamp, journal);
        }
    }

    SearchJournalEntry publishEntry(String url, String collection, Calendar timestamp) {
        SearchJournalEntry entry = new SearchJournalEntry();
        entry.setUrl(url);
        entry.setAction("publish");
        entry.setCollection(collection);
        entry.setTimestamp(timestamp);
        return entry;
    }

    boolean hasDocuments(Node folder) throws RepositoryException {
        return folder.hasNode("documents") && folder.getNode("documents").getNodes().hasNext();
    }

    boolean processPublicationPages(Node publication, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {

        if (!publication.getParent().getParent().hasNode(PAGES)) {
            return false;
        }

        boolean addedPages = false;
        boolean seenFirstPage = false;
        Node pagesFolder = publication.getParent().getParent().getNode(PAGES);
        NodeIterator it = pagesFolder.getNodes();
        while (it.hasNext()) {
            Node pageHandle = it.nextNode();
            if (publishedNonContentPage(pageHandle)) {
                if (!seenFirstPage) {
                    seenFirstPage = true;
                } else {
                    String url = pageUrl(slug, pageHandle);
                    journal.record(publishEntry(url, collection, timestamp));
                    addedPages = true;
                }
            }
        }
        return addedPages;
    }
    boolean publishedNonContentPage(Node handle) throws RepositoryException {
        Node publishedVariant = publishedVariant(handle);
        return publishedVariant != null && !isContentsPage(handle);
    }

    boolean isContentsPage(Node handle) throws RepositoryException {
        Node variant = new HippoUtils().getVariant(handle);
        return variant.hasProperty("govscot:contentsPage")
                && variant.getProperty("govscot:contentsPage").getBoolean();
    }

    void processComplexDocumentChapters(Node publication, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {
        Node pagesFolder = publication.getParent().getParent().getNode("chapters");
        NodeIterator it = pagesFolder.getNodes();
        while (it.hasNext()) {
            Node folder = it.nextNode();
            processComplexDocumentChapterFolder(folder, slug, collection, timestamp, journal);
        }
    }

    void processComplexDocumentChapterFolder(Node folder, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {
        hippoUtils.apply(folder.getNodes(),
                chapterHandle -> indexChapter(chapterHandle, slug, collection, timestamp, journal));
    }

    void indexChapter(Node chapterHandle, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {
        String url = chapterUrl(slug, chapterHandle);
        journal.record(publishEntry(url, collection, timestamp));
    }
    String chapterUrl(String slug, Node handle) throws RepositoryException {
        String pubUrl = publicationUrl(slug);
        String handlePath = handle.getPath();
        return pubUrl + "chapters/" + StringUtils.substringAfter(handlePath, "chapters");
    }

    Calendar getTimestamp(Node node) throws RepositoryException {
        if (node.hasProperty("govscot:displayDate")) {
            return node.getProperty("govscot:displayDate").getDate();
        }
        return node.getProperty("hippostdpubwf:lastModificationDate").getDate();
    }

    String publicationUrl(String slug) {
        return UrlSource.URL_BASE + "publications/" + slug + "/";
    }

    String pageUrl(String slug, Node page) throws RepositoryException {
        String publicationUrl = publicationUrl(slug);
        return publicationUrl + "pages/" + page.getName() + "/";
    }

}