package scot.gov.www.filters;

import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;
import static scot.gov.www.components.RedirectComponent.GOVSCOT_URL;
import static scot.gov.www.filters.BinaryRedirectServletFilter.X_FORWARDED_HOST;
import static scot.gov.www.filters.BinaryRedirectServletFilter.X_FORWARDED_PROTO;

public class BinaryRedirectServletFilterTest {

    /**
     * If no redirect exists in the repo then the filter chain should be called with the request and response
     * unchanged.
     */
    @Test
    public void requestPassedOnIfNoRedirectInRepo() throws Exception {
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        Session session = sessionWithNoNodes();
        sut.sessionProvider = request -> session;
        HttpServletRequest request = request("/contextPath", "/binaries/this/is/a/path/govscot:document");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // ACT
        sut.doFilter(request, response, filterChain);

        // ASSERT
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    /**
     * If a redirect node exists in the repo but has not url set then the filter chain should be called with the
     * request and response unchanged.
     */
    @Test
    public void requestPassedOnIfRedirectNodeHasNoUrlSet() throws Exception {
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        HttpServletRequest request = request("/contextPath", "/binaries/this/is/a/path/govscot:document");
        HttpServletResponse response = mock(HttpServletResponse.class);
        String redirectPath = "/redirect/me/here";
        Node redirectNode = mock(Node.class);
        Session session = sessionWithNode(BinaryRedirectServletFilter.jcrLookupPath(request), redirectPath, redirectNode);
        sut.sessionProvider = anyrequest -> session;

        FilterChain filterChain = mock(FilterChain.class);

        // ACT
        sut.doFilter(request, response, filterChain);

        // ASSERT
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    /**
     * If a jcr exception is thrown looking up the redirect then it should be treeated the same as if no redirect exists.
     */
    @Test
    public void requestPassedOnIfRepositoryExceptionIsThrown() throws Exception {
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        Session session = exceptionThrowingSession();
        sut.sessionProvider = request -> session;
        HttpServletRequest request = request("/contextPath", "/binaries/this/is/a/path/govscot:document");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // ACT
        sut.doFilter(request, response, filterChain);

        // ASSERT
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    /**
     * If a redirect exists in the repo then a redirect should be sent with the right url.
     */
    @Test
    public void requestRedirectedIfRedirectInRepoWithNullForwardingHeaders() throws Exception {
        // ARRANGE
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        String path = "/binaries/this/is/a/path";
        String redirectPath = "/redirect/me/here";

        HttpServletRequest request = request("/contextPath", path);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Node redirectNode = redirectNode(redirectPath);
        Session session = sessionWithNode(BinaryRedirectServletFilter.jcrLookupPath(request), redirectPath, redirectNode);
        sut.sessionProvider = anyRequest -> session;

        // ACT
        sut.doFilter(request, response, filterChain);

        // ASSERT
        verify(response).sendRedirect("/contextPath/redirect/me/here");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void requestRedirectedIfRedirectInRepoWithEmptyForwardingHeaders() throws Exception {
        // ARRANGE
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        String path = "/binaries/this/is/a/path";
        String redirectPath = "/redirect/me/here";

        HttpServletRequest request = request("/contextPath", path, "", "");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Node redirectNode = redirectNode(redirectPath);
        Session session = sessionWithNode(BinaryRedirectServletFilter.jcrLookupPath(request), redirectPath, redirectNode);
        sut.sessionProvider = anyRequest -> session;

        // ACT
        sut.doFilter(request, response, filterChain);

        // ASSERT
        verify(response).sendRedirect("/contextPath/redirect/me/here");
        verify(filterChain, never()).doFilter(request, response);
    }


    /**
     * If a redirect exists in the repo then a redirect should be sent with the right url for the forwarding headers that have been set.
     *
     * Note that this test is not trying to test the correctness of getUrl it jcrLookupPath - these have their owqn tests.
     */
    @Test
    public void requestRedirectedIfRedirectInRepoWithForwardingHeaders() throws Exception {
        // ARRANGE
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        String path = "/binaries/this/is/a/path";
        String redirectPath = "/redirect/me/here";

        HttpServletRequest request = request("/contextPath", path, "https", "www.gov.scot");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Node redirectNode = redirectNode(redirectPath);
        Session session = sessionWithNode(BinaryRedirectServletFilter.jcrLookupPath(request), redirectPath, redirectNode);
        sut.sessionProvider = anyRequest -> session;

        // ACT
        sut.doFilter(request, response, filterChain);

        // ASSERT
        verify(response).sendRedirect("https://www.gov.scot/redirect/me/here");
        verify(filterChain, never()).doFilter(request, response);
    }

    HttpServletRequest request(String contextPath, String path) {
        return request(contextPath, path, null, null);
    }

    HttpServletRequest request(String contextPath, String path, String forwardingProto,  String forwardingHost) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn(path);
        when(request.getContextPath()).thenReturn(contextPath);
        when(request.getHeader(eq(X_FORWARDED_PROTO))).thenReturn(forwardingProto);
        when(request.getHeader(eq(X_FORWARDED_HOST))).thenReturn(forwardingHost);
        return request;
    }

    Node redirectNode(String url) throws RepositoryException {
        Node redirectNode = mock(Node.class);
        Property urlProperty = mock(Property.class);
        when(urlProperty.getString()).thenReturn(url);
        when(redirectNode.hasProperty(GOVSCOT_URL)).thenReturn(true);
        when(redirectNode.getProperty(GOVSCOT_URL)).thenReturn(urlProperty);
        return redirectNode;
    }

    Session sessionWithNode(String jcrPath, String redirectPath, Node node) throws RepositoryException {
        Session session = mock(Session.class);
        when(session.nodeExists(jcrPath)).thenReturn(true);
        when(session.getNode(jcrPath)).thenReturn(node);
        return session;
    }

    Session sessionWithNoNodes() throws RepositoryException {
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenReturn(false);
        return session;
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenThrow(new RepositoryException("exceptionthrowingSession"));
        return session;
    }
}
