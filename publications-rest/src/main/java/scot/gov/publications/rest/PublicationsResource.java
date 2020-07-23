package scot.gov.publications.rest;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.jcr.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporter;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.metadata.MetadataExtractor;
import scot.gov.publications.repo.*;
import scot.gov.publications.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

public class PublicationsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsResource.class);

    Session daemonSession;

    PublicationRepositoryJcrImpl repository;

    MetadataExtractor metadataExtractor = new MetadataExtractor();

    ZipUtil zipUtil = new ZipUtil();

    // ensure we are only processing one publication at a time
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public PublicationsResource(Session daemonSession, PublicationRepositoryJcrImpl repository) {
        this.daemonSession = daemonSession;
        this.repository = repository;
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response get(@PathParam("id") String id) {

        try {
            Publication publication = repository.get(id);
            if (publication == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(publication).build();
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to get publication {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response list(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size,
            @DefaultValue("") @QueryParam("title") String title,
            @DefaultValue("") @QueryParam("isbn") String isbn,
            @DefaultValue("") @QueryParam("filename") String filename) {

        try {
            ListResult result = repository.list(page, size, title, isbn, filename);
            return Response.ok(result).build();
        } catch (PublicationRepositoryException e) {
            throw new WebApplicationException(e, Response.status(500).build());
        }
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response uploadPublication(
            @Multipart("file") File file,
            @Multipart("filename") String filename,
            @HeaderParam("X-User") String username) {

        Publication publication;
        ZipFile zip;
        try {
            zip = new ZipFile(zipUtil.getZipToProcess(file));
            publication = newPublication(zip, username, filename);
            repository.create(publication);
        } catch (ApsZipImporterException e) {
            LOG.error("Failed to import zip {}", e.getMessage(), e);
            return Response.serverError().entity(UploadResponse.error(e.getMessage())).build();
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to create import job {}, {}", filename, e.getMessage(), e);
            return Response.serverError().entity(UploadResponse.error(e.getMessage())).build();
        } catch (IOException e) {
            LOG.error("Failed to extract zip file", e);
            return Response.serverError().entity(UploadResponse.error("Failed to extract zip file")).build();
        }

        executor.submit(() -> uploadPublication(publication, zip));
        return Response.accepted(UploadResponse.accepted(publication)).build();
    }

    void uploadPublication(Publication publication, ZipFile zip) {

        try {
            doUploadPublication(publication, zip);
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to save publication status", e);
        }
    }

    private void doUploadPublication(Publication publication, ZipFile zip) throws PublicationRepositoryException {

        try {
            Session session = daemonSession;
            ApsZipImporter apsZipImporter = new ApsZipImporter(session);
            String path = apsZipImporter.importApsZip(zip, publication);
            publication.setState(State.DONE.name());
            publication.setStatedetails(path);
            repository.update(publication);
        } catch (ApsZipImporterException e) {
            LOG.error("Failed to upload publications", e);
            populateErrorInformation(publication, e.getMessage());
            repository.update(publication);
        }
    }

    private void populateErrorInformation(Publication publication, String details) {
        publication.setState(State.FAILED.name());
        publication.setStatedetails(details);
    }

    private Publication newPublication(
            ZipFile zip,
            String username,
            String filename)
            throws ApsZipImporterException {

        Metadata metadata = metadataExtractor.extract(zip);
        Publication publication = new Publication();
        publication.setId(UUID.randomUUID().toString());
        publication.setUsername(username);
        publication.setIsbn(metadata.normalisedIsbn());
        publication.setFilename(filename);
        publication.setTitle(metadata.getTitle());
        publication.setState(State.PENDING.name());
        publication.setStatedetails("Pending");
        publication.setEmbargodate(GregorianCalendar.from(metadata.getPublicationDateWithTimezone()));
        publication.setCreateddate(GregorianCalendar.getInstance());
        publication.setContact(contactEmail(metadata));
        return publication;
    }

    /**
     * The contact email has not always been present in the JSON and so we guard against it being null.
     */
    private String contactEmail(Metadata metadata) {
        if (metadata.getContact() != null) {
            return metadata.getContact().getEmail().trim();
        }
        return "";
    }
}