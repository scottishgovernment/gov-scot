package scot.gov.www;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;

/**
 * Best-effort derivation of a document's public gov.scot URL from its JCR handle path, for
 * logging from repository-level code (scheduled jobs etc.) that runs outside an HST request
 * context, where the site's own link-resolution machinery isn't reachable.
 *
 * <p>Publications and news have a slug-based URL unrelated to their JCR path depth, with pages
 * nested under a publication taking the form {@code publications/{slug}/pages/{pageName}/}
 * (mirroring the rules in {@code scot.gov.www.searchjournal.UrlSource}); every other document is
 * assumed to have a URL that directly mirrors its JCR path under {@link
 * GalleryFolderUtils#DOCUMENTS_PREFIX} (mirroring {@code DirectPathLocator}, the fallback used by
 * {@code GovScotLinkRewriteStrategy} when resolving incoming links). That fallback isn't exact
 * for every content type (e.g. topics and site items sit under their own top-level URL segment
 * but are nested under {@code govscot/topics}/{@code govscot/siteitems} in the JCR tree), so
 * treat the result as a best guess for logging, never as a canonical link.
 */
final class PageUrls {

    private static final Logger LOG = LoggerFactory.getLogger(PageUrls.class);

    private static final String URL_BASE = "https://www.gov.scot/";
    private static final String PUBLICATIONS_SEGMENT = "publications";
    private static final String NEWS_SEGMENT = "news";
    private static final String PAGES_SEGMENT = "pages";
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
        String[] segments = relativePath.split("/");
        String firstSegment = segments[0];

        if (PUBLICATIONS_SEGMENT.equals(firstSegment) || NEWS_SEGMENT.equals(firstSegment)) {
            // the slug lives on the publication/news document itself, which is the segment
            // just before "pages" for a nested page, or the last segment otherwise
            int pagesIndex = indexOf(segments, PAGES_SEGMENT);
            int slugFolderEnd = pagesIndex >= 0 ? pagesIndex : segments.length;
            String slugHandlePath = GalleryFolderUtils.DOCUMENTS_PREFIX
                    + String.join("/", Arrays.copyOfRange(segments, 0, slugFolderEnd));
            String slug = readSlug(session, slugHandlePath);
            if (slug != null) {
                String url = URL_BASE + firstSegment + "/" + slug + "/";
                if (pagesIndex >= 0 && pagesIndex + 1 < segments.length) {
                    url += PAGES_SEGMENT + "/" + segments[pagesIndex + 1] + "/";
                }
                return url;
            }
        }

        return URL_BASE + relativePath + "/";
    }

    private static int indexOf(String[] segments, String value) {
        for (int i = 0; i < segments.length; i++) {
            if (value.equals(segments[i])) {
                return i;
            }
        }
        return -1;
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
            LOG.error("unexpected error reading slug", e);
            return null;
        }
    }
}
