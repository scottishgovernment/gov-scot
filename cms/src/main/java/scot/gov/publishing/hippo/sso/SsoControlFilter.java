package scot.gov.publishing.hippo.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class SsoControlFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String path = requestURI.substring(contextPath.length());
        if (!path.startsWith("/sso")) {
            super.doFilter(req, res, chain);
            return;
        }
        String[] splits = path.split("/", 3);
        String action = splits[2];
        switch (action) {
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

    private void enableSSOCookie(HttpServletRequest req, HttpServletResponse res) {
        performSSOLogin(req, res);
    }

    private void disableSSOCookie(HttpServletRequest req, HttpServletResponse res) {
        HttpSession s = req.getSession();
        s.setAttribute("sso", false);
        sendResponse(req, res, false);
    }

    private void performSSOLogin(HttpServletRequest req, HttpServletResponse res) {
        HttpSession s = req.getSession(false);
        if (s != null) {
            s.invalidate();
        }
        sendResponse(req, res, true);
    }

    private static void sendResponse(HttpServletRequest req, HttpServletResponse res, boolean enable) {
        Cookie cookie = createSsoCookie(req.isSecure(), enable);
        res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        res.setHeader("Location", "..");
        res.setHeader("Cache-Control", "no-cache");
        res.addCookie(cookie);
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
