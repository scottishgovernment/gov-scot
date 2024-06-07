package scot.gov.pressreleases;

import org.junit.Test;
import org.mockito.Mockito;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.BadRequestException;
import java.util.Collections;

public class ValueListImporterTest {

    ValueListImporter sut = new ValueListImporter();

    @Test(expected = BadRequestException.class)
    public void importValueListRejectsUnrecognised() throws RepositoryException  {
        sut.importValueList(Mockito.mock(Session.class), "unsuported", Collections.emptyMap());
    }


}
