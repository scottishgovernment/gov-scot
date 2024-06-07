package scot.gov.pressreleases;

import com.fasterxml.jackson.core.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class ErrorHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Error handling request", exception);

        // parsing errors - 400
        if (exception instanceof JsonParseException) {
            return Response.status(BAD_REQUEST).entity("Invalid JSON").build();

        }

        // jcr exceptions
        if (exception instanceof RepositoryException) {
            return Response.status(INTERNAL_SERVER_ERROR).entity("jcr exception").build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).build();
    }

}