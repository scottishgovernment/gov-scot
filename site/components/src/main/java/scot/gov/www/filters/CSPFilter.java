package scot.gov.www.filters;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CSPFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(CSPFilter.class);

    private String cspPolicy;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.trace("Initialising CSP filter");

        try (InputStream inputStream = CSPFilter.class.getResourceAsStream("/cspPolicy.txt")) {
            String policy = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            cspPolicy = policy
                    .replaceAll("\\s+;", "; ")
                    .replaceAll("\\s+", " ");
        } catch (IOException ex) {
            throw new ServletException("Could not read CSP policy", ex);
        }
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Content-Security-Policy", cspPolicy);

        filterChain.doFilter(servletRequest, response);
    }

    @Override
    public void destroy() {
        // destroy
    }

}
