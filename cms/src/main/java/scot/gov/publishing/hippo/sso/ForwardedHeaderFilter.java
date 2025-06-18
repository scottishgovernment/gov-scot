package scot.gov.publishing.hippo.sso;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
            return switch (forwardedProto) {
                case "http" -> 80;
                case "https" -> 443;
                default -> super.getServerPort();
            };
        }

    }

}
