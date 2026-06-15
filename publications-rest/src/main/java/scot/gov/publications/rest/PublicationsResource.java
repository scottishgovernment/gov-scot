package scot.gov.publications.rest;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.jcr.Session;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;

import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporter;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.metadata.MetadataExtractor;
import scot.gov.publications.repo.*;
import scot.gov.publications.util.FileUtil;
import scot.gov.publications.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

@Path("/")
public class PublicationsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsResource.class);

    Session daemonSession;

    PublicationRepository repository;

    MetadataExtractor metadataExtractor = new MetadataExtractor();

    ZipUtil zipUtil = new ZipUtil();

    FileUtil fileUtil = new FileUtil();

    // ensure we are only processing one publication at a time
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public PublicationsResource(Session daemonSession, PublicationRepository repository) {
        this.daemonSession = daemonSession;
        this.repository = repository;
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response get(@PathParam("id") String id) {
        LOG.info("GET publication {}", id);
        try {
            Publication publication = repository.get(id);
            if (publication == null) {
                LOG.info("Publication not found: {}", id);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            LOG.info("GET publication {} returning state={}", id, publication.getState());
            return Response.ok(publication).build();
        } catch (Exception e) {
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

        LOG.info("LIST publications page={} size={} title={} isbn={} filename={}", page, size, title, isbn, filename);
        try {
            ListResult result = repository.list(page, size, title, isbn, filename);
            LOG.info("LIST returning {} publications, totalSize={} - building response", result.getPublications().size(), result.getTotalSize());
            Response response = Response.ok(result).build();
            LOG.info("LIST response built successfully");
            return response;
        } catch (Exception e) {
            LOG.error("Failed to list publications", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response uploadPublication(
            @Multipart("file") InputStream fileStream,
            @Multipart("filename") String filename) {

        LOG.info("POST upload filename={}", filename);

        Publication publication;
        ZipFile zip;
        try {
            String username = UserSession.get().getJcrSession().getUserID();
            LOG.info("Upload by user={}", username);
            LOG.info("Saving upload to temp file");
            File file = fileUtil.createTempFile("publication", "zip", fileStream);
            LOG.info("Temp file created: {}", file.getAbsolutePath());
            zip = new ZipFile(zipUtil.getZipToProcess(file));
            LOG.info("Zip opened, extracting metadata");
            publication = newPublication(zip, username, filename);
            LOG.info("Creating publication record id={} isbn={}", publication.getId(), publication.getIsbn());
            repository.create(publication);
            LOG.info("Publication record created id={}", publication.getId());
        } catch (ApsZipImporterException e) {
            LOG.error("Failed to import zip {}", e.getMessage(), e);
            return Response.serverError().entity(UploadResponse.error(e.getMessage())).build();
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to create import job {}, {}", filename, e.getMessage(), e);
            return Response.serverError().entity(UploadResponse.error(e.getMessage())).build();
        } catch (IOException e) {
            LOG.error("Failed to extract zip file", e);
            return Response.serverError().entity(UploadResponse.error("Failed to extract zip file")).build();
        } catch (Exception e) {
            LOG.error("Unexpected error during upload of {}", filename, e);
            return Response.serverError().entity(UploadResponse.error(e.getMessage())).build();
        }

        LOG.info("Submitting background import for id={}", publication.getId());
        executor.submit(() -> uploadPublication(publication, zip));
        LOG.info("Returning 202 Accepted for id={} - building response", publication.getId());
        Response response = Response.accepted(UploadResponse.accepted(publication)).build();
        LOG.info("POST response built successfully for id={}", publication.getId());
        return response;
    }

    void uploadPublication(Publication publication, ZipFile zip) {
        LOG.info("Background import starting for id={}", publication.getId());
        try {
            doUploadPublication(publication, zip);
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to save publication status for id={}", publication.getId(), e);
        } catch (Exception e) {
            LOG.error("Unexpected error in background import for id={}", publication.getId(), e);
        }
    }

    private void doUploadPublication(Publication publication, ZipFile zip) throws PublicationRepositoryException {
        LOG.info("doUploadPublication starting for id={}", publication.getId());
        try {
            Session session = daemonSession;
            ApsZipImporter apsZipImporter = new ApsZipImporter(session);
            LOG.info("Calling importApsZip for id={}", publication.getId());
            String path = apsZipImporter.importApsZip(zip, publication);
            LOG.info("importApsZip completed, path={}", path);
            publication.setState(State.DONE.name());
            publication.setStatedetails(path);
            repository.update(publication);
            LOG.info("Publication id={} marked DONE", publication.getId());
        } catch (ApsZipImporterException e) {
            LOG.error("Failed to import zip for id={}: {}", publication.getId(), e.getMessage(), e);
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
        publication.setEmbargodate(Timestamp.from(metadata.getPublicationDateWithTimezone().toInstant()));
        publication.setCreateddate(new Timestamp(System.currentTimeMillis()));
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
