package scot.gov.www.rest.metadata;

import org.hippoecm.hst.container.RequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Expose metadata required for external publication registration forms.
 *
 * Data that we expose:
 * - list of published Directorates
 * - list of published Roles (this includes people who have role specified internally)
 * - list of available Publication Types
 * - list of available Topics
 * - list of available Policies (including what topics they should be listed under)
 */
@Path("/metadata/")
public class MetadataResource {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataResource.class);

    @Path("directorates")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MetadataResponse getDirectorates() {
        return getMetadata(this::getDirectorateInfo, "govscot:Directorate");
    }

    @Path("roles")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MetadataResponse getRoles() {
        return getMetadata("govscot:Role", "govscot:Person");
    }

    @Path("topics")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MetadataResponse getTopics() {
        return getMetadata("govscot:Topic");
    }

    @Path("publicationtypes")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MetadataResponse getPublicationTypes() {
        try {
            Session session = RequestContextProvider.get().getSession();
            Node valueList = session.getNode("/content/documents/govscot/valuelists/publicationTypes/publicationTypes");
            MetadataResponse response = new MetadataResponse();
            addResponses(response, valueList.getNodes(), this::getItemFromListItem);
            return response;
        } catch (RepositoryException e) {
            LOG.error("Failed to generate metadata", e);
            throw new WebApplicationException("Metadata not available", e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("policies")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MetadataResponse getPolicies() {
        return getMetadata(this::getPolicyInfo, "govscot:Policy");
    }

    MetadataResponse getMetadata(String ...types) {
        return getMetadata(this::getMetadata, types);
    }

    MetadataResponse getMetadata(MetadataGetter getter, String ...types) {
        try {
            MetadataResponse response = new MetadataResponse();
            for (String type : types) {
                String sqlForType = sqlPublishedItemsOfType(type);
                NodeIterator iterator = performQuery(sqlForType);
                addResponses(response, iterator, getter);
            }
            response.getData().sort(Comparator.comparing(MetadataItem::getKey));
            return response;
        } catch (RepositoryException e) {
            LOG.error("Failed to generate metadata", e);
            throw new WebApplicationException("Metadata not available", e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    String sqlPublishedItemsOfType(String type) {
        return String.format("SELECT * FROM %s WHERE hippostd:state = 'published'", type);
    }

    NodeIterator performQuery(String sql) throws RepositoryException {
        Session session = RequestContextProvider.get().getSession();
        Query query  = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
        QueryResult result = query.execute();
        return result.getNodes();
    }


    MetadataItem getMetadata(Node node) throws RepositoryException {
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setKey(node.getParent().getName());
        metadataItem.setValue(node.getProperty("govscot:title").getString());
        return metadataItem;
    }

    MetadataItem getDirectorateInfo(Node node) throws RepositoryException {
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setKey(node.getParent().getParent().getName());
        metadataItem.setValue(node.getProperty("govscot:title").getString());
        return metadataItem;
    }

    MetadataItem getPolicyInfo(Node node) throws RepositoryException {
        PolicyMetadataItem metadataItem = new PolicyMetadataItem();
        metadataItem.setKey(node.getParent().getParent().getName());
        metadataItem.setValue(node.getProperty("govscot:title").getString());
        metadataItem.setTopics(topicIds(node));
        return metadataItem;
    }

    List<String> topicIds(Node node) throws RepositoryException {
        List<String> topicsIds = new ArrayList<>();
        NodeIterator it = node.getNodes("govscot:topics");
        Session session = RequestContextProvider.get().getSession();
        while (it.hasNext()) {
            Node topicMirror = it.nextNode();
            String docbase = topicMirror.getProperty("hippo:docbase").getString();
            Node topic = session.getNodeByIdentifier(docbase);
            topicsIds.add(topic.getName());
        }
        return topicsIds;
    }

    MetadataItem getItemFromListItem(Node node) throws RepositoryException {
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setKey(node.getProperty("selection:key").getString());
        metadataItem.setValue(node.getProperty("selection:label").getString());
        return metadataItem;
    }

    void addResponses(MetadataResponse response, NodeIterator iterator, MetadataGetter getter) throws RepositoryException {
        while (iterator.hasNext()) {
            response.getData().add(getter.getItemFromNode(iterator.nextNode()));
        }
    }

    @FunctionalInterface
    public interface MetadataGetter {
        MetadataItem getItemFromNode(Node t) throws RepositoryException;
    }

}
