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
import java.sql.Timestamp;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Tracks publication job by storing information in the jcr repo.
 *
 * Lays them out by breaking the guid into quartiles to avoid a large node.
 */
public class PublicationRepositoryJcrImpl implements PublicationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationRepositoryJcrImpl.class);

    private static final String ROOT = "/content/publicationjobs";

    /**
     * Allowlist of the fields a caller may sort by, mapped to their JCR property name. Sort input
     * is never concatenated into the query directly to avoid JCR-SQL injection via the sort param.
     */
    private static final Map<String, String> SORTABLE_FIELDS = new LinkedHashMap<>();
    static {
        SORTABLE_FIELDS.put("title", "govscot:title");
        SORTABLE_FIELDS.put("isbn", "govscot:isbn");
        SORTABLE_FIELDS.put("filename", "govscot:filename");
        SORTABLE_FIELDS.put("createddate", "govscot:createddate");
        SORTABLE_FIELDS.put("embargodate", "govscot:embargodate");
        SORTABLE_FIELDS.put("state", "govscot:state");
        SORTABLE_FIELDS.put("username", "govscot:username");
    }

    private static final String DEFAULT_SORT = "createddate";

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
    @Override
    public void create(Publication publication) throws PublicationRepositoryException {
        try {
            ensureRoot();
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
    @Override
    public void update(Publication publication) throws PublicationRepositoryException {
        try {
            Node node = findById(publication.getId());
            publication.setLastmodifieddate(new Timestamp(System.currentTimeMillis()));
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
    @Override
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
     * @param username username to match (partial case insensitive
     * @param sort field to sort by, defaults to {@value #DEFAULT_SORT} if blank or unrecognised
     * @param dir sort direction, "asc" or "desc" (defaults to "desc")
     * @return Collection of matching publications
     * @throws PublicationRepositoryException if it fails to list publications
     */
    @Override
    public ListResult list(int page, int size, String title, String isbn, String filename, String username, String sort, String dir)
            throws PublicationRepositoryException {

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM nt:unstructured ");
            List<String> andterms = new ArrayList<>();
            andterms.add("jcr:path LIKE '/content/publicationjobs/%'");
            // exclude the bucket folder nodes created by path()/HippoPaths.ensurePath, which are
            // also nt:unstructured and would otherwise be picked up alongside real publications
            andterms.add("govscot:id IS NOT NULL");

            if (isNotBlank(title)) {
                andterms.add(String.format("CONTAINS(govscot:title, '%s')", escapeForContains(title)));
            }

            if (isNotBlank(isbn)) {
                andterms.add(String.format("CONTAINS(govscot:isbn, '%s')", escapeForContainsPrefix(isbn)));
            }
            if (isNotBlank(filename)) {
                andterms.add(String.format("CONTAINS(govscot:filename, '%s')", escapeForContainsPrefix(filename)));
            }
            if (isNotBlank(username)) {
                andterms.add(String.format("CONTAINS(govscot:username, '%s')", escapeForContainsPrefix(username)));
            }

            if (!andterms.isEmpty()) {
                sql.append(" WHERE ");
                sql.append(andterms.stream().collect(joining(" AND ")));
            }

            sql.append(orderByClause(sort, dir));
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

    /**
     * Build an ORDER BY clause for the given sort field and direction, using only allowlisted
     * property names so the sort param can never be used to inject arbitrary JCR-SQL.
     */
    String orderByClause(String sort, String dir) {
        String property = SORTABLE_FIELDS.getOrDefault(sort, SORTABLE_FIELDS.get(DEFAULT_SORT));
        String direction = "asc".equalsIgnoreCase(dir) ? "ASC" : "DESC";
        return String.format(" ORDER BY %s %s", property, direction);
    }

    /**
     * Sanitise a user supplied search term for use inside a CONTAINS() fulltext expression,
     * wrapping it as a phrase so multi word terms are matched as adjacent words rather than
     * being interpreted as fulltext query syntax (AND/OR/NOT, wildcards, quotes).
     */
    String escapeForContains(String term) {
        String sanitized = term.replace("\"", "").replace("'", "");
        return "\"" + sanitized + "\"";
    }

    /**
     * Sanitise a user supplied search term for use as a fulltext prefix match (term*), for fields
     * such as isbn/filename that are indexed as a single token, where a caller is typically typing
     * from the start of the value rather than searching for a whole word.
     */
    String escapeForContainsPrefix(String term) {
        String sanitized = term.replace("\"", "").replace("'", "").replace("*", "").trim();
        return sanitized + "*";
    }

    /**
     * Create the {@value #ROOT} node if it does not already exist. HippoPaths.ensurePath only
     * creates the elements below the root it is given, so the root itself has to be created here.
     */
    void ensureRoot() throws RepositoryException {
        if (!session.nodeExists(ROOT)) {
            session.getNode("/content").addNode("publicationjobs", "nt:unstructured");
        }
    }

    List<String> path(Publication publication) {
        String guid = publication.getId().replaceAll("-", "");
        return Arrays.asList(guid.substring(0, 2), guid.substring(2, 4));
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
        Calendar createdDateCal = Calendar.getInstance();
        createdDateCal.setTimeInMillis(publication.getCreateddate().getTime());
        node.setProperty("govscot:createddate", createdDateCal);
        Calendar embargoDateCal = Calendar.getInstance();
        embargoDateCal.setTimeInMillis(publication.getEmbargodate().getTime());
        node.setProperty("govscot:embargodate", embargoDateCal);
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
        publication.setCreateddate(new Timestamp(node.getProperty("govscot:createddate").getDate().getTimeInMillis()));
        publication.setEmbargodate(new Timestamp(node.getProperty("govscot:embargodate").getDate().getTimeInMillis()));
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
