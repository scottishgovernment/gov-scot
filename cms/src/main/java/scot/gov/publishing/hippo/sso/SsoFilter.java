package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class SsoFilter extends HttpFilter {

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
            case "login":
                performSSOLogin(req, res);
                break;
            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
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
        HttpSession s = req.getSession(false);
        if (s != null) {
            s.invalidate();
        }
        s = req.getSession(true);
        s.setAttribute(OIDCLoginFilter.SSO_ATTR_NAME, true);
        sendRedirect(req, res);
    }

    private static void sendRedirect(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setHeader("Cache-Control", "no-cache");
        res.sendRedirect("..");
    }

    private static Cookie createSsoCookie(boolean secure, boolean enable) {
        Cookie cookie = new Cookie("sso", Boolean.toString(enable));
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        return cookie;
    }

}
