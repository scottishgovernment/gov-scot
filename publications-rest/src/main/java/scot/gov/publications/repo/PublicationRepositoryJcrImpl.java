package scot.gov.publications.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String PROP_ID = "govscot:id";

    private static final String PROP_TITLE = "govscot:title";

    private static final String PROP_ISBN = "govscot:isbn";

    private static final String PROP_CONTACT = "govscot:contact";

    private static final String PROP_FILENAME = "govscot:filename";

    private static final String PROP_STATE = "govscot:state";

    private static final String PROP_STATEDETAILS = "govscot:statedetails";

    private static final String PROP_USERNAME = "govscot:username";

    private static final String PROP_CREATEDDATE = "govscot:createddate";

    private static final String PROP_EMBARGODATE = "govscot:embargodate";

    private static final String PROP_URL = "govscot:url";

    private static final String PROP_DOCUMENTCOUNT = "govscot:documentcount";

    private static final String PROP_PAGECOUNT = "govscot:pagecount";

    private static final String PROP_EMBARGOED = "govscot:embargoed";

    private static final String PROP_SCHEDULED = "govscot:scheduled";

    private static final String PROP_PUBLISHED = "govscot:published";

    private static final String PROP_STACKTRACE = "govscot:stacktrace";

    private static final String PROP_CHECKSUM = "govscot:checksum";

    private static final String DEFAULT_SORT = "createddate";

    private static final String CONTAINS_FORMAT = "CONTAINS(%s, '%s')";

    /**
     * Allowlist of the fields a caller may sort by, mapped to their JCR property name. Sort input
     * is never concatenated into the query directly to avoid JCR-SQL injection via the sort param.
     */
    private static final Map<String, String> SORTABLE_FIELDS = new LinkedHashMap<>();
    static {
        SORTABLE_FIELDS.put("title", PROP_TITLE);
        SORTABLE_FIELDS.put("isbn", PROP_ISBN);
        SORTABLE_FIELDS.put("filename", PROP_FILENAME);
        SORTABLE_FIELDS.put(DEFAULT_SORT, PROP_CREATEDDATE);
        SORTABLE_FIELDS.put("embargodate", PROP_EMBARGODATE);
        SORTABLE_FIELDS.put("state", PROP_STATE);
        SORTABLE_FIELDS.put("username", PROP_USERNAME);
    }

    Session session;

    HippoPaths paths;

    HippoUtils hippoUtils;

    public PublicationRepositoryJcrImpl(Session session) {
        this.session = session;
        this.paths = new HippoPaths(session);
        this.hippoUtils = new HippoUtils();
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
     * @param pageable page number and page size
     * @param query the optional search and sort parameters - see {@link PublicationListQuery} for
     *              the meaning of each
     * @return Collection of matching publications
     * @throws PublicationRepositoryException if it fails to list publications
     */
    @Override
    public ListResult list(Pageable pageable, PublicationListQuery query) throws PublicationRepositoryException {
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM nt:unstructured ");
            List<String> andterms = new ArrayList<>();
            andterms.add("jcr:path LIKE '/content/publicationjobs/%'");
            // exclude the bucket folder nodes created by path()/HippoPaths.ensurePath, which are
            // also nt:unstructured and would otherwise be picked up alongside real publications
            andterms.add(PROP_ID + " IS NOT NULL");

            if (isNotBlank(query.getTitle())) {
                // title is a multi word field, so require each word in the search term to
                // be present (as a prefix match) rather than treating the whole term as one token
                for (String word : query.getTitle().trim().split("\\s+")) {
                    andterms.add(String.format(CONTAINS_FORMAT, PROP_TITLE, escapeForContainsPrefix(word)));
                }
            }

            if (isNotBlank(query.getIsbn())) {
                andterms.add(String.format(CONTAINS_FORMAT, PROP_ISBN, escapeForContainsPrefix(query.getIsbn())));
            }
            if (isNotBlank(query.getFilename())) {
                andterms.add(String.format(CONTAINS_FORMAT, PROP_FILENAME, escapeForContainsPrefix(query.getFilename())));
            }
            if (isNotBlank(query.getUsername())) {
                andterms.add(String.format(CONTAINS_FORMAT, PROP_USERNAME, escapeForContainsPrefix(query.getUsername())));
            }
            if (isNotBlank(query.getState()) && isValidState(query.getState())) {
                andterms.add(String.format("%s = '%s'", PROP_STATE, query.getState()));
            }

            if (!andterms.isEmpty()) {
                sql.append(" WHERE ");
                sql.append(andterms.stream().collect(joining(" AND ")));
            }

            sql.append(orderByClause(query.getSort(), query.getDir()));
            return executeQuery(sql.toString(), pageable.getPage(), pageable.getSize());
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
     * Sanitise a user supplied search term for use as a fulltext prefix match (term*), for fields
     * such as isbn/filename that are indexed as a single token, where a caller is typically typing
     * from the start of the value rather than searching for a whole word.
     */
    String escapeForContainsPrefix(String term) {
        String sanitized = term.replace("\"", "").replace("'", "").replace("*", "").trim();
        return sanitized + "*";
    }

    /**
     * Only allow known {@link State} values through to the query, since state is concatenated
     * directly into the JCR-SQL rather than passed as a bind parameter.
     */
    boolean isValidState(String state) {
        for (State s : State.values()) {
            if (s.name().equals(state)) {
                return true;
            }
        }
        return false;
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
        node.setProperty(PROP_ID, publication.getId());
        node.setProperty(PROP_TITLE, publication.getTitle());
        node.setProperty(PROP_ISBN, publication.getIsbn());
        node.setProperty(PROP_CONTACT, publication.getContact());
        node.setProperty(PROP_FILENAME, publication.getFilename());
        node.setProperty(PROP_STATE, publication.getState());
        node.setProperty(PROP_STATEDETAILS, publication.getStatedetails());
        node.setProperty(PROP_USERNAME, publication.getUsername());
        Calendar createdDateCal = Calendar.getInstance();
        createdDateCal.setTimeInMillis(publication.getCreateddate().getTime());
        node.setProperty(PROP_CREATEDDATE, createdDateCal);
        Calendar embargoDateCal = Calendar.getInstance();
        embargoDateCal.setTimeInMillis(publication.getEmbargodate().getTime());
        node.setProperty(PROP_EMBARGODATE, embargoDateCal);
        node.setProperty(PROP_URL, publication.getUrl());
        node.setProperty(PROP_DOCUMENTCOUNT, publication.getDocumentcount());
        node.setProperty(PROP_PAGECOUNT, publication.getPagecount());
        node.setProperty(PROP_EMBARGOED, publication.isEmbargoed());
        node.setProperty(PROP_SCHEDULED, publication.isScheduled());
        node.setProperty(PROP_PUBLISHED, publication.isPublished());
        node.setProperty(PROP_STACKTRACE, publication.getStacktrace());
        node.setProperty(PROP_CHECKSUM, publication.getChecksum());
        node.setProperty("hippostd:state", "published");
    }

    Publication nodeToPublication(Node node) throws RepositoryException {
        Publication publication = new Publication();
        publication.setId(node.getProperty(PROP_ID).getString());
        publication.setTitle(node.getProperty(PROP_TITLE).getString());
        publication.setIsbn(node.getProperty(PROP_ISBN).getString());
        publication.setContact(propWithDefault(node, PROP_CONTACT, ""));
        publication.setFilename(node.getProperty(PROP_FILENAME).getString());
        publication.setState(node.getProperty(PROP_STATE).getString());
        publication.setStatedetails(propWithDefault(node, PROP_STATEDETAILS, ""));
        publication.setUsername(propWithDefault(node, PROP_USERNAME, ""));
        publication.setCreateddate(new Timestamp(node.getProperty(PROP_CREATEDDATE).getDate().getTimeInMillis()));
        publication.setEmbargodate(new Timestamp(node.getProperty(PROP_EMBARGODATE).getDate().getTimeInMillis()));
        publication.setUrl(propWithDefault(node, PROP_URL, null));
        publication.setDocumentcount(intPropWithDefault(node, PROP_DOCUMENTCOUNT, 0));
        publication.setPagecount(intPropWithDefault(node, PROP_PAGECOUNT, 0));
        publication.setEmbargoed(boolPropWithDefault(node, PROP_EMBARGOED, false));
        publication.setScheduled(boolPropWithDefault(node, PROP_SCHEDULED, false));
        publication.setPublished(boolPropWithDefault(node, PROP_PUBLISHED, false));
        publication.setStacktrace(propWithDefault(node, PROP_STACKTRACE, null));
        publication.setChecksum(propWithDefault(node, PROP_CHECKSUM, null));
        return publication;
    }

    String propWithDefault(Node node, String prop, String defaultVal) throws RepositoryException {
        return node.hasProperty(prop) ? node.getProperty(prop).getString() : defaultVal;
    }

    int intPropWithDefault(Node node, String prop, int defaultVal) throws RepositoryException {
        return node.hasProperty(prop) ? (int) node.getProperty(prop).getLong() : defaultVal;
    }

    boolean boolPropWithDefault(Node node, String prop, boolean defaultVal) throws RepositoryException {
        return node.hasProperty(prop) ? node.getProperty(prop).getBoolean() : defaultVal;
    }

    Node findById(String id) throws RepositoryException {
        String sql= String.format(
                    "SELECT * FROM nt:unstructured " +
                    "WHERE jcr:path LIKE '/content/publicationjobs/%%' " +
                    "AND " + PROP_ID + " = '%s'", id);
        return hippoUtils.findOneQueryNoArgs(session, sql, Query.SQL);
    }

}
