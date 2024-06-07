package scot.gov.pressreleases;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.pressreleases.domain.ImporterStatus;
import scot.gov.pressreleases.domain.LockStatus;
import scot.gov.pressreleases.domain.PressRelease;

import javax.jcr.*;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Rest resource to support the press release importer
 **/
public class PressReleaseResource {

    private static final Logger LOG = LoggerFactory.getLogger(PressReleaseResource.class);

    static final String LOCK_PATH = "/content/importers";

    static final String LOCK_OWNER= "pressreleaseimporter";

    Session session;

    PressReleaseImporter importer = new PressReleaseImporter();

    ImporterStatusUpdater importerStatusUpdater = new ImporterStatusUpdater();

    public PressReleaseResource(Session session) {
        this.session = session;
    }

    @GET
    @Path("/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    public LockStatus releaseLock() throws RepositoryException {
        LockManager lockManager = session.getWorkspace().getLockManager();
        lockManager.unlock(LOCK_PATH);
        session.save();
        return LockStatus.status("unlocked");
    }

    @GET
    @Path("/lock")
    @Produces(MediaType.APPLICATION_JSON)
    public LockStatus lock() throws RepositoryException {
        ensureImporterNodeExists();

        LockStatus status = unlockExpiredLock();
        if (status != null) {
            return status;
        }

        try {
            LockManager lockManager = session.getWorkspace().getLockManager();
            Lock lock = lockManager.lock(LOCK_PATH, false, false, lockDuration(), LOCK_OWNER);
            session.save();
            return LockStatus.status("locked", lock.getLockToken(), lock.getSecondsRemaining());
        } catch (LockException e) {
            LOG.error("Already locked", e);
            return LockStatus.status("already locked");
        }
    }

    /**
     * Get the last time this importer ran
     */
    @GET
    @Path("/status/{importer}")
    @Produces(MediaType.APPLICATION_JSON)
    public ImporterStatus importerStatus(@PathParam("importer") String importer, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        ImporterStatus status = importerStatusUpdater.getStatus(importer, session);
        if (status == null) {
            throw new NotFoundException();
        }
        return status;
    }

    /**
     * Update the last run time of an importer
     */
    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ImporterStatus updateLastRunTimes(ImporterStatus status, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        return importerStatusUpdater.setStatus(status, session);
    }

    @POST
    @Path("/valuelist/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result news(Map<String, String> values, @PathParam("name") String name, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        ValueListImporter valueListImporter = new ValueListImporter();
        valueListImporter.importValueList(session, name, values);
        return new Result("done");
    }

    /**
     * Publish a press releavse, will update an existing one or publish a new one
     */
    @POST
    @Path("/news")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result news(PressRelease pressRelease, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        String location = importer.importPressRelease(pressRelease, session);
        return new Result(location);
    }

    /**
     * Publish a correspondence, will update an existing one or publish a new one
     */
    @POST
    @Path("/correspondence")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result correspondence(PressRelease pressRelease, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        String location = importer.importCorrespondence(pressRelease, session);
        return new Result(location);
    }

    /**
     * Publish a speech / statement, will update an existing one or publish a new one
     */
    @POST
    @Path("/speech")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result speech(PressRelease pressRelease, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        String location = importer.importSpeech(pressRelease, session);
        return new Result(location);
    }

    @PUT
    @Path("/deleteNews/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result unpublishNews(@PathParam("id") String id, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);

        String result = importer.deleteNews(id, session);
        if (result == null) {
            throw new NotFoundException();
        }
        return new Result("done");
    }

    @PUT
    @Path("/deletePublication/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result unpublishPublication(@PathParam("id") String id, @QueryParam("locktoken") String locktoken) throws RepositoryException {
        assertLockToken(locktoken);
        String result = importer.deletePublication(id, session);
        if (result == null) {
            throw new NotFoundException();
        }
        return new Result("done");
    }

    void ensureImporterNodeExists() throws RepositoryException {
        if (session.nodeExists(LOCK_PATH)) {
            return;
        }
        // this is for the benefit of running locally where the importers node does not exist
        Node content = session.getNode("/content");
        Node importers = content.addNode("importers", "nt:unstructured");
        importers.addMixin("mix:lockable");
        session.save();
    }

    LockStatus unlockExpiredLock() throws RepositoryException {
        LockManager lockManager = session.getWorkspace().getLockManager();
        if (!lockManager.isLocked(LOCK_PATH)) {
            return null;
        }

        Lock lock = lockManager.getLock(LOCK_PATH);
        if (!lock.isLive()) {
            // unlock this since it has expired
            lockManager.unlock(LOCK_PATH);
            return null;
        }

        return LockStatus.status("already locked", null, lock.getSecondsRemaining());
    }

    long lockDuration() {
        return TimeUnit.MINUTES.toSeconds(5);
    }

    void assertLockToken(String lockToken) throws RepositoryException {
        LockManager lockManager = session.getWorkspace().getLockManager();
        Lock lock = lockManager.getLock(LOCK_PATH);
        if (!lock.isLive()) {
            throw new LockException("Lock is no longer live: " + lockToken);
        }
        if (!StringUtils.equals(lock.getLockToken(), lockToken)) {
            throw new LockException("Wrong lock token: " + lockToken + " != " + lock.getLockToken());
        }
    }

    /**
     * Class to represent the result.
     */
    public static class Result {

        String path;

        public Result(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

    }

}
