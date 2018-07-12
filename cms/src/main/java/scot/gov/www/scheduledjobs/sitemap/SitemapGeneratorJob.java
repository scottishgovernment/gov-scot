package scot.gov.www.scheduledjobs.sitemap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.client.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
 * Scheduled job to create required sitemaps as assets.
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
        } catch (XMLStreamException | IOException | RepositoryException | ParserConfigurationException | TransformerException e) {
            LOG.error("Failed to write sitemap", e);
        } finally {
            if(session != null) {
                session.logout();
            }
        }
    }

    private byte [] sitemapindex(Session session)
            throws IOException, ParserConfigurationException, TransformerException, RepositoryException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        dbf.setNamespaceAware(true);
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElementNS(SITEMAP_NS, "sitemap");
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", SITEMAP_NS);
        Node root = session.getNode(CONTENT_DOCUMENTS_GOVSCOT);
        NodeIterator nodeIterator = root.getNodes();
        while (nodeIterator.hasNext()) {
            Node child = nodeIterator.nextNode();

            if (STOPLIST.contains(child.getName()) || !child.isNodeType(HIPPOSTD_FOLDER)) {
                continue;
            }

            Element sitemapElement = doc.createElementNS(SITEMAP_NS, "sitemap");
            Element locElement = doc.createElementNS(SITEMAP_NS, "loc");
            locElement.appendChild(doc.createTextNode(format("%ssitemap.%s.xml", ROOT_URL, child.getName())));
            sitemapElement.appendChild(locElement);
            rootElement.appendChild(sitemapElement);
        }
        doc.appendChild(rootElement);
        return writeDocumentToBytes(doc);
    }

    private byte [] urlset(NodeIterator nodeIterator)
            throws RepositoryException, XMLStreamException, IOException, ParserConfigurationException, TransformerException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        dbf.setNamespaceAware(true);
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElementNS(SITEMAP_NS, "urlset");
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", SITEMAP_NS);

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

                rootElement.appendChild(urlElement(doc, url, dateModified));

                // write the policy latest page for policies
                if ("govscot:Policy".equals(entry.getNodeType())) {
                    urlElement(doc, url + "latest/", startOfToday());
                }
            }
        }
        doc.appendChild(rootElement);
        return writeDocumentToBytes(doc);
    }

    private Element urlElement(Document doc, String url, Calendar dateModified) {
        Element urlElement = doc.createElementNS(SITEMAP_NS, "url");
        Element locElement = doc.createElementNS(SITEMAP_NS, "loc");
        Element lastModElement = doc.createElementNS(SITEMAP_NS, "lastmod");
        locElement.appendChild(doc.createTextNode(url));
        lastModElement.appendChild(doc.createTextNode(ISO_DATE_TIME_ZONE_FORMAT.format(dateModified)));
        urlElement.appendChild(locElement);
        urlElement.appendChild(lastModElement);
        return urlElement;
    }

    private byte [] writeDocumentToBytes(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(out);
        transformer.transform(domSource, sr);
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
