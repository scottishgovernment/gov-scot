package scot.gov.www.searchjournal;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publishing.searchjounal.FeatureFlag;
import scot.gov.publishing.searchjounal.FunnelbackCollection;
import scot.gov.publishing.searchjounal.SearchJournal;
import scot.gov.publishing.searchjounal.SearchJournalEntry;
import scot.gov.www.searchjournal.funnelback.Funnelback;
import scot.gov.www.searchjournal.funnelback.FunnelbackException;
import scot.gov.www.searchjournal.funnelback.FunnelbackFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.substringAfter;

public class JournalPopulationJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(JournalPopulationJob.class);

    private static final String PAGES = "pages";

    private HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        Session session = context.createSystemSession();
        try {
            FeatureFlag featureFlag = new FeatureFlag(session, "JournalPopulationJob");
            if (featureFlag.isEnabled()) {
                LOG.info("JournalPopulationJob running");
                populate(session, context);
                resetJournalPosition(context);
                deactivateJob(context);
                activateReconciliationJob(context);
                LOG.info("JournalPopulationJob finished");
            }
        } catch (FunnelbackException e) {
            LOG.error("FunnelbackException in JournalPopulationJob", e);
        } catch (RepositoryException e) {
            LOG.error("RepositoryException in JournalPopulationJob", e);
        } finally {
            session.logout();
        }
    }

    void populate(Session session, RepositoryJobExecutionContext context) throws RepositoryException {
        for (String path : getPublicationMonthFolders(session)) {
            populatePublicationsFolder(path, context);
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
        FeatureFlag featureFlag = new FeatureFlag(session, "JournalPopulationJob");
        featureFlag.setEnabled(false);
        session.save();
    }

    void activateReconciliationJob(RepositoryJobExecutionContext context) throws RepositoryException {
        LOG.info("Activating FunnelbackReconciliationLoop");
        Session session = context.createSystemSession();
        FeatureFlag featureFlag = new FeatureFlag(session, "FunnelbackReconciliationLoop");
        featureFlag.setEnabled(true);
        session.save();
    }

    List<String> getPublicationMonthFolders(Session session) throws RepositoryException {
        List<String> paths = new ArrayList<>();
        hippoUtils.executeXpathQuery(
                session,
                "/jcr:root/content/documents/govscot/publications/*/*/element(*, hippostd:folder)",
                n -> paths.add(n.getPath()));
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

        boolean hasPages = false;
        if (publication.isNodeType("govscot:ComplexDocument2")) {
            processComplexDocumentChapters(publication, slug, collection, timestamp, journal);
            hasPages = true;
        } else {
            hasPages = processPublicationPages(publication, slug, collection, timestamp, journal);
        }
        if (hasPages && hasDocuments(folder)) {
            String url = publicationUrl + "documents/";
            journal.record(publishEntry(url, collection, timestamp));
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
        if (!folder.hasNode("documents")) {
            return false;
        }

        // account for the fact that the documents folder can hav nested folders
        Node documentsFolder = folder.getNode("documents");
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

    boolean processPublicationPages(Node publication, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {

        if (!publication.getParent().getParent().hasNode(PAGES)) {
            return false;
        }

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
        return variant.hasProperty("govscot:contentsPage")
                && variant.getProperty("govscot:contentsPage").getBoolean();
    }

    void processComplexDocumentChapters(Node publication, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {
        Node pagesFolder = publication.getParent().getParent().getNode("chapters");
        hippoUtils.apply(pagesFolder.getNodes(),
                folder -> hippoUtils.apply(folder.getNodes(),
                    chapterHandle -> indexChapter(chapterHandle, slug, collection, timestamp, journal)));
    }

    void indexChapter(Node chapterHandle, String slug, String collection, Calendar timestamp, SearchJournal journal) throws RepositoryException {
        if (publishedVariant(chapterHandle) == null) {
            return;
        }
        String url = chapterUrl(slug, chapterHandle);
        journal.record(publishEntry(url, collection, timestamp));
    }

    String chapterUrl(String slug, Node handle) throws RepositoryException {
        String pubUrl = publicationUrl(slug);
        String handlePath = handle.getPath();
        return pubUrl + substringAfter(handlePath, "chapters/") + "/";
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