package scot.gov.www.filters;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.aspectj.weaver.Dump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.SecureRandom;
import java.util.Base64;


public class CSPFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(CSPFilter.class);

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    private String cspPolicyTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.trace("Initialising CSP filter");

        try (InputStream inputStream = CSPFilter.class.getResourceAsStream("/cspPolicy.txt")) {
            String policy = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            cspPolicyTemplate = policy
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

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String nonce = generateNonce();
        String cspPolicy = cspPolicyTemplate.replaceAll("<nonce>", nonce);
        response.setHeader("Content-Security-Policy", cspPolicy);
        request.setAttribute("nonce", nonce);
        filterChain.doFilter(servletRequest, response);
    }

    String generateNonce() {
        SecureRandom sr = new SecureRandom();
        byte[] nonceBytes = new byte[16];
        sr.nextBytes(nonceBytes);
        return base64Encoder.encodeToString(nonceBytes);
    }

    @Override
    public void destroy() {
        // destroy
    }

}
