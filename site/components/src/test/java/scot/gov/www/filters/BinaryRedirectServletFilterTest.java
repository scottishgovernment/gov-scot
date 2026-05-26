package scot.gov.www.filters;

import org.junit.Test;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.hst.AliasRedirectService;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static scot.gov.www.filters.BinaryRedirectServletFilter.X_FORWARDED_HOST;
import static scot.gov.www.filters.BinaryRedirectServletFilter.X_FORWARDED_PROTO;

public class BinaryRedirectServletFilterTest {

    /**
     * If no redirect exists in the repo then the filter chain should be called with the request and response
     * unchanged.
     */
    @Test
    public void requestPassedOnIfNoRedirectInRepo() throws Exception {
        BinaryRedirectServletFilter sut = filter();
        when(sut.aliasRedirectService.lookup(any(), any())).thenReturn(Optional.empty());
        HttpServletRequest request = request("/contextPath", "/this/is/a/path");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        sut.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    /**
     * If the redirect exists but has a blank 'to' URL then the filter chain should be called unchanged.
     */
    @Test
    public void requestPassedOnIfRedirectHasBlankTo() throws Exception {
        BinaryRedirectServletFilter sut = filter();
        when(sut.aliasRedirectService.lookup(any(), any())).thenReturn(Optional.of(redirect("")));
        HttpServletRequest request = request("/contextPath", "/this/is/a/path");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        sut.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    /**
     * If a JCR exception is thrown while acquiring the session it should be treated the same as
     * having no redirect.
     */
    @Test
    public void requestPassedOnIfRepositoryExceptionIsThrown() throws Exception {
        BinaryRedirectServletFilter sut = filter();
        sut.sessionProvider = r -> { throw new RepositoryException("test"); };
        HttpServletRequest request = request("/contextPath", "/binaries/this/is/a/path");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        sut.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
    }

    /**
     * If a redirect exists in the repo then a redirect should be sent with the right url (no forwarding headers).
     */
    @Test
    public void requestRedirectedIfRedirectInRepoWithNullForwardingHeaders() throws Exception {
        BinaryRedirectServletFilter sut = filter();
        String path = "/this/is/a/path";
        String redirectTo = "/redirect/me/here";
        when(sut.aliasRedirectService.lookup(any(), eq("/binaries" + path))).thenReturn(Optional.of(redirect(redirectTo)));

        HttpServletRequest request = request("/contextPath", path);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        sut.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/contextPath/redirect/me/here");
        verify(filterChain, never()).doFilter(request, response);
    }

    /**
     * Empty forwarding headers behave the same as missing ones — use the context path.
     */
    @Test
    public void requestRedirectedIfRedirectInRepoWithEmptyForwardingHeaders() throws Exception {
        BinaryRedirectServletFilter sut = filter();
        String path = "/this/is/a/path";
        String redirectTo = "/redirect/me/here";
        when(sut.aliasRedirectService.lookup(any(), eq("/binaries" + path))).thenReturn(Optional.of(redirect(redirectTo)));

        HttpServletRequest request = request("/contextPath", path, "", "");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        sut.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/contextPath/redirect/me/here");
        verify(filterChain, never()).doFilter(request, response);
    }

    /**
     * When forwarding headers are present the redirect URL is constructed from them.
     */
    @Test
    public void requestRedirectedIfRedirectInRepoWithForwardingHeaders() throws Exception {
        BinaryRedirectServletFilter sut = filter();
        String path = "/this/is/a/path";
        String redirectTo = "/redirect/me/here";
        when(sut.aliasRedirectService.lookup(any(), eq("/binaries" + path))).thenReturn(Optional.of(redirect(redirectTo)));

        HttpServletRequest request = request("/contextPath", path, "https", "www.gov.scot");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        sut.doFilter(request, response, filterChain);

        verify(response).sendRedirect("https://www.gov.scot/redirect/me/here");
        verify(filterChain, never()).doFilter(request, response);
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    BinaryRedirectServletFilter filter() throws RepositoryException {
        BinaryRedirectServletFilter sut = new BinaryRedirectServletFilter();
        Session session = mock(Session.class);
        sut.sessionProvider = r -> session;
        sut.aliasRedirectService = mock(AliasRedirectService.class);
        return sut;
    }

    HttpServletRequest request(String contextPath, String path) {
        return request(contextPath, path, null, null);
    }

    HttpServletRequest request(String contextPath, String path, String forwardingProto, String forwardingHost) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn(path);
        when(request.getContextPath()).thenReturn(contextPath);
        when(request.getHeader(eq(X_FORWARDED_PROTO))).thenReturn(forwardingProto);
        when(request.getHeader(eq(X_FORWARDED_HOST))).thenReturn(forwardingHost);
        return request;
    }

    Redirect redirect(String to) {
        Redirect r = new Redirect();
        r.setTo(to);
        return r;
    }
}
