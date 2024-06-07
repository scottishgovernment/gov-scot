package scot.gov.pressreleases;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.pressreleases.domain.ImporterStatus;
import scot.gov.pressreleases.domain.LockStatus;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.ws.rs.NotFoundException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static scot.gov.pressreleases.PressReleaseResource.LOCK_PATH;

public class PressReleaseResourceTest {

    @Test
    public void lockReturnedIfNoLockExists() throws RepositoryException {
        // ARRANGE
        Session session = session(false, false);
        PressReleaseResource sut = new PressReleaseResource(session);

        // ACT
        LockStatus lockStatus = sut.lock();

        // ASSERT
        assertNotNull(lockStatus.getLockToken());
        assertEquals("locked", lockStatus.getStatus());
    }

    @Test
    public void lockUnlocksExpiredLock() throws RepositoryException{
        // ARRANGE
        Session session = session(true, true);
        PressReleaseResource sut = new PressReleaseResource(session);

        // ACT
        LockStatus lockStatus = sut.lock();

        // ASSERT
        assertNotNull(lockStatus.getLockToken());
        assertEquals("locked", lockStatus.getStatus());
    }

    @Test
    public void cannotGetLockIfUnexpiredLockAlreadyExist() throws RepositoryException{
        // ARRANGE
        Session session = session(true, false);
        PressReleaseResource sut = new PressReleaseResource(session);

        // ACT
        LockStatus lockStatus = sut.lock();

        // ASSERT
        assertNull(lockStatus.getLockToken());
        assertEquals("already locked", lockStatus.getStatus());
    }

    @Test
    public void importerStatusGreenPath() throws RepositoryException {
        // ARRANGE
        Session session = session(false, false);
        PressReleaseResource sut = new PressReleaseResource(session);
        sut.importerStatusUpdater = Mockito.mock(ImporterStatusUpdater.class);
        ImporterStatus importerStatusIn = new ImporterStatus();
        when(sut.importerStatusUpdater.getStatus("press-release-importer", session))
                .thenReturn(importerStatusIn);

        // ACT
        ImporterStatus importerStatus = sut.importerStatus("press-release-importer", "locktoken");

        // ASSERT
        Assert.assertSame(importerStatusIn, importerStatus);
    }

    @Test(expected = NotFoundException.class)
    public void importerStatusNoSuchImporterThrowsNotFoundException() throws RepositoryException {
        // ARRANGE
        Session session = session(false, false);
        PressReleaseResource sut = new PressReleaseResource(session);
        sut.importerStatusUpdater = Mockito.mock(ImporterStatusUpdater.class);
        when(sut.importerStatusUpdater.getStatus("nosuchimporter", session))
                .thenReturn(null);

        // ACT
        ImporterStatus importerStatus = sut.importerStatus("nosuchimporter", "locktoken");

        // ASSERT - see excepted exception
    }

    @Test(expected = LockException.class)
    public void importerStatusWrongLocktokenThrowsLockException() throws RepositoryException {
        // ARRANGE
        Session session = session(true, false, "token1");
        PressReleaseResource sut = new PressReleaseResource(session);

        // ACT
        ImporterStatus importerStatus = sut.importerStatus("press-release-importer", "token2");

        // ASSERT - see excepted exception
    }

    @Test(expected = LockException.class)
    public void importerStatusExpiredLocktokenThrowsLockException() throws RepositoryException {
        // ARRANGE
        Session session = session(true, true);
        PressReleaseResource sut = new PressReleaseResource(session);

        // ACT
        ImporterStatus importerStatus = sut.importerStatus("press-release-importer", "locktoken");

        // ASSERT - see excepted exception
    }

    @Test
    public void unpublishNewsGreenPath() throws RepositoryException {
        // ARRANGE
        Session session = session(true, false);
        PressReleaseResource sut = new PressReleaseResource(session);
        sut.importer = Mockito.mock(PressReleaseImporter.class);
        when(sut.importer.deleteNews(anyString(), same(session))).thenReturn("deletepath");

        // ACT
        PressReleaseResource.Result result = sut.unpublishNews("id", "locktoken");

        // ASSERT
        assertEquals("done", result.getPath());
    }

    @Test(expected = NotFoundException.class)
    public void unpublishNewsThrowsNotFoundException() throws RepositoryException {
        // ARRANGE
        Session session = session(true, false);
        PressReleaseResource sut = new PressReleaseResource(session);
        sut.importer = Mockito.mock(PressReleaseImporter.class);
        when(sut.importer.deleteNews(anyString(), same(session))).thenReturn(null);

        // ACT
        sut.unpublishNews("newsid", "locktoken");

        // ASSERT - see excepted exception
    }

    @Test(expected = NotFoundException.class)
    public void unpublishPublicationThrowsNotFoundException() throws RepositoryException {
        // ARRANGE
        Session session = session(true, false);
        PressReleaseResource sut = new PressReleaseResource(session);
        sut.importer = Mockito.mock(PressReleaseImporter.class);
        when(sut.importer.deletePublication(anyString(), same(session))).thenReturn(null);

        // ACT
        sut.unpublishPublication("id", "locktoken");

        // ASSERT - see excepted exception
    }

    @Test
    public void unpublishPublicationGreenPath() throws RepositoryException {
        // ARRANGE
        Session session = session(true, false);
        PressReleaseResource sut = new PressReleaseResource(session);
        sut.importer = Mockito.mock(PressReleaseImporter.class);
        when(sut.importer.deletePublication(anyString(), same(session))).thenReturn("deletepath");

        // ACT
        PressReleaseResource.Result result = sut.unpublishPublication("id", "locktoken");

        // ASSERT
        assertEquals("done", result.getPath());
    }

    Session session(boolean locked, boolean expired) throws RepositoryException {
        return session(locked, expired, "locktoken");
    }

    Session session(boolean locked, boolean expired, String locktoken) throws RepositoryException {
        Session session = mock(Session.class);
        when(session.nodeExists(LOCK_PATH)).thenReturn(true);

        Workspace workspace = mock(Workspace.class);
        LockManager lockManager = mock(LockManager.class);
        when(lockManager.isLocked(eq(LOCK_PATH))).thenReturn(locked);
        Lock lock = mock(Lock.class);
        when(lock.getLockToken()).thenReturn(locktoken);
        when(lock.isLive()).thenReturn(!expired);
        when(lockManager.getLock(eq(LOCK_PATH))).thenReturn(lock);
        when(lockManager.lock(eq(LOCK_PATH), eq(false), eq(false), anyLong(), anyString())).thenReturn(lock);

        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getLockManager()).thenReturn(lockManager);

        return session;
    }

// lock casers
    // no lock exists, lock is called
    // lock exists, not expirted,   cant get a lock
    // lock exists, expired, lock is called

    // lastRunTimes
    //

//    LockStatus status = unlockExpiredLock();
//        if (status != null) {
//        return status;
//    }
//
//        try {
//        LockManager lockManager = session.getWorkspace().getLockManager();
//        Lock lock = lockManager.lock(LOCK_PATH, false, false, lockDuration(), LOCK_OWNER);
//        session.save();
//        return LockStatus.status("locked", lock.getLockToken(), lock.getSecondsRemaining());
//    } catch (
//    LockException e) {
//        LOG.error("Already locked", e);
//        return LockStatus.status("already locked");
//    }


}
