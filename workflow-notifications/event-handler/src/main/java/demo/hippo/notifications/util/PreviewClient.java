package demo.hippo.notifications.util;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/preview/")
public interface PreviewClient {

    @Path("/uuid/{uuid}")
    @GET
    public String getDocumentUrl(@PathParam(value = "uuid") String uuid);
}
