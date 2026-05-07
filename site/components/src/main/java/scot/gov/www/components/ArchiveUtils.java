package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.hst.AliasRedirectService;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class ArchiveUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveUtils.class);

    private ArchiveUtils() {
        // prevent instantiation
    }

    public static String archiveUrl(HstRequest request) {
        String baseUrl = archiveBaseUrl(request);
        String baseUrlWithNoTrailingSlash =
                baseUrl.endsWith("/")
                        ? StringUtils.substringBeforeLast(baseUrl, "/")
                        : baseUrl;
        return String.format("%s%s", baseUrlWithNoTrailingSlash, request.getPathInfo());
    }

    public static boolean isArchivedUrl(HstRequest request)  {
        AliasRedirectService aliasRedirectService = new AliasRedirectService();
        try {
            Session session = request.getRequestContext().getSession();
            Optional<Redirect> redirect = aliasRedirectService.lookup(session, request.getPathInfo());
            return redirect.isPresent() && redirect.get().isHistoricalUrl();
        } catch (RepositoryException e) {
            LOG.error("Failed to find historical url redirect {}", request.getPathInfo(), e);
            return false;
        }
    }

    private static String archiveBaseUrl(HstRequest request) {
        try {
            Session session = request.getRequestContext().getSession();
            String sql = "SELECT * FROM govscot:PageNotFound WHERE hippostd:state = 'published'";
            QueryResult result = session
                    .getWorkspace()
                    .getQueryManager()
                    .createQuery(sql, Query.SQL)
                    .execute();
            if (result.getNodes().getSize() != 1) {
                throw new HstComponentException("Unexpected number of PageNotFound pages: " + result.getNodes().getSize());
            }
            Node pageNotFoundPage = result.getNodes().nextNode() ;
            return pageNotFoundPage.getProperty("govscot:archiveUrl").getString();
        } catch (RepositoryException e) {
            throw  new HstComponentException("Failed to get archive base url for: " + request.getPathInfo(), e);
        }
    }
}
