package scot.gov.pressreleases;

import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import org.apache.cxf.annotations.FactoryType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.pressreleases.domain.ImporterStatus;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ImporterStatusUpdaterTest {

    ImporterStatusUpdater sut = new ImporterStatusUpdater();

    @Test
    public void returnNullForUnsupportedImporterName() throws RepositoryException {
        assertNull(sut.getStatus("nosuchiimporter", null));
    }

    @Test
    public void returnNullSettingForUnsupportedImporterName() throws RepositoryException {
        ImporterStatus importerStatus = new ImporterStatus();
        importerStatus.setImporter("nosuchimporter");
        assertNull(sut.setStatus(importerStatus, null));
    }

    @Test
    public void returnImporterStatusWithDefaultDateIfImporterNodeAbsent() throws RepositoryException {
        Session session = sessionNoImporters();
        ImporterStatus status = sut.getStatus("tag-importer", session);
        assertEquals(status.getLastrun(),
                ZonedDateTime.now().minusDays(7).plusMinutes(1).truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void returnImporterStatusWithDefaultDateIfDateAbsent() throws RepositoryException {
        Session session = sessionNoImportDate();
        ImporterStatus status = sut.getStatus("tag-importer", session);
        assertEquals(status.getLastrun(),
                ZonedDateTime.now().minusDays(7).plusMinutes(1).truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void returnImporterStatusIfNodeExsiteWithDate() throws RepositoryException {
        Calendar cal = Calendar.getInstance();
        Session session = sessionWithImportDate(cal);
        ImporterStatus status = sut.getStatus("tag-importer", session);

        Assert.assertEquals(ZonedDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()), status.getLastrun());
    }

    Session sessionNoImporters() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node content = Mockito.mock(Node.class);
        when(content.hasNode(any())).thenReturn(false);
        when(session.getNode("/content")).thenReturn(content);

        Node importers = Mockito.mock(Node.class);
        when(content.addNode(any(), any())).thenReturn(importers);

        Node importer = Mockito.mock(Node.class);
        when(importers.hasNode(any())).thenReturn(false);
        when(importers.addNode(any(), anyString())).thenReturn(importer);
        when(importer.hasProperty(any())).thenReturn(false);
        return session;
    }

    Session sessionNoImportDate() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node content = Mockito.mock(Node.class);
        when(content.hasNode(any())).thenReturn(false);
        when(session.getNode("/content")).thenReturn(content);

        Node importers = Mockito.mock(Node.class);
        when(content.addNode(any(), any())).thenReturn(importers);

        Node importer = Mockito.mock(Node.class);
        when(importers.hasNode(any())).thenReturn(true);
        when(importers.getNode(any())).thenReturn(importer);
        when(importer.hasProperty(any())).thenReturn(false);
        return session;
    }

    Session sessionWithImportDate(Calendar cal) throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node content = Mockito.mock(Node.class);
        when(content.hasNode(any())).thenReturn(false);
        when(session.getNode("/content")).thenReturn(content);

        Node importers = Mockito.mock(Node.class);
        when(content.addNode(any(), any())).thenReturn(importers);

        Node importer = Mockito.mock(Node.class);
        when(importers.hasNode(any())).thenReturn(true);
        when(importers.getNode(any())).thenReturn(importer);
        when(importer.hasProperty(any())).thenReturn(true);
        Property p = Mockito.mock(Property.class);
        when(p.getDate()).thenReturn(cal);
        when(importer.getProperty(any())).thenReturn(p);
        return session;
    }

}
