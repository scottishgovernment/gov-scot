package scot.gov.searchjournal;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureFlagsResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureFlagsResource.class);

    private static final String FEATURE_FLAGS_PATH = "/content/featureflags/";

    Session session;

    public FeatureFlagsResource(Session session) {
        this.session = session;
    }

    @PUT
    @Path("{flag}/{enabled}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response setEnabledFlag(@PathParam("flag") String flag, @PathParam("enabled") boolean isEnabled) {

        try {
            Node flagsNode = getFlagsNode();
            flagsNode.setProperty(flag, isEnabled);
            session.save();
            return Response.status(Response.Status.OK).entity("Done").build();
        } catch (RepositoryException e) {
            LOG.error("Failed to set enabled flag for FunnelbackReconciliationLoop", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{flag}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getEnabledFlag(@PathParam("flag") String flag) {

        try {
            Node flagsNode = getFlagsNode();

            boolean isEnabled = false;
            if (flagsNode.hasProperty(flag)) {
                isEnabled = flagsNode.getProperty(flag).getBoolean();
            }
            return Response.status(Response.Status.OK).entity(Boolean.toString(isEnabled)).build();
        } catch (RepositoryException e) {
            LOG.error("Failed to get enabled flag for FunnelbackReconciliationLoop", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    Node getFlagsNode() throws RepositoryException {

        return session.nodeExists(FEATURE_FLAGS_PATH)
                ? session.getNode(FEATURE_FLAGS_PATH)
                : createFlagsNode();
    }

    Node createFlagsNode() throws RepositoryException {
        return session.getNode("/content").addNode("featureflags", "nt:unstructured");
    }

}