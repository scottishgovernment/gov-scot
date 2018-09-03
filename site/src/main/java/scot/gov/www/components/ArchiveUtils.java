package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.HstResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class ArchiveUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveUtils.class);

    private ArchiveUtils() {
        // prevent instantiation
    }

    /**
     * Redirect to the 'Archive'.
     *
     * While beta still exists pages will be sent to the live gov.scot site.  When the new site goes live the redirects
     * will all go to ww2.gov.scot.
     */
    public static void sendArchiveRedirect(String path, HstRequest request, HstResponse response) {

        String archiveUrl;

        if (request.getHeader("Host").endsWith("beta.gov.scot")) {
            archiveUrl = String.format("https://www.gov.scot%s", path);
        } else {
            archiveUrl = String.format("https://www2.gov.scot%s", path);
        }
        LOG.info("Redirecting to archive {} -> {}", path, archiveUrl);
        HstResponseUtils.sendRedirect(request, response, archiveUrl);
    }

    public static boolean isArchivedUrl(HstRequest request)  {
        try {
            Session session = request.getRequestContext().getSession();
            String path = String.format("/content/redirects/HistoricalUrls%s", request.getPathInfo());
            if (path.endsWith("/")) {
                path = StringUtils.substringBeforeLast(path, "/");
            }
            return session.nodeExists(path);
        } catch (RepositoryException e) {
            LOG.error("Failed to find publications redirect {}", request.getPathInfo(), e);
            return false;
        }
    }
}
