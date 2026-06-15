package scot.gov.publications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class RequestLogger implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLogger.class);

    /**
     * Runs before the request is processed.
     *
     * Adds the user to the MDC and records the start time on the request context.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String user = requestContext.getHeaderString("X-User");
        MDC.put("username", user);
        requestContext.setProperty("startTime", System.currentTimeMillis());
    }

    /**
     * Runs after the request has been processed.
     *
     * Removes the user from the MDC and logs the request details
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        String method = request.getRequest().getMethod();
        String path = request.getUriInfo().getPath();
        int status = response.getStatus();
        Long startTime = (Long) request.getProperty("startTime");
        if (startTime != null) {
            LOG.info("{} {} {} requestTime={}ms", status, method, path, System.currentTimeMillis() - startTime);
        } else {
            LOG.info("{} {} {}", status, method, path);
        }
        MDC.remove("user");
    }
}
