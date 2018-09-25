package scot.gov.www.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class CSPFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(CSPFilter.class);

    private String cspPolicy;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        LOG.info("CSP header added to the response");

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Content-Security-Policy", cspPolicy);

        filterChain.doFilter(servletRequest, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Creating CSP string");

        InputStream inputStream = CSPFilter.class.getResourceAsStream("/cspPolicy.txt"); // src/main/resources
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(inputStream, writer, "UTF-8");
            cspPolicy = writer.toString().replaceAll("\\s+", " ");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        // destroy
    }
}
