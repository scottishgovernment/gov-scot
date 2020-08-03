package scot.gov.publications.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoNodeFactory;
import scot.gov.publications.hippo.HippoPaths;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Tracks publication job by storing information in the jcr repo.
 *
 * Lays them out by breaking the guid into quartiles to avoid a large node.
 */
public class PublicationRepositoryJcrImpl {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationRepositoryJcrImpl.class);

    private static final String ROOT = "/content/publicationjobs";

    Session session;

    HippoPaths paths;

    HippoUtils hippoUtils;

    HippoNodeFactory hippoNodeFactory;

    public PublicationRepositoryJcrImpl(Session session) {
        this.session = session;
        this.paths = new HippoPaths(session);
        this.hippoUtils = new HippoUtils();
        this.hippoNodeFactory = new HippoNodeFactory(session);
    }

    /**
     * Create a new publication.
     *
     * @param publication Publication details to create.
     * @throws PublicationRepositoryException if the create failed.
     */
    public void create(Publication publication) throws PublicationRepositoryException {
        try {
            Node folder = paths.ensurePath(ROOT, path(publication));
            Node node = folder.addNode(publication.getIsbn(), "nt:unstructured");
            copyValues(node, publication);
            session.save();
        } catch (RepositoryException e) {
            throw new PublicationRepositoryException("Failed to create publication", e);
        }
    }

    /**
     * Update a publication.
     *
     * @param publication Publication details to update.
     * @throws PublicationRepositoryException if the update the publication.
     */
    public void update(Publication publication) throws PublicationRepositoryException {
        try {
            Node node = findById(publication.getId());
            publication.setLastmodifieddate(Calendar.getInstance());
            copyValues(node, publication);
            session.save();
        } catch (RepositoryException e) {
            throw new PublicationRepositoryException("Failed to update publication", e);
        }
    }

    /**
     * Fetch a publication using its id.
     *
     * @param id id to retrieve
     * @return Publication with that id, null if none exists
     * @throws PublicationRepositoryException if the create failed.
     */
    public Publication get(String id) throws PublicationRepositoryException {
        try {
            Node node = findById(id);
            return nodeToPublication(node);
        } catch (RepositoryException e) {
            throw new PublicationRepositoryException("Failed to get publication", e);
        }
    }

    /**
     * Paged list of publications with an optional search.
     *
     * @param page the page number to fetch
     * @param size the size of the page
     * @param title title to match (partial case insensitive
     * @param isbn isbn to match (partial case insensitive
     * @param filename filename to match (partial case insensitive
     * @return Collection of matching publications
     * @throws PublicationRepositoryException if it fails to list publications
     */
    public ListResult list(int page, int size, String title, String isbn, String filename)
            throws PublicationRepositoryException {

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM nt:unstructured ");
            List<String> andterms = new ArrayList<>();
            andterms.add("jcr:path LIKE '/content/publicationjobs/%'");

            if (isNotBlank(title)) {
                andterms.add(String.format("govscot:title LIKE '%%%s%%'", title));
            }

            if (isNotBlank(isbn)) {
                andterms.add(String.format("govscot:isbn LIKE '%%%s%%'", isbn));
            }

            if (isNotBlank(filename)) {
                andterms.add(String.format("govscot:filename LIKE '%%%s%%'", filename));
            }

            if (!andterms.isEmpty()) {
                sql.append(" WHERE ");
                sql.append(andterms.stream().collect(joining(" AND ")));
            }

            sql.append(" ORDER by govscot:createddate DESC");
            return executeQuery(sql.toString(), page, size);
        } catch (RepositoryException e) {
            LOG.error("Failed to list publications {}", e);
            throw new PublicationRepositoryException("Failed to list publications", e);
        }
    }

    ListResult executeQuery(String sql, int page, int size) throws RepositoryException {
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
        int zeroBasedPage = page - 1;
        int offset = zeroBasedPage * size;
        queryObj.setOffset(offset);
        QueryResult queryResult = queryObj.execute();
        NodeIterator nodeIt = queryResult.getNodes();
        List<Publication> publications = new ArrayList<>();
        int i = 0;
        while (nodeIt.hasNext()) {
            Node node = nodeIt.nextNode();
            i++;
            if (i <= size) {
                publications.add(nodeToPublication(node));
            }
        }
        ListResult result = new ListResult();
        result.setPage(page);
        result.setPageSize(size);
        result.setPublications(publications);
        result.setTotalSize(offset + i);
        return result;
    }

    List<String> path(Publication publication) {
        String guid = publication.getId().replaceAll("-", "");
        return Arrays.asList(guid.split("(?<=\\G.{2})"));
    }

    void copyValues(Node node, Publication publication) throws RepositoryException  {
        node.setProperty("govscot:id", publication.getId());
        node.setProperty("govscot:title", publication.getTitle());
        node.setProperty("govscot:isbn", publication.getIsbn());
        node.setProperty("govscot:contact", publication.getContact());
        node.setProperty("govscot:filename", publication.getFilename());
        node.setProperty("govscot:state", publication.getState());
        node.setProperty("govscot:statedetails", publication.getStatedetails());
        node.setProperty("govscot:username", publication.getUsername());
        node.setProperty("govscot:createddate", publication.getCreateddate());
        node.setProperty("govscot:embargodate", publication.getEmbargodate());
        node.setProperty("hippostd:state", "published");
        hippoNodeFactory.addBasicFields(node, publication.getTitle());
    }

    Publication nodeToPublication(Node node) throws RepositoryException {
        Publication publication = new Publication();
        publication.setId(node.getProperty("govscot:id").getString());
        publication.setTitle(node.getProperty("govscot:title").getString());
        publication.setIsbn(node.getProperty("govscot:isbn").getString());
        publication.setContact(propWithDefault(node, "govscot:contact", ""));
        publication.setFilename(node.getProperty("govscot:filename").getString());
        publication.setState(node.getProperty("govscot:state").getString());
        publication.setStatedetails(propWithDefault(node, "govscot:statedetails", ""));
        publication.setUsername(propWithDefault(node, "govscot:username", ""));
        publication.setCreateddate(node.getProperty("govscot:createddate").getDate());
        publication.setEmbargodate(node.getProperty("govscot:embargodate").getDate());
        return publication;
    }

    String propWithDefault(Node node, String prop, String defaultVal) throws RepositoryException {
        return node.hasProperty(prop) ? node.getProperty(prop).getString() : defaultVal;
    }

    Node findById(String id) throws RepositoryException {
        String sql= String.format(
                    "SELECT * FROM nt:unstructured " +
                    "WHERE jcr:path LIKE '/content/publicationjobs/%%' " +
                    "AND govscot:id = '%s'", id);
        return hippoUtils.findOneQueryNoArgs(session, sql, Query.SQL);
    }

}
