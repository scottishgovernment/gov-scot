package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class SsoFilter extends HttpFilter {

    public static final String SSO_COOKIE_NAME = "sso";

    private final CallbackHandler callbackHandler = new CallbackHandler();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String path = requestURI.substring(contextPath.length());
        if (!path.startsWith("/sso") || !"GET".equals(req.getMethod())) {
            super.doFilter(req, res, chain);
            return;
        }
        String[] splits = path.split("/", 3);
        String action = splits[2];
        switch (action) {
            case "callback":
                callbackHandler.handleRequest(req, res);
                break;
            case "enable":
                enableSSOCookie(req, res);
                break;
            case "disable":
                disableSSOCookie(req, res);
                break;
            case "jwks":
                serveJwks(res);
                break;
            case "login":
                performSSOLogin(req, res);
                break;
            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void serveJwks(HttpServletResponse res) throws IOException, ServletException {
        try {
            OidcConfig oidcConfig = OidcConfig.get();
            res.setContentType("application/json");
            res.getWriter().write(oidcConfig.publicJwks().toString());
        } catch (Exception ex) {
            throw new ServletException("Failed to serve JWKS", ex);
        }
    }

    private void enableSSOCookie(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.addCookie(createSsoCookie(req.isSecure(), true));
        sendRedirect(req, res);
    }

    private void disableSSOCookie(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.addCookie(createSsoCookie(req.isSecure(), false));
        sendRedirect(req, res);
    }

    private void performSSOLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(true);
        s.setAttribute(SsoSessionAttributes.SSO, true);
        // Clear stale credentials from a previous SSO attempt that ended with
        // "user not found". If left in the session, OidcLoginFilter would see
        // them and pass through rather than redirecting to the IdP.
        s.removeAttribute(SsoSessionAttributes.CREDENTIALS);
        sendRedirect(req, res);
    }

    private static void sendRedirect(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setHeader("Cache-Control", "no-cache");
        res.sendRedirect("..");
    }

    private static Cookie createSsoCookie(boolean secure, boolean enable) {
        Cookie cookie = new Cookie(SSO_COOKIE_NAME, Boolean.toString(enable));
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        return cookie;
    }

}
