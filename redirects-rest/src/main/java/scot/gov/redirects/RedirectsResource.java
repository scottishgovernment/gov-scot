package scot.gov.redirects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class RedirectsResource {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectsResource.class);

    RedirectsRepository redirectsRepository;

    RedirectValidator redirectValidator = new RedirectValidator();

    @Context
    UriInfo uriInfo;

    public RedirectsResource(RedirectsRepository redirectsRepository) {
        this.redirectsRepository = redirectsRepository;
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response upload(List<Redirect> redirects) {

        List<String> violations = redirectValidator.validateRedirects(redirects);
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
        }

        try {
            redirectsRepository.createRedirects(redirects);
            logRedirects(redirects);
            return Response.status(Response.Status.OK).entity(redirects).build();
        } catch (RepositoryException e) {
            String error = "Unexpected exception uploading redirects";
            LOG.error(error, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @POST
    @Path("csv")
    @Consumes("text/csv")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response uploadCsv(@Multipart("file") File file) throws IOException {
        try (Reader in = new FileReader(file);
             CSVParser csvParser = new CSVParser(in, CSVFormat.DEFAULT)) {
            List<CSVRecord> records = csvParser.getRecords();
            List<Redirect> redirects = records.stream().map(this::toRedirect).collect(Collectors.toList());
            List<String> violations = redirectValidator.validateRedirects(redirects);
            if (!violations.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
            }
            redirectsRepository.createRedirects(redirects);
            logRedirects(redirects);
            return Response.status(Response.Status.OK).entity(redirects).build();
        } catch (IOException | RepositoryException e) {
            String error = "Unexpected exception uploading csv";
            LOG.error(error, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    Redirect toRedirect(CSVRecord record) {
        Redirect redirect = new Redirect();
        redirect.setFrom(record.get(0));
        redirect.setTo(record.get(1));
        if (record.size() > 2) {
            redirect.setDescription(record.get(2));
        }
        return redirect;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getRoot() {
        return doGet("/");
    }

    @GET
    @Path("{path: .+}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response get() {
        String path = uriInfo.getPathParameters().getFirst("path");
        return doGet(path);
    }

    Response doGet(String path) {
        try {
            RedirectResult result = redirectsRepository.list(path);
            if (result == null) {
                RedirectResult redirectResult = new RedirectResult();
                redirectResult.setDescription("No redirects found");
                return Response.status(Response.Status.NOT_FOUND).entity(redirectResult).build();
            } else {
                return Response.status(Response.Status.OK).entity(result).build();
            }
        } catch (RepositoryException e) {
            String error = "Unexpected exception fetching redirects";
            LOG.error(error, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @DELETE
    @Path("{path: .+}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response delete() {
        String path = uriInfo.getPathParameters().getFirst("path");
        try {
            boolean deleted = redirectsRepository.deleteRedirect(path);
            if (deleted) {
                LOG.info("deleted redirect \'{}\'", path);
                return Response.status(Response.Status.OK).entity("deleted").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("no redirect found").build();
            }
        } catch (RepositoryException e) {
            String error = "Unexpected exception deleting redirect";
            LOG.error(error, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    void logRedirects(List<Redirect> redirects) {
        for (Redirect redirect : redirects) {
            LOG.info("adding redirect  \'{}\' -> '{}' ({})", redirect.getFrom(), redirect.getTo(), description(redirect));
        }
    }

    String description(Redirect redirect) {
        return isBlank(redirect.getDescription())
                ? "no description"
                : redirect.getDescription();
    }

}