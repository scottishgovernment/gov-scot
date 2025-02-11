package scot.gov.www.rest;

import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.repository.api.HippoSession;
import org.onehippo.repository.security.SessionUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.jcr.ItemNotFoundException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

public class InRoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(InRoleResource.class);

    @Context
    SecurityContext securityContext;

    public InRoleResource() {
    }

    @GET
    @Path("role/{role}")
    public Response upload(@PathParam("role") String role) {
        HippoSession session = UserSession.get().getJcrSession();
        try {
            SessionUser sessionUser = session.getUser();
            LOG.info("{}\n{}",
                    sessionUser.getUserRoles(),
                    sessionUser.getMemberships()
            );
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        }

        boolean inRole = securityContext.isUserInRole(role);
        LOG.info("User {} in role {}: {}",
                role,
                inRole);
        if (inRole) {
            return Response.ok().build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

}
