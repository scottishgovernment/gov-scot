package scot.gov.publications.rest;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;

import org.hippoecm.frontend.session.UserSession;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import scot.gov.publications.ApsZipImporter;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.ImportResult;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.metadata.MetadataExtractor;
import scot.gov.publications.repo.*;
import scot.gov.publications.storage.LocalFilePublicationStorage;
import scot.gov.publications.storage.PublicationStorage;
import scot.gov.publications.storage.PublicationStorageException;
import scot.gov.publications.storage.S3PublicationStorage;
import scot.gov.publications.util.FileUtil;
import scot.gov.publications.util.ZipUtil;
import scot.gov.publishing.jcr.FeatureFlag;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

@Path("/")
public class PublicationsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsResource.class);

    private static final String MODULE_PATH = "/hippo:configuration/hippo:modules/publications-rest";

    // name of the node under /content/featureflags that controls whether this API responds at all
    private static final String FEATURE_FLAG_NAME = "PublicationImporterPerspective";

    Session daemonSession;

    PublicationRepository repository;

    MetadataExtractor metadataExtractor = new MetadataExtractor();

    ZipUtil zipUtil = new ZipUtil();

    FileUtil fileUtil = new FileUtil();

    S3PublicationStorage s3Storage = new S3PublicationStorage();

    LocalFilePublicationStorage localStorage = new LocalFilePublicationStorage();

    // ensure we are only processing one publication at a time
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public PublicationsResource(Session daemonSession, PublicationRepository repository) {
        this.daemonSession = daemonSession;
        this.repository = repository;
    }

    /**
     * The publication-importer group is granted the restuser role on this module's own repository node
     * (see configuration/domains/publications-rest.yaml) - this checks the caller actually holds it,
     * since ManagedUserSessionInvoker only authenticates the CMS session, it does not authorize it.
     */
    private boolean isAuthorized(Session session) throws RepositoryException {
        return session.hasPermission(MODULE_PATH, RepositoryJaxrsService.HIPPO_REST_PERMISSION);
    }

    private boolean isEnabled(Session session) throws RepositoryException {
        return new FeatureFlag(session, FEATURE_FLAG_NAME).isEnabled();
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response get(@PathParam("id") String id) {
        LOG.debug("GET publication {}", id);
        try {
            Session session = UserSession.get().getJcrSession();
            if (!isEnabled(session)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (!isAuthorized(session)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            Publication publication = repository.get(id);
            if (publication == null) {
                LOG.debug("Publication not found: {}", id);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            LOG.debug("GET publication {} returning state={}", id, publication.getState());
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
            @DefaultValue("") @QueryParam("filename") String filename,
            @DefaultValue("") @QueryParam("username") String username,
            @DefaultValue("") @QueryParam("state") String state,
            @DefaultValue("") @QueryParam("sort") String sort,
            @DefaultValue("") @QueryParam("dir") String dir) {

        LOG.debug("LIST publications page={} size={} title={} isbn={} filename={} username={} state={} sort={} dir={}",
                page, size, title, isbn, filename, username, state, sort, dir);
        try {
            Session session = UserSession.get().getJcrSession();
            if (!isEnabled(session)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (!isAuthorized(session)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            ListResult result = repository.list(page, size, title, isbn, filename, username, state, sort, dir);
            LOG.debug("LIST returning {} publications, totalSize={}", result.getPublications().size(), result.getTotalSize());
            return Response.ok(result).build();
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
        ZipFile zip = null;
        File file = null;
        File fileToProcess = null;
        try {
            Session jcrSession = UserSession.get().getJcrSession();
            if (!isEnabled(jcrSession)) {
                return Response.status(Response.Status.NOT_FOUND).entity(UploadResponse.error("Not found")).build();
            }
            if (!isAuthorized(jcrSession)) {
                LOG.warn("Unauthorized publications-importer upload attempt by user={}", jcrSession.getUserID());
                return Response.status(Response.Status.FORBIDDEN).entity(UploadResponse.error("Not authorized")).build();
            }
            String username = jcrSession.getUserID();
            LOG.info("Upload by user={} filename={}", username, filename);
            file = fileUtil.createTempFile("publication", "zip", fileStream);
            LOG.info("Temp file created: {} filename={}", file.getAbsolutePath(), filename);
            String checksum = fileUtil.hash(file);
            LOG.info("Checksum computed: {} filename={}", checksum, filename);
            archivePublication(filename, checksum, file);
            fileToProcess = zipUtil.getZipToProcess(file);
            zip = new ZipFile(fileToProcess);
            publication = newPublication(zip, username, filename, checksum);
            LOG.info("Publication upload received: isbn={} filename={} checksum={} id={}",
                    publication.getIsbn(), filename, checksum, publication.getId());
            repository.create(publication);
            LOG.info("Publication record created id={}", publication.getId());
        } catch (ApsZipImporterException e) {
            LOG.error("Failed to import zip {}", e.getMessage(), e);
            return failedUpload(zip, file, fileToProcess, e.getMessage());
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to create import job {}, {}", filename, e.getMessage(), e);
            return failedUpload(zip, file, fileToProcess, e.getMessage());
        } catch (PublicationStorageException e) {
            LOG.error("Failed to archive uploaded zip {}, {}", filename, e.getMessage(), e);
            return failedUpload(zip, file, fileToProcess, "Failed to store uploaded zip");
        } catch (IOException e) {
            LOG.error("Failed to extract zip file", e);
            return failedUpload(zip, file, fileToProcess, "Failed to extract zip file");
        } catch (Exception e) {
            LOG.error("Unexpected error during upload of {}", filename, e);
            return failedUpload(zip, file, fileToProcess, e.getMessage());
        }

        LOG.info("Submitting background import for id={}", publication.getId());
        ZipFile submittedZip = zip;
        File[] tempFiles = { file, fileToProcess };
        executor.submit(() -> uploadPublication(publication, submittedZip, tempFiles));
        return Response.accepted(UploadResponse.accepted(publication)).build();
    }

    /**
     * Shared tail end of every upload-failure branch in uploadPublication() - cleans up the zip and
     * its temp file(s) (which were never handed off to the background executor on this path) and
     * builds the error response, so each catch block only needs its own specific log line.
     */
    private Response failedUpload(ZipFile zip, File file, File fileToProcess, String message) {
        closeAndDelete(zip, file, fileToProcess);
        return Response.serverError().entity(UploadResponse.error(message)).build();
    }

    /**
     * Closes the zip (if open) and deletes every temp file backing it, deduplicated since
     * getZipToProcess() may have returned the same File it was given (common case) rather than a
     * newly-extracted one (nested-zip case) - without the dedup, the second delete() would just be
     * a harmless no-op, but this avoids logging a spurious failed-to-delete warning for it.
     */
    private void closeAndDelete(ZipFile zip, File... files) {
        if (zip != null) {
            try {
                zip.close();
            } catch (IOException e) {
                LOG.warn("Failed to close zip file", e);
            }
        }
        Set<File> unique = new LinkedHashSet<>();
        for (File f : files) {
            if (f != null) {
                unique.add(f);
            }
        }
        for (File f : unique) {
            if (f.exists() && !f.delete()) {
                LOG.warn("Failed to delete temp file {}", f.getAbsolutePath());
            }
        }
    }

    void uploadPublication(Publication publication, ZipFile zip, File... tempFiles) {
        LOG.info("Background import starting for id={}", publication.getId());
        try {
            publication.setState(State.PROCESSING.name());
            publication.setStatedetails("Processing");
            repository.update(publication);
            doUploadPublication(publication, zip, tempFiles);
        } catch (PublicationRepositoryException e) {
            LOG.error("Failed to save publication status for isbn={} filename={} id={}",
                    publication.getIsbn(), publication.getFilename(), publication.getId(), e);
            closeAndDelete(zip, tempFiles);
        } catch (Exception e) {
            LOG.error("Unexpected error in background import for isbn={} filename={} id={}",
                    publication.getIsbn(), publication.getFilename(), publication.getId(), e);
            closeAndDelete(zip, tempFiles);
        }
    }

    private void doUploadPublication(Publication publication, ZipFile zip, File... tempFiles) throws PublicationRepositoryException {
        try {
            // Import as the uploading CMS user (captured at request time, since this runs on the background
            // executor thread where UserSession.get() is no longer available) so that content is created
            // under that user's own identity, rather than everything being attributed to a shared system
            // account - access to the content tree is granted via that user's own repository permissions.
            Session session;
            try {
                session = daemonSession.impersonate(new SimpleCredentials(publication.getUsername(), new char[0]));
            } catch (RepositoryException e) {
                LOG.error("Failed to impersonate user={} for isbn={} filename={} id={}",
                        publication.getUsername(), publication.getIsbn(), publication.getFilename(), publication.getId(), e);
                populateErrorInformation(publication, "Failed to import publication", e);
                repository.update(publication);
                return;
            }

            try {
                ApsZipImporter apsZipImporter = new ApsZipImporter(session);
                ImportResult result = apsZipImporter.importApsZip(zip, publication);
                publication.setState(State.DONE.name());
                publication.setStatedetails(result.getPath());
                publication.setUrl(result.getPath());
                publication.setDocumentcount(result.getDocumentCount());
                publication.setPagecount(result.getPageCount());
                publication.setEmbargoed(result.isEmbargoed());
                publication.setScheduled(result.isScheduled());
                publication.setPublished(result.isPublished());
                publication.setStacktrace(null);
                repository.update(publication);
                LOG.info("Publication import complete: isbn={} filename={} id={}",
                        publication.getIsbn(), publication.getFilename(), publication.getId());
            } catch (ApsZipImporterException e) {
                LOG.error("Failed to import zip for isbn={} filename={} id={}: {}",
                        publication.getIsbn(), publication.getFilename(), publication.getId(), e.getMessage(), e);
                populateErrorInformation(publication, e.getMessage(), e);
                repository.update(publication);
            } catch (RuntimeException e) {
                LOG.error("Unexpected error importing zip for isbn={} filename={} id={}: {}",
                        publication.getIsbn(), publication.getFilename(), publication.getId(), e.getMessage(), e);
                populateErrorInformation(publication, "Unexpected error: " + e.getMessage(), e);
                repository.update(publication);
            } finally {
                session.logout();
            }
        } finally {
            // runs regardless of outcome above, so the zip and its backing temp file(s) never
            // outlive the import attempt that used them
            closeAndDelete(zip, tempFiles);
        }
    }

    private void populateErrorInformation(Publication publication, String details, Throwable cause) {
        publication.setState(State.FAILED.name());
        publication.setStatedetails(details);
        publication.setStacktrace(ExceptionUtils.getStackTrace(cause));
    }

    /**
     * Durably archive the uploaded zip, keyed by its checksum, using S3 if configured or the local
     * filesystem otherwise.
     */
    private void archivePublication(String filename, String checksum, File file) throws PublicationStorageException {
        PublicationStorage storage = storage();
        LOG.info("Archiving uploaded zip {} (checksum={}) using {}", filename, checksum, storage.describeConnection());
        storage.save(checksum, file);
    }

    private PublicationStorage storage() {
        return s3Storage.isConfigured() ? s3Storage : localStorage;
    }

    private Publication newPublication(
            ZipFile zip,
            String username,
            String filename,
            String checksum)
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
        publication.setChecksum(checksum);
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
