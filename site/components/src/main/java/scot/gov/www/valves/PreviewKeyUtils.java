package scot.gov.www.valves;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.request.HstRequestContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class PreviewKeyUtils {

    private static final String PREVIEW_KEY = "previewkey";

    private PreviewKeyUtils() {
        // prevent instantiation
    }

    public static Set<String> getPreviewKeys(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            Mount resolvedMount) {
        String previewKey = servletRequest.getParameter(PREVIEW_KEY);
        Set<String> previewKeys = getPreviewKeyCookies(servletRequest);
        if (isNotBlank(previewKey)) {
            addCookieIfNotPresent(servletRequest, servletResponse, resolvedMount, previewKeys, previewKey);
            previewKeys.add(previewKey);
        }

        return previewKeys;
    }

    public static boolean isPreviewMount(HstRequestContext requestContext) {
        Mount resolvedMount = requestContext.getResolvedMount().getMount();
        if (resolvedMount == null) {
            return false;
        }
        return "preview".equals(resolvedMount.getType())
                && endsWith(resolvedMount.getAlias(), "-staging");
    }

    private static void addCookieIfNotPresent(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            Mount resolvedMount,
            Set<String> previewKeys,
            String previewKey) {
        if (!previewKeys.contains(previewKey)) {
            addCookie(servletRequest, servletResponse, resolvedMount, previewKey);
        }
    }

    private static void addCookie(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            Mount resolvedMount,
            String previewKey) {

        Cookie cookie = new Cookie(PREVIEW_KEY, previewKey);
        cookie.setPath(getPath(servletRequest));
        boolean httpOnly = Boolean.parseBoolean(resolvedMount.getProperty("preview-cookie-httponly"));
        boolean secure = Boolean.parseBoolean(resolvedMount.getProperty("preview-cookie-secure"));
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        servletResponse.addCookie(cookie);
    }

    private static String getPath(HttpServletRequest request) {
        final String contextPath = request.getContextPath();
        if (StringUtils.isBlank(contextPath)) {
            return "/";
        }
        return contextPath;
    }

    private static Set<String> getPreviewKeyCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return emptySet();
        }
        Set<String> previewKeys = Arrays.stream(cookies)
                .filter(PreviewKeyUtils::isPreviewKey)
                .map(Cookie::getValue)
                .collect(toSet());
        return new HashSet<>(previewKeys);
    }

    private static boolean isPreviewKey(Cookie cookie) {
        return PREVIEW_KEY.equals(cookie.getName());
    }

}
