package scot.gov.pressreleases;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.Test;

import javax.jcr.RepositoryException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ErrorHandlerTest {

    ErrorHandler sut = new ErrorHandler();

    @Test
    public void jsonParsingExceptionReturns400() {
        assertEquals(400, sut.toResponse(new JsonParseException("")).getStatus());
    }

    @Test
    public void jcrExceptionReturns500() {
        assertEquals(500, sut.toResponse(new RepositoryException("")).getStatus());
    }

    @Test
    public void ioExceptionReturns500() {
        assertEquals(500, sut.toResponse(new IOException("")).getStatus());
    }

}
