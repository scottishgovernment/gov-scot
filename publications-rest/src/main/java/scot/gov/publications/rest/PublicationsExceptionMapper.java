package scot.gov.publications.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

/**
 * Catches any exception not handled by the resource methods (e.g. JSON serialisation errors)
 * and logs it before returning a 500, so the root cause is always visible in the logs.
 */
@Provider
public class PublicationsExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        LOG.error("Unhandled exception in publications REST endpoint", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
