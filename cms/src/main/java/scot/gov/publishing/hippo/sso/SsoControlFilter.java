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
                setSSOCookie(req, res, true);
                break;
            case "disable":
                setSSOCookie(req, res, false);
                break;
            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void setSSOCookie(HttpServletRequest req, HttpServletResponse res, boolean enable) {
        if (enable) {
            HttpSession s = req.getSession(false);
            if (s != null) {
                s.invalidate();
            }
        } else {
            HttpSession s = req.getSession();
            s.setAttribute("sso", enable);
        }

        Cookie cookie = new Cookie("sso", "false");
        cookie.setSecure(req.isSecure());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);

        res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        res.setHeader("Location", "..");
        res.setHeader("Cache-Control", "no-cache");
        res.addCookie(cookie);
    }

}
