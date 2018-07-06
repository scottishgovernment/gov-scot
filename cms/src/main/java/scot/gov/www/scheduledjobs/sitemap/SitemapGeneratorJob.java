package scot.gov.www.scheduledjobs.sitemap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.client.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.time.DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT;
import static scot.gov.www.scheduledjobs.sitemap.SitemapAssetsUtils.createOrUpdateResource;

/**
 *
 * Scheduled job to create requred sitemaps as assets.
 *
 * Works by first generating the sitemap index file and then by generating a sitemap for each folder that s a child
 * of the root content folder.  Urls are fetch from hst / site via a rest service in batches of 100.
 */
public class SitemapGeneratorJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapGeneratorJob.class);

    private static final String SITEMAP_NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

    private static final String ROOT_URL = "https://www.beta.gov.scot/";

    private static final String REST_URL = "http://localhost:8080/site/rest/urls/";

    private static final String CONTENT_DOCUMENTS_GOVSCOT = "/content/documents/govscot";

    private static final String HIPPOSTD_FOLDER = "hippostd:folder";

    private static final Set<String> STOPLIST = new HashSet<>();

    private static final Set<String> QUERY_BACKED_TYPES = new HashSet<>();

    private Client restClient = ClientBuilder.newClient();

    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        // list of items to exclude
        Collections.addAll(STOPLIST, "valuelists", "featured-items", "404", "search");

        // types that are backed by queries and whose modified date should be set to the start of today
        Collections.addAll(QUERY_BACKED_TYPES, "govscot:Home", "govscot:Issue", "govscot:Topic");
    }

    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        LOG.info("Generating sitemap");

        Session session = null;
        try {
            session = context.createSystemSession();
            session.refresh(false);
            createOrUpdateResource(session, "", sitemapindex(session));

            Node root = session.getNode(CONTENT_DOCUMENTS_GOVSCOT);
            NodeIterator nodeIterator = root.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                String name = node.getName();
                if (STOPLIST.contains(name) || !node.isNodeType(HIPPOSTD_FOLDER)) {
                    continue;
                }
                NodeIterator nodesForPath = getPublishedNodesUnderPath(session, CONTENT_DOCUMENTS_GOVSCOT + "/" + name);
                byte [] urlset = urlset(nodesForPath);
                createOrUpdateResource(session, name, urlset);
            }

            // now create the root sitemap (only include items directly at the root)
            byte [] urlset = urlset(session.getNode(CONTENT_DOCUMENTS_GOVSCOT).getNodes());
            createOrUpdateResource(session, "root", urlset);
        } catch (XMLStreamException | IOException | RepositoryException e) {
            LOG.error("Failed to write sitemap", e);
        } finally {
            if(session != null) {
                session.logout();
            }
        }
    }

    private byte [] sitemapindex(Session session) throws IOException, XMLStreamException, RepositoryException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
        writer.setDefaultNamespace(SITEMAP_NS);
        writer.writeStartDocument();
        writer.writeStartElement(SITEMAP_NS, "sitemapindex");

        Node root = session.getNode(CONTENT_DOCUMENTS_GOVSCOT);
        NodeIterator nodeIterator = root.getNodes();
        while (nodeIterator.hasNext()) {
            Node child = nodeIterator.nextNode();

            if (STOPLIST.contains(child.getName()) || !child.isNodeType(HIPPOSTD_FOLDER)) {
                continue;
            }

            writer.writeStartElement(SITEMAP_NS, "sitemap");
            writer.writeStartElement(SITEMAP_NS, "loc");
            String url = format("%ssitemap.%s.xml", ROOT_URL, child.getName());
            writer.writeCharacters(url);
            writer.writeEndElement();
            writer.writeEndElement();
        }

        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        out.close();

        return out.toByteArray();
    }

    private byte [] urlset(NodeIterator nodeIterator)
            throws RepositoryException, XMLStreamException, IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
        writer.setDefaultNamespace(SITEMAP_NS);
        writer.writeStartDocument();
        writer.writeStartElement(SITEMAP_NS, "urlset");


        Map<String, SitemapEntry> entriesByLoc = mapSitemapEntriesByLoc(nodeIterator);

        // convert the paths in the entries to urls using the rest service
        // partition them into chunks of 100 first
        Map<Integer, List<SitemapEntry>> partitions = partition(entriesByLoc.values());

        for (List<SitemapEntry> partition : partitions.values()) {
            UrlResponse urlResponse = fetchUrlsForPartition(restClient, partition);

            for (Map.Entry<String, String> pathAndUrl : urlResponse.getUrls().entrySet()) {
                SitemapEntry entry = entriesByLoc.get(pathAndUrl.getKey());

                String url = sitemapUrl(pathAndUrl.getValue());
                Calendar dateModified = entry.getLastModified();
                if (QUERY_BACKED_TYPES.contains(entry.getNodeType())) {
                    dateModified = startOfToday();
                }
                urlElement(writer, url, ISO_DATE_TIME_ZONE_FORMAT.format(dateModified));

                // write the policy latest page for policies
                if ("govscot:Policy".equals(entry.getNodeType())) {
                    urlElement(writer, url + "latest/", ISO_DATE_TIME_ZONE_FORMAT.format(startOfToday()));
                }
            }
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        out.close();
        return out.toByteArray();
    }

    private Calendar startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
    private void urlElement(XMLStreamWriter writer, String url, String date) throws XMLStreamException {
        writer.writeStartElement(SITEMAP_NS, "url");
        writer.writeStartElement(SITEMAP_NS, "loc");
        writer.writeCharacters(url);
        writer.writeEndElement();
        writer.writeStartElement("lastmod");
        writer.writeCharacters(date);
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private String sitemapUrl(String path) {
        String url = format("%s%s", ROOT_URL, path);
        return removeEnd(url, "index/");
    }

    private UrlResponse fetchUrlsForPartition(Client client, List<SitemapEntry> partition) throws IOException {
        UrlRequest urlRequest = new UrlRequest();
        urlRequest.setPaths(partition.stream().map(SitemapEntry::getLoc).collect(toList()));
        String res = client.target(REST_URL)
                .request(APPLICATION_JSON)
                .post(Entity.json(objectMapper.writeValueAsString(urlRequest)))
                .readEntity(String.class);
        return objectMapper.readValue(res, UrlResponse.class);
    }

    private NodeIterator getPublishedNodesUnderPath(Session session, String path) throws RepositoryException {
        String sql = format(
                "SELECT * FROM hippo:document WHERE " +
                        "hippostd:state = 'published' AND " +
                        "hippostd:stateSummary = 'live' AND " +
                        "jcr:path LIKE '%s/%%'",
                path);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL).execute();
        return result.getNodes();
    }

    private Map<String, SitemapEntry> mapSitemapEntriesByLoc(NodeIterator nodeIt) throws RepositoryException {
        Map<String, SitemapEntry> entries = new HashMap<>();
        while (nodeIt.hasNext()) {
            Node node = nodeIt.nextNode();
            if (includeSitemapEntry(node)) {
                SitemapEntry entry = toSitemapEntry(node);
                entries.put(entry.getLoc(), entry);
            }
        }
        return entries;
    }

    private boolean includeSitemapEntry(Node node) throws RepositoryException {

        if (STOPLIST.contains(node.getName()) || node.isNodeType(HIPPOSTD_FOLDER)) {
            return false;
        }

        // exclude document information codes since they are not pages
        if (node.isNodeType("govscot:DocumentInformation")) {
            return false;
        }

        // ensure the item is published
        if (!node.hasProperty("hippostd:state") || !"published".equals(node.getProperty("hippostd:state").getString())) {
            return false;
        }

        return true;
    }

    private SitemapEntry toSitemapEntry(Node node) throws RepositoryException {
        SitemapEntry entry = new SitemapEntry();
        entry.setLoc(node.getPath());
        entry.setNodeType(node.getPrimaryNodeType().getName());
        if (node.hasProperty("hippostdpubwf:lastModificationDate")) {
            entry.setLastModified(node.getProperty("hippostdpubwf:lastModificationDate").getDate());
        } else {
            entry.setLastModified(Calendar.getInstance());
            LOG.info("Node has no modificaiton date: {}", node.getPath());
        }
        return entry;
    }

    private Map<Integer, List<SitemapEntry>> partition(Collection<SitemapEntry> entries) {
        int[] count = new int[1];
        return entries.stream().collect(
                groupingBy(
                    user -> {
                        count[0]++;
                        return Math.floorDiv( count[0], 100 );
                    }
                ));
    }
}
