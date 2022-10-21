package scot.gov.www.rest.sitemap;

import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.sitemap.components.model.Url;
import org.onehippo.forge.sitemap.components.model.Urlset;
import org.onehippo.forge.sitemap.components.model.sitemapindex.SitemapIndex;
import org.onehippo.forge.sitemap.components.model.sitemapindex.TSitemap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.hippoecm.hst.container.RequestContextProvider;

import java.time.LocalDate;
import java.util.Calendar;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class SitemapResource {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapResource.class);

    private static final String SITEMAP_ROOT_PATH = "/content/sitemaps";

    static final String LAST_MOD = "govscot:lastMod";

    static final String LATEST_LAST_MOD = "govscot:latestLastMod";

    static final String LATEST_PATH = "sitemap/latest.xml";

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    SitemapLatestGenerator sitemapLatestGenerator = new SitemapLatestGenerator();

    @Path("sitemap.xml")
    @Produces(APPLICATION_XML)
    @GET
    public Response getSitemapIndex() {
        return processRequest(
                "/sitemap.xml",
                this::getSitemapRootNode,
                this::generateSitemapIndex);
    }

    @Path(LATEST_PATH)
    @Produces(APPLICATION_XML)
    @GET
    public Response getLatestSitemap() {

        String path = uriInfo.getPathParameters().getFirst("path");
        return processRequest(
                path,
                this::getSitemapNodeForLatest,
                this::generateLatestSitemap);
    }

    @Path("{path: .+}")
    @Produces(APPLICATION_XML)
    @GET
    public Response getSitemap() {
        String path = uriInfo.getPathParameters().getFirst("path");
        return processRequest(
                path,
                this::getSitemapNodeForPath,
                this::generateSitemap);
    }

    Response processRequest(String path, NodeSupplier nodeSupplier, EntitySupplier entitySupplier) {

        try {
            Node node = nodeSupplier.getNode();
            if (node == null) {
                return Response.status(404).entity("Sitemap not found").build();
            }
            Calendar lastModified = getLastModifiedDate(path, node);
            Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(lastModified.getTime());
            if (responseBuilder != null) {
                return responseBuilder.build();
            }
            Object entity = entitySupplier.getEntity(node);
            return Response.status(Response.Status.OK).entity(entity).build();
        } catch (PathNotFoundException e) {
            return notFoundError("Sitemap not found " + path, e);
        } catch (RepositoryException e) {
            return serverError("failed to generate sitemap", e);
        }
    }

    Calendar getLastModifiedDate(String path, Node node) throws RepositoryException {

        if (LATEST_PATH.equals(path)) {
            return node.getParent().getProperty(LATEST_LAST_MOD).getDate();
        }
        return node.getProperty(LAST_MOD).getDate();
    }

    Node getSitemapRootNode() throws RepositoryException {
        String siteName = getSiteName();
        String sitemapPath = new StringBuilder(SITEMAP_ROOT_PATH).append('/').append(siteName).toString();
        Session session = RequestContextProvider.get().getSession();
        return session.getNode(sitemapPath);
    }

    SitemapIndex generateSitemapIndex(Node node) throws RepositoryException {
        String siteName = getSiteName();
        String rootUrl = siteRootUrl();
        SitemapIndex sitemapIndex = new SitemapIndex();
        TSitemap latestSitemap = new TSitemap();
        String latestPath = new StringBuilder(rootUrl).append("/sitemap/latest.xml").toString();
        latestSitemap.setLoc(latestPath);
        sitemapIndex.getSitemap().add(latestSitemap);
        populateSitemapIndex(node, sitemapIndex, siteName, rootUrl);
        return sitemapIndex;
    }

    void populateSitemapIndex(Node node, SitemapIndex sitemapIndex, String siteName, String rootUrl) throws RepositoryException {
        NodeIterator it = node.getNodes();

        while (it.hasNext()) {
            Node child = it.nextNode();
            if (containsUrls(child) && !isCurrentMonth(child)) {
                addSitemap(child, sitemapIndex, siteName, rootUrl);
            } else {
                populateSitemapIndex(child, sitemapIndex, siteName, rootUrl);
            }
        }
    }

    boolean isCurrentMonth(Node node) throws RepositoryException {
        LocalDate today = LocalDate.now();
        String year = Integer.toString(today.getYear());
        String month = Integer.toString(today.getMonthValue());
        return node.getName().equals(month) && node.getParent().getName().equals(year);
    }

    boolean containsUrls(Node node) throws RepositoryException {
        return node.hasNodes() && node.getNodes().nextNode().hasProperty("govscot:loc");
    }

    void addSitemap(Node node, SitemapIndex sitemapIndex, String siteName, String rootUrl) throws RepositoryException {
        TSitemap tSitemap = new TSitemap();
        String sitemapRootPath =  SITEMAP_ROOT_PATH + '/' + siteName;
        String sitemapPath = substringAfter(node.getPath(), sitemapRootPath);
        String url = new StringBuilder(rootUrl)
                .append("/sitemap")
                .append(sitemapPath)
                .append(".xml")
                .toString();
        tSitemap.setLoc(url);
        sitemapIndex.getSitemap().add(tSitemap);
    }

    Node getSitemapNodeForPath() throws RepositoryException {
        Session session = RequestContextProvider.get().getSession();
        String siteName = getSiteName();
        String requestPath = uriInfo.getPathParameters().getFirst("path");
        String repositoryPath = getRepositoryPathFromRequestPath(siteName, requestPath);
        return session.nodeExists(repositoryPath) ? session.getNode(repositoryPath) : null;
    }

    Node getSitemapNodeForLatest() throws RepositoryException {
        Session session = RequestContextProvider.get().getSession();
        String siteName = getSiteName();
        String yearString = Integer.toString(LocalDate.now().getYear());
        String monthString = Integer.toString(LocalDate.now().getMonthValue());
        String repositoryPath = new StringBuffer(SITEMAP_ROOT_PATH)
                .append('/')
                .append(siteName)
                .append('/')
                .append(yearString)
                .append('/')
                .append(monthString)
                .toString();
        return session.nodeExists(repositoryPath) ? session.getNode(repositoryPath) : null;
    }

    String getRepositoryPathFromRequestPath(String siteName, String requestPath) {

        requestPath = substringAfter(substringBefore(requestPath, ".xml"), "sitemap/");
        return new StringBuffer(SITEMAP_ROOT_PATH)
                .append('/')
                .append(siteName)
                .append('/')
                .append(requestPath)
                .toString();
    }

    Urlset generateSitemap(Node node) throws RepositoryException {
        String rootUrl = siteRootUrl();
        NodeIterator it = node.getNodes();
        Urlset urlset = new Urlset();
        while (it.hasNext()) {
            Node child = it.nextNode();
            Url url = buildUrl(child, rootUrl);
            urlset.getUrls().add(url);
        }
        return urlset;
    }

    Urlset generateLatestSitemap(Node node) throws RepositoryException {
        Urlset latestUrlset = sitemapLatestGenerator.generateSitemap(node);
        if (node != null) {
            Urlset urlset = generateSitemap(node);
            latestUrlset.getUrls().addAll(urlset.getUrls());
        }
        return latestUrlset;
    }

    Url buildUrl(Node node, String rootUrl) throws RepositoryException {
        Url urlObj = new Url();
        Calendar lastMod = node.getProperty(LAST_MOD).getDate();
        String url = fullyQualifiedUrl(node, rootUrl);
        urlObj.setLoc(url);
        urlObj.setLastmod(lastMod);
        return urlObj;
    }

    String fullyQualifiedUrl(Node node, String rootUrl) throws RepositoryException {
        String loc = node.getProperty("govscot:loc").getString();
        return new StringBuilder(rootUrl).append(loc).toString();
    }

    static String siteRootUrl() {
        HstRequestContext context = RequestContextProvider.get();
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        String rootUrl = linkCreator.create(context.getSiteContentBaseBean(), context).toUrlForm(context, true);
        return stripEnd(rootUrl, "/");
    }

    String getSiteName() {
        return RequestContextProvider.get()
                .getResolvedMount()
                .getMount()
                .getHstSite()
                .getName();
    }

    Response serverError(String msg, Exception e) {
        LOG.error(msg, e);
        return Response.serverError().entity(msg).build();
    }

    Response notFoundError(String msg, Exception e) {
        LOG.error(msg, e);
        return Response.status(404).entity(msg).build();
    }

    interface NodeSupplier {
        Node getNode() throws RepositoryException;
    }

    interface EntitySupplier {
        Object getEntity(Node node) throws RepositoryException;
    }
}
