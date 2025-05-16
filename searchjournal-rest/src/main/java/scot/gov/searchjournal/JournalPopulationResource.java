package scot.gov.searchjournal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publishing.searchjournal.FunnelbackCollection;
import scot.gov.publishing.searchjournal.SearchJournal;
import scot.gov.publishing.searchjournal.SearchJournalEntry;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static javax.jcr.query.Query.XPATH;
import org.apache.jackrabbit.util.ISO9075;

public class JournalPopulationResource {

    private static final Logger LOG = LoggerFactory.getLogger(JournalPopulationResource.class);

    private static final String URL_BASE = "https://www.gov.scot/";

    private static final String PAGES = "pages";

    Session session;

    SearchJournal journal;

    public JournalPopulationResource(Session session) {
        this.session = session;
        this.journal = new SearchJournal(session);
    }

    @PUT
    @Path("populate")
    @Produces({MediaType.APPLICATION_JSON})
    public Response populatePublication(@QueryParam("path") String path) {

        try {
            LOG.info("populatePublication path={}", path);
            List<String> paths = new ArrayList<>();
            runQuery(queryForType(path, "govscot:Publication"), paths);
            runQuery(queryForType(path, "govscot:ComplexDocument"), paths);
            return Response.status(Response.Status.OK).entity(paths).build();
        } catch (RepositoryException e) {
            LOG.error("Failed to populatePublication for {}", path, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed").build();
        }

        /*x
         *  make sure only cs admins can access this encpoint
         */
    }

    String queryForType(String path, String type) {

        List<String> pathelement = Arrays.asList(path.split("/"));
        String encodedPath = pathelement.stream().map(ISO9075::encode).collect(Collectors.joining("/"));
        return String.format("/jcr:root/content/documents/govscot/publications%s//element(*, %s)" +
                "[hippostd:state = 'published'][hippostd:stateSummary = 'live']",
                encodedPath, type);
    }

    void runQuery(String query, List<String> paths) throws RepositoryException {
        LOG.info("runQuery {}", query);
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(query, XPATH);
        queryObj.setLimit(10000);
        QueryResult result = queryObj.execute();
        LOG.info("{} publications", result.getNodes().getSize());
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            Node node = it.nextNode();
            processPublicationOrComplexDocument(node, paths);
        }
    }

    void processPublicationOrComplexDocument(Node publication, List<String> paths) throws RepositoryException {
        LOG.info("processPublicationOrComplexDocument {}", publication.getPath());
        String publicationType = publication.getProperty("govscot:publicationType").getString();
        String collection = FunnelbackCollection.getCollectionByPublicationType(publicationType).getCollectionName();
        Calendar timestamp = getTimestamp(publication);
        String slug = publication.getProperty("govscot:slug").getString();
        String publicationUrl = publicationUrl(slug);
        Calendar now = Calendar.getInstance();
        timestamp.set(Calendar.SECOND, now.get(Calendar.SECOND));
        timestamp.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));
        journal.record(publishEntry(publicationUrl, collection, timestamp));
        paths.add(publicationUrl);
        Node folder = publication.getParent().getParent();

        if (publication.isNodeType("govscot:Publication")) {
            boolean addedPages = processPublicationPages(publication, slug, collection, timestamp, paths);
            if (addedPages && hasDocuments(folder)) {
                String url = publicationUrl + "documents/";
                journal.record(publishEntry(url, collection, timestamp));
                paths.add(url);
            }
        } else {
            processComplexDocumentChapters(publication, slug, collection, timestamp, paths);
        }
    }

    boolean hasDocuments(Node folder) throws RepositoryException {
        return folder.hasNode("documents") && folder.getNode("documents").getNodes().hasNext();
    }

    Calendar getTimestamp(Node node) throws RepositoryException {
        if (node.hasProperty("govscot:displayDate")) {
            return node.getProperty("govscot:displayDate").getDate();
        }
        return node.getProperty("hippostdpubwf:lastModificationDate").getDate();
    }

    String publicationUrl(String slug) {
        return URL_BASE + "publications/" + slug + "/";
    }

    String pageUrl(String slug, Node page) throws RepositoryException {
        String publicationUrl = publicationUrl(slug);
        return publicationUrl + "pages/" + page.getName() + "/";
    }

    SearchJournalEntry publishEntry(String url, String collection, Calendar timestamp) {
        SearchJournalEntry entry = new SearchJournalEntry();
        entry.setUrl(url);
        entry.setAction("publish");
        entry.setCollection(collection);
        entry.setTimestamp(timestamp);
        return entry;
    }

    boolean processPublicationPages(Node publication, String slug, String collection, Calendar timestamp, List<String> paths) throws RepositoryException {

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
                    paths.add(url);
                    addedPages = true;
                }
            }
        }
        return addedPages;
    }

    boolean publishedNonContentPage(Node handle) throws RepositoryException {
        return isPublished(handle) && !isContentsPage(handle);
    }

    boolean isPublished(Node handle) throws RepositoryException {
        NodeIterator it = handle.getNodes();
        while (it.hasNext()) {
            Node variant = it.nextNode();
            String state = variant.getProperty("hippostd:state").getString();
            if ("published".equals(state)) {
                return true;
            }
        }
        return false;
    }

    boolean isContentsPage(Node handle) throws RepositoryException {
        Node variant = new HippoUtils().getVariant(handle);
        return variant.hasProperty("govscot:contentsPage")
                && variant.getProperty("govscot:contentsPage").getBoolean();
    }

    void processComplexDocumentChapters(Node publication, String slug, String collection, Calendar timestamp, List<String> paths) throws RepositoryException {

        Node pagesFolder = publication.getParent().getParent().getNode("chapters");
        NodeIterator it = pagesFolder.getNodes();
        while (it.hasNext()) {
            Node folder = it.nextNode();
            processComplexDocumentChapterFolder(folder, slug, collection, timestamp, paths);
        }
    }

    void processComplexDocumentChapterFolder(Node folder, String slug, String collection, Calendar timestamp, List<String> paths) throws RepositoryException {
        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node chapterHandle = it.nextNode();
            String url = chapterUrl(slug, chapterHandle);
            journal.record(publishEntry(url, collection, timestamp));
            paths.add(url);
        }
    }

    String chapterUrl(String slug, Node handle) throws RepositoryException {
        String pubUrl = publicationUrl(slug);
        String handlePath = handle.getPath();
        return pubUrl + "chapters/" + StringUtils.substringAfter(handlePath, "chapters");
    }
}
