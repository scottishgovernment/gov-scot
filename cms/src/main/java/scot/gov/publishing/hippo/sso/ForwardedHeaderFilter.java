package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Adds a wrapper to requests to reflect values in X-Forwarded-* headers in
 * request methods.
 * <p>
 * Spring Security uses certain request methods to determine the URL that the
 * user is using to access the application, and specifically to construct the
 * SAML Assertion Consumer Service (ACS) URL, where authentication tokens
 * should be POSTed. The ACS URL is constructed by calling request methods
 * overridden by this filter from the Spring Security SAML2 plugin, namely
 * from RelyingPartyRegistrationPlaceholderResolvers.
 * <p>
 * This filter is similar in purpose to the Spring filter with a similar name,
 * see {@link org.springframework.web.filter.ForwardedHeaderFilter}. However,
 * this filter does not remove the X-Forwarded-* headers. These are used by
 * Bloomreach.
 * <p>
 * @see org.springframework.web.filter.ForwardedHeaderFilter
 * @see org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationPlaceholderResolvers
 */
public class ForwardedHeaderFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new RequestWrapper(request), response);
    }

    static class RequestWrapper extends HttpServletRequestWrapper {
        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public boolean isSecure() {
            return "https".equalsIgnoreCase(this.getScheme());
        }

        @Override
        public String getRequestURI() {
            String requestURI = super.getRequestURI();
            if (super.getHeader("x-forwarded-host") == null) {
                return requestURI;
            }
            return requestURI;//requestURI.substring(requestURI.indexOf('/', 1));
        }

        @Override
        public String getScheme() {
            String forwardedProto = super.getHeader("x-forwarded-proto");
            if (forwardedProto == null) {
                return super.getScheme();
            }
            return forwardedProto;
        }

        @Override
        public String getServerName() {
            String forwardedHost = super.getHeader("x-forwarded-host");
            if (forwardedHost != null) {
                return forwardedHost;
            }
            return super.getServerName();
        }

        @Override
        public int getServerPort() {
            String forwardedPort = super.getHeader("x-forwarded-port");
            if (forwardedPort != null) {
                try {
                    return Integer.parseInt(forwardedPort);
                } catch (NumberFormatException ex) {
                    return super.getServerPort();
                }
            }
            String forwardedProto = super.getHeader("x-forwarded-proto");
            if (forwardedProto == null) {
                return super.getServerPort();
            }
            return switch (forwardedProto) {
                case "http" -> 80;
                case "https" -> 443;
                default -> super.getServerPort();
            };
        }

    }

}
