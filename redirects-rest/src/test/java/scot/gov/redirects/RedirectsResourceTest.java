package scot.gov.redirects;

import com.fasterxml.jackson.annotation.JsonTypeId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.jcr.RepositoryException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 15/11/2022.
 */
public class RedirectsResourceTest {

    @Test
    public void uploadReturns400IfInvalidRedirect() {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        Redirect invalidRedirect = invalidRedirect();

        // ACT
        Response response = sut.upload(singletonList(invalidRedirect));

        // ASSERT
        List<String> violations = (List<String>) response.getEntity();
        assertFalse(violations.isEmpty());
        assertEquals(400, response.getStatus());
    }

    @Test
    public void uploadReturns500IfRepoExceptionThrown() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = exceptionThrowingRedirectsRepo();
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        Redirect validRedirect = validRedirect();

        // ACT
        Response response = sut.upload(singletonList(validRedirect));

        // ASSERT
        assertEquals("Unexpected exception creating redirect", (String) response.getEntity());
        assertEquals(500, response.getStatus());
    }

    @Test
    public void uploadReturns200WhenRedirectsCreated() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        Redirect validRedirect = validRedirect();

        // ACT
        Response response = sut.upload(singletonList(validRedirect));

        // ASSERT
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getReturns200ForSuccess() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        RedirectResult redirectResult = new RedirectResult();
        redirectResult.setChildren(emptyList());
        when(redirectsRepository.list("/path")).thenReturn(new RedirectResult());
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        sut.uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        m.put("path", Collections.singletonList("/path"));
        when(sut.uriInfo.getPathParameters()).thenReturn(m);

        // ACT
        Response response = sut.get();

        // ASSERT
        RedirectResult entity = (RedirectResult) response.getEntity();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getReturns404IfNoRedirects() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        when(redirectsRepository.list("/path")).thenReturn(null);
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        sut.uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        m.put("path", Collections.singletonList("/path"));
        when(sut.uriInfo.getPathParameters()).thenReturn(m);

        // ACT
        Response response = sut.get();

        // ASSERT
        assertEquals(404, response.getStatus());
    }

    @Test
    public void getReturns500ForException() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        when(redirectsRepository.list("/path")).thenThrow(new RepositoryException());
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        sut.uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        m.put("path", Collections.singletonList("/path"));
        when(sut.uriInfo.getPathParameters()).thenReturn(m);

        // ACT
        Response response = sut.get();

        // ASSERT
        assertEquals(500, response.getStatus());
    }

    @Test
    public void deleteReturnsOkIfPathDeleted() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        when(redirectsRepository.deleteRedirect(anyString())).thenReturn(true);
        RedirectsResource sut = new RedirectsResource(redirectsRepository);

        sut.uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        m.put("path", Collections.singletonList("/path"));
        when(sut.uriInfo.getPathParameters()).thenReturn(m);

        // ACT
        Response response = sut.delete();

        // ASSERT
        assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteReturns404IfPathNotFound() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        when(redirectsRepository.deleteRedirect(any())).thenReturn(false);
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        sut.uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        m.put("path", Collections.singletonList("/path"));
        when(sut.uriInfo.getPathParameters()).thenReturn(m);

        // ACT
        Response response = sut.delete();

        // ASSERT
        assertEquals(404, response.getStatus());
    }

    @Test
    public void deleteReturns500IfRepopExceptonThrown() throws RepositoryException {
        // ARRANGE
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        when(redirectsRepository.deleteRedirect(any())).thenThrow(new RepositoryException(""));
        RedirectsResource sut = new RedirectsResource(redirectsRepository);
        sut.uriInfo = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> m = new MultivaluedHashMap<>();
        m.put("path", Collections.singletonList("/path"));
        when(sut.uriInfo.getPathParameters()).thenReturn(m);

        // ACT
        Response response = sut.delete();

        // ASSERT
        assertEquals(500, response.getStatus());
    }

    //
//
//    @DELETE
//    @Path("{path: .+}")
//    @Produces({ MediaType.APPLICATION_JSON })
//    public Response delete() {
//        String path = uriInfo.getPathParameters().getFirst("path");
//        try {
//            boolean deleted = redirectsRepository.deleteRedirect(path);
//            if (deleted) {
//                return Response.status(Response.Status.OK).entity("deleted").build();
//            } else {
//                return Response.status(Response.Status.NOT_FOUND).entity("no redirect found").build();
//            }
//        } catch (RepositoryException e) {
//            LOG.error("Unexpected exception deleting redirect", e);
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("Unexpected exception deleting redirect " + path).build();
//        }
//    }
    Redirect invalidRedirect() {
        Redirect redirect = new Redirect();
        redirect.setTo("invalidto");
        redirect.setFrom("invalidfrom");
        return redirect;
    }

    Redirect validRedirect() {
        Redirect redirect = new Redirect();
        redirect.setTo("/to");
        redirect.setFrom("/from");
        return redirect;
    }

    RedirectsRepository exceptionThrowingRedirectsRepo() throws RepositoryException {
        RedirectsRepository redirectsRepository = Mockito.mock(RedirectsRepository.class);
        doThrow(RepositoryException.class)
                .when(redirectsRepository)
                .createRedirects(anyList());
        return redirectsRepository;
    }
}
