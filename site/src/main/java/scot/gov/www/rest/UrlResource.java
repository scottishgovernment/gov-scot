package scot.gov.www.rest;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


@Path("/urls/")
public class UrlResource extends org.hippoecm.hst.jaxrs.services.AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(UrlResource.class);

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public UrlResponse getUrls(@RequestBody UrlRequest urlRequest) {
        UrlResponse response = new UrlResponse();
        response.setUrls(urlRequest.getPaths()
                .stream().collect(toMap(identity(), this::toUrl)));
        return response;
    }

    private String toUrl(String path) {
        HstRequestContext req = RequestContextProvider.get();
        try {
            Session session = req.getSession();
            Node node = session.getNode(path);
            HstLink link = req.getHstLinkCreator().create(node, req);
            return link.getPath();
        } catch (RepositoryException e) {
            LOG.warn("Unable to get url for path {}", path, e);
            return "invalidpath";
        }
    }
}
