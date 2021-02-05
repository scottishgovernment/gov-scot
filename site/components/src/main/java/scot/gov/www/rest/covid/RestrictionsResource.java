package scot.gov.www.rest.covid;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * Allow querying of local covid restrictions.
 * - CORS headers?
 */

@Path("/covid/")
public class RestrictionsResource {

    private static final Logger LOG = LoggerFactory.getLogger(RestrictionsResource.class);

    private static final String RESTRICTIONS_XPATH = "/jcr:root/content/documents/govscot/covidrestrictions//element(*, govscot:covidrestriction)[hippostd:state = 'published']";

    private static final String LEVELS_XPATH = "/jcr:root/content/documents/govscot/covidrestrictions/levels//element(*, govscot:covidlevel)[hippostd:state = 'published']";

    @Path("restrictions")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<Restriction> getRestrictions() {

        try {
            return doGetRestrictions();
        } catch (RepositoryException e) {
            LOG.error("getRestrictions failed", e);
            Response response = Response.status(SERVICE_UNAVAILABLE).entity("Failed to get covid restrictions data").build();
            throw new WebApplicationException(response);
        }
    }

    public List<Restriction> doGetRestrictions() throws RepositoryException {

        Map<String, Level> levels = getLevels();
        QueryResult result = executeQuery(RESTRICTIONS_XPATH);
        NodeIterator iterator = result.getNodes();
        List<Restriction> restrictions = new ArrayList<>();
        while (iterator.hasNext()) {
            Node restrictionNode = iterator.nextNode();
            String level = getLevel(restrictionNode);
            Restriction restriction = restrictionForNode(restrictionNode);
            restriction.setLevel(levels.get(level));
            restrictions.add(restriction);
        }
        return restrictions;
    }

    Map<String, Level> getLevels() throws RepositoryException {

        Map<String, Level> levels = new HashMap<>();
        QueryResult result = executeQuery(LEVELS_XPATH);
        NodeIterator iterator = result.getNodes();
        while (iterator.hasNext()) {
            Node levelNode = iterator.nextNode();
            Level level = levelForNode(levelNode);
            levels.put(level.getLevel(), level);
        }
        return levels;
    }

    QueryResult executeQuery(String xpath) throws RepositoryException {

        Session session = RequestContextProvider.get().getSession();
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        return query.execute();
    }

    Restriction restrictionForNode(Node node) throws RepositoryException {

        Restriction restriction = new Restriction();
        restriction.setId(node.getProperty("govscot:restrictedid").getString());
        restriction.setType(node.getProperty("govscot:type").getString());
        restriction.setTitle(node.getProperty("govscot:title").getString());
        restriction.setDescription(getDescription(node));
        return restriction;
    }

    Level levelForNode(Node node) throws RepositoryException {

        Level level = new Level();
        level.setLevel(getLevel(node));
        level.setTitle(node.getProperty("govscot:title").getString());
        level.setDescription(getDescription(node));
        level.setLink(getLink(node));
        return level;
    }

    String getLevel(Node node) throws RepositoryException {
        return node.getProperty("govscot:level").getString();
    }

    String getDescription(Node node) throws RepositoryException {
        return node.hasNode("govscot:description")
                ? node.getNode("govscot:description").getProperty("hippostd:content").getString()
                : "";
    }

    String getLink(Node from) throws RepositoryException {
        HstRequestContext request = RequestContextProvider.get();
        Session session = RequestContextProvider.get().getSession();
        String uuid = from.getNode("govscot:link").getProperty("hippo:docbase").getString();
        Node to = session.getNodeByIdentifier(uuid);
        HstLink link = RequestContextProvider.get().getHstLinkCreator().create(to, request);
        return link.getPath();
    }
}
