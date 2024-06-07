package scot.gov.pressreleases;

import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.pressreleases.domain.PressRelease;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationsTest {

    Locations sut = new Locations();

    @Test
    public void newsLocationsReusesExistingLocation() throws RepositoryException {
        // ARRANGE
        PressRelease release = new PressRelease();
        Node node = nodeWithParent("/content/path");
        Session session = sessionWithExternalId(node);

        // ACT
        String location = sut.newsLocation(release, session);

        // ASSERT
        assertEquals("/content/path", location);
    }

    @Test
    public void publicaitonsLocationsReusesExistingLocation() throws RepositoryException {
        // ARRANGE
        PressRelease release = new PressRelease();
        Node node = nodeWithParent("/content/path");
        Session session = sessionWithExternalId(node);

        // ACT
        String location = sut.publicationLocation(release, "speech-statement", session);

        // ASSERT
        assertEquals("/content/path", location);
    }

    @Test
    public void newsLocationAsExpectedIfNoIdExists() throws RepositoryException {
        // ARRANGE
        PressRelease release = new PressRelease();
        release.setTitle("news title");
        release.setDateTime(dateTimeWithYearMonth(2020, 1));
        Session session = sessionWithExternalId(null);

        // ACT
        String location = sut.newsLocation(release, session);

        // ASSERT
        assertEquals("/content/documents/govscot/news/2020/01/news-title", location);
    }

    @Test
    public void speechLocationAsExpectedIfNoIdExists() throws RepositoryException {
        // ARRANGE
        PressRelease release = new PressRelease();
        release.setTitle("pub title");
        release.setDateTime(dateTimeWithYearMonth(2020, 1));
        Session session = sessionWithExternalId(null);

        // ACT
        String location = sut.publicationLocation(release, "speech-statement", session);

        // ASSERT
        assertEquals("/content/documents/govscot/publications/speech-statement/2020/01/pub-title/index", location);
    }

    @Test
    public void corespondenceLocationAsExpectedIfNoIdExists() throws RepositoryException {
        // ARRANGE
        PressRelease release = new PressRelease();
        release.setTitle("pub title");
        release.setDateTime(dateTimeWithYearMonth(2020, 1));
        Session session = sessionWithExternalId(null);

        // ACT
        String location = sut.publicationLocation(release, "corespondence", session);

        // ASSERT
        assertEquals("/content/documents/govscot/publications/corespondence/2020/01/pub-title/index", location);
    }

    ZonedDateTime dateTimeWithYearMonth(int year, int month) {
        return ZonedDateTime.of(year, month,
                1, 0, 0, 0, 0,
                ZoneId.systemDefault());
    }
    Node nodeWithParent(String path) throws RepositoryException {
        Node node = Mockito.mock(Node.class);
        Node parent = Mockito.mock(Node.class);;
        when(node.getParent()).thenReturn(parent);
        when(parent.getPath()).thenReturn(path);
        return node;
    }

    Session sessionWithExternalId(Node node) throws RepositoryException {

        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult result = mock(QueryResult.class);
        NodeIterator it = mock(NodeIterator.class);

        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), any())).thenReturn(query);
        when(query.execute()).thenReturn(result);
        when(result.getNodes()).thenReturn(it);
        if (node != null) {
            when(it.hasNext()).thenReturn(true);
            when(it.nextNode()).thenReturn(node);
        } else {
            when(it.hasNext()).thenReturn(false);
        }
        return session;
    }
}
