package scot.gov.www.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class HomePage404Logger implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(HomePage404Logger.class);

    private Set<String> interestingUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Collections.addAll(interestingUrls, "/site/");
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain)
            throws IOException, ServletException {

        filterChain.doFilter(servletRequest, servletResponse);

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (shouldLog(request, response)) {
           log(request, response);
        }
    }

    boolean shouldLog(HttpServletRequest request, HttpServletResponse response) {

        // Log anything that is a request for the home page but is not a 200
        return response.getStatus() != 200 && interestingUrls.contains(request.getRequestURI());
    }

    void log(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("{} {} {} requestHeaders: {} responseHeaders: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                requestHeaders(request),
                responseHeaders(response));
    }

    @Override
    public void destroy() {
        // destroy
    }

    String requestHeaders(HttpServletRequest request) {
        Enumeration<String> headerEnum = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>(0);
        while (headerEnum.hasMoreElements()) {
            String headerName = headerEnum.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers.toString();
    }

    String responseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>(0);
        for (String headerName : response.getHeaderNames()) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers.toString();
    }
}
