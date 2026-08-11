package scot.gov.www;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Best-effort derivation of a document's public gov.scot URL from its JCR handle path, for
 * logging from repository-level code (scheduled jobs etc.) that runs outside an HST request
 * context, where the site's own link-resolution machinery isn't reachable.
 *
 * <p>Publications and news have a flat slug-based URL unrelated to their JCR path depth
 * (mirroring the rule in {@code scot.gov.www.searchjournal.UrlSource}); every other document is
 * assumed to have a URL that directly mirrors its JCR path under {@link
 * GalleryFolderUtils#DOCUMENTS_PREFIX} (mirroring {@code DirectPathLocator}, the fallback used by
 * {@code GovScotLinkRewriteStrategy} when resolving incoming links). That fallback isn't exact
 * for every content type (e.g. topics and site items sit under their own top-level URL segment
 * but are nested under {@code govscot/topics}/{@code govscot/siteitems} in the JCR tree), so
 * treat the result as a best guess for logging, never as a canonical link.
 */
final class PageUrls {

    private static final String URL_BASE = "https://www.gov.scot/";
    private static final String PUBLICATIONS_SEGMENT = "publications";
    private static final String NEWS_SEGMENT = "news";
    private static final String SLUG_PROPERTY = "govscot:slug";
    private static final String INDEX_SUFFIX = "/index";

    private PageUrls() {
    }

    /**
     * Returns a best-effort public URL for the document at {@code handlePath}, or {@code null}
     * if it's outside {@value GalleryFolderUtils#DOCUMENTS_PREFIX} or anything goes wrong reading
     * the repository.
     */
    static String bestEffortUrl(Session session, String handlePath) {
        if (!handlePath.startsWith(GalleryFolderUtils.DOCUMENTS_PREFIX)) {
            return null;
        }
        String relativePath = handlePath.substring(GalleryFolderUtils.DOCUMENTS_PREFIX.length());
        if (relativePath.endsWith(INDEX_SUFFIX)) {
            relativePath = relativePath.substring(0, relativePath.length() - INDEX_SUFFIX.length());
        }
        String firstSegment = relativePath.contains("/")
                ? relativePath.substring(0, relativePath.indexOf('/'))
                : relativePath;

        if (PUBLICATIONS_SEGMENT.equals(firstSegment) || NEWS_SEGMENT.equals(firstSegment)) {
            String slug = readSlug(session, handlePath);
            if (slug != null) {
                return URL_BASE + firstSegment + "/" + slug + "/";
            }
        }

        return URL_BASE + relativePath + "/";
    }

    private static String readSlug(Session session, String handlePath) {
        try {
            if (!session.nodeExists(handlePath)) {
                return null;
            }
            Node handle = session.getNode(handlePath);
            String name = handle.getName();
            if (!handle.hasNode(name)) {
                return null;
            }
            Node variant = handle.getNode(name);
            return variant.hasProperty(SLUG_PROPERTY) ? variant.getProperty(SLUG_PROPERTY).getString() : null;
        } catch (RepositoryException e) {
            return null;
        }
    }
}
