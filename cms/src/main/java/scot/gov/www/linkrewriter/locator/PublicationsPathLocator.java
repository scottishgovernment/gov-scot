package scot.gov.www.linkrewriter.locator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.Optional;

/**
 * Resolves publication URLs:
 * <ul>
 *   <li>{@code /publications/<slug>} and {@code /publications/<slug>/<sub-path>}</li>
 *   <li>{@code /isbn/<isbn>} and {@code /ISBN/<isbn>} (with optional sub-path)</li>
 * </ul>
 *
 * <p>Sub-paths within a publication are resolved as:
 * <ul>
 *   <li>{@code /pages/<page-slug>} — the named page within the {@code pages} subfolder.</li>
 *   <li>{@code /documents} or {@code /documents/<filename>} — the first document handle
 *       in the {@code documents} subfolder.</li>
 *   <li>Anything else — treated as a ComplexDocument chapter path under the
 *       {@code chapters} subfolder (multi-level, no {@code /chapters/} prefix in the URL).</li>
 * </ul>
 */
public class PublicationsPathLocator implements PathLocatorStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsPathLocator.class);

    private static final String CONTENT_DOCUMENTS_ROOT = SlugLookup.CONTENT_DOCUMENTS_ROOT;

    static final String PUBLICATIONS_PREFIX = "/publications/";
    static final String ISBN_PREFIX         = "/isbn/";

    private static final String PAGES_SUBPATH_PREFIX = "/pages/";
    private static final String DOCUMENTS_SUBPATH    = "/documents";
    private static final String CHAPTERS_SUBFOLDER   = "chapters";
    private static final String INDEX                = "index";

    @Override
    public String name() {
        return "publications";
    }

    @Override
    public Optional<Node> locate(Session session, String path) throws RepositoryException {
        if (path.startsWith(PUBLICATIONS_PREFIX)) {
            return locateBySlug(session, path);
        }
        if (path.startsWith(ISBN_PREFIX) || path.startsWith("/ISBN/")) {
            return locateByIsbn(session, path);
        }
        return Optional.empty();
    }

    // ---- Slug-based ------------------------------------------------------------------

    private Optional<Node> locateBySlug(Session session, String path) throws RepositoryException {
        String remainder = path.substring(PUBLICATIONS_PREFIX.length());
        int slash = remainder.indexOf('/');
        String slug    = slash < 0 ? remainder : remainder.substring(0, slash);
        String subPath = slash < 0 ? ""        : remainder.substring(slash);

        Optional<Node> pubHandle = SlugLookup.findBySlug(session, "publications", slug);
        if (pubHandle.isEmpty()) {
            return Optional.empty();
        }
        if (subPath.isEmpty()) {
            return pubHandle;
        }
        return Optional.ofNullable(resolvePublicationSubPath(session, pubHandle.get(), subPath, path));
    }

    // ---- ISBN-based ------------------------------------------------------------------

    private Optional<Node> locateByIsbn(Session session, String path) throws RepositoryException {
        String isbnPrefix    = path.startsWith(ISBN_PREFIX) ? ISBN_PREFIX : "/ISBN/";
        String isbnRemainder = path.substring(isbnPrefix.length());
        int slash   = isbnRemainder.indexOf('/');
        String isbn    = slash < 0 ? isbnRemainder : isbnRemainder.substring(0, slash);
        String subPath = slash < 0 ? ""            : isbnRemainder.substring(slash);

        Optional<Node> pubHandle = findByIsbn(session, isbn);
        if (pubHandle.isEmpty()) {
            return Optional.empty();
        }
        if (subPath.isEmpty()) {
            return pubHandle;
        }
        return Optional.ofNullable(resolvePublicationSubPath(session, pubHandle.get(), subPath, path));
    }

    /**
     * Finds the {@code hippo:handle} for the publication whose {@code govscot:isbn} property
     * matches {@code rawIsbn} after normalisation (lowercase, whitespace and hyphens stripped).
     */
    private Optional<Node> findByIsbn(Session session, String rawIsbn) throws RepositoryException {
        if (rawIsbn == null || rawIsbn.isEmpty()) {
            return Optional.empty();
        }
        String isbn = rawIsbn.toLowerCase().replaceAll("[\\s\\-]", "");
        String xpath = "/jcr:root" + CONTENT_DOCUMENTS_ROOT
                + "//element(*)[not(@jcr:primaryType='nt:frozenNode')"
                + " and @hippostd:state='published'"
                + " and @govscot:isbn='" + isbn + "']";
        NodeIterator nodes = session.getWorkspace().getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();

        if (!nodes.hasNext()) {
            LOG.warn("PublicationsPathLocator: no publication found for ISBN '{}'", rawIsbn);
            return Optional.empty();
        }
        Node variant = nodes.nextNode();
        if (nodes.hasNext()) {
            LOG.warn("PublicationsPathLocator: multiple publications found for ISBN '{}', using first", rawIsbn);
        }
        Node handle = variant.getParent();
        if (SlugLookup.isHandle(handle)) {
            return Optional.of(handle);
        }
        LOG.warn("PublicationsPathLocator: parent of ISBN '{}' variant is not a handle ({})",
                rawIsbn, handle.getPrimaryNodeType().getName());
        return Optional.empty();
    }

    // ---- Sub-path resolution ---------------------------------------------------------

    private Node resolvePublicationSubPath(Session session, Node indexHandle, String subPath,
                                           String originalPath) throws RepositoryException {
        Node pubFolder = indexHandle.getParent();

        if (subPath.startsWith(PAGES_SUBPATH_PREFIX)) {
            String pageSegment = subPath.substring(PAGES_SUBPATH_PREFIX.length());
            if (pageSegment.contains("/")) {
                LOG.warn("PublicationsPathLocator: sub-path '{}' has too many segments, skipping", originalPath);
                return null;
            }
            String pagePath = pubFolder.getPath() + PAGES_SUBPATH_PREFIX + pageSegment;
            if (!session.nodeExists(pagePath)) {
                LOG.warn("PublicationsPathLocator: sub-path '{}' — page node not found at {}", originalPath, pagePath);
                return null;
            }
            Node page = session.getNode(pagePath);
            if (!SlugLookup.isHandle(page)) {
                LOG.warn("PublicationsPathLocator: sub-path '{}' — node at {} is not a handle", originalPath, pagePath);
                return null;
            }
            return page;
        }

        if (subPath.equals(DOCUMENTS_SUBPATH) || subPath.startsWith(DOCUMENTS_SUBPATH + "/")) {
            return resolveDocumentsSubPath(session, pubFolder, subPath, originalPath);
        }

        // ComplexDocument: chapters are stored under pubFolder/chapters/<multi-level-path>.
        // The /chapters/ segment is absent from the public URL.
        Node byChapter = resolveChapterSubPath(session, pubFolder, subPath, originalPath);
        if (byChapter != null) {
            return byChapter;
        }

        LOG.warn("PublicationsPathLocator: unrecognised sub-path '{}', skipping", originalPath);
        return null;
    }

    private Node resolveDocumentsSubPath(Session session, Node pubFolder, String subPath,
                                          String originalPath) throws RepositoryException {
        String docsPath = pubFolder.getPath() + DOCUMENTS_SUBPATH;
        if (!session.nodeExists(docsPath)) {
            LOG.warn("PublicationsPathLocator: sub-path '{}' — documents folder not found at {}", originalPath, docsPath);
            return null;
        }
        Node docsFolder = session.getNode(docsPath);
        if (subPath.startsWith(DOCUMENTS_SUBPATH + "/")) {
            String filename = subPath.substring(DOCUMENTS_SUBPATH.length() + 1);
            if (docsFolder.hasNode(filename)) {
                Node candidate = docsFolder.getNode(filename);
                if (SlugLookup.isHandle(candidate)) {
                    return candidate;
                }
            }
            LOG.info("PublicationsPathLocator: sub-path '{}' — no handle named '{}' in documents folder, falling back to first document",
                    originalPath, filename);
        }
        Node first = firstDocumentHandle(docsFolder);
        if (first == null) {
            LOG.warn("PublicationsPathLocator: sub-path '{}' — documents folder at {} contains no handles", originalPath, docsPath);
            return null;
        }
        return first;
    }

    /**
     * Resolves {@code subPath} within the {@code chapters} folder of a ComplexDocument.
     * The sub-path maps directly onto the chapter hierarchy — for example,
     * {@code /appendix-defined-terms/definitions-explanation-terms-used-document} maps to
     * {@code pubFolder/chapters/appendix-defined-terms/definitions-explanation-terms-used-document}.
     */
    private Node resolveChapterSubPath(Session session, Node pubFolder, String subPath,
                                       String originalPath) throws RepositoryException {
        if (subPath.contains("..") || subPath.contains("%")) {
            LOG.warn("PublicationsPathLocator: chapter sub-path '{}' contains unsafe characters, skipping", originalPath);
            return null;
        }
        String chaptersPath = pubFolder.getPath() + "/" + CHAPTERS_SUBFOLDER + subPath;
        if (!session.nodeExists(chaptersPath)) {
            return null;
        }
        Node node = session.getNode(chaptersPath);
        if (SlugLookup.isHandle(node)) {
            return node;
        }
        if (node.isNodeType("hippostd:folder") && node.hasNode(INDEX)) {
            return node.getNode(INDEX);
        }
        LOG.warn("PublicationsPathLocator: chapter sub-path '{}' — node at {} is neither a handle nor an indexable folder",
                originalPath, chaptersPath);
        return null;
    }

    private Node firstDocumentHandle(Node documentsFolder) throws RepositoryException {
        NodeIterator it = documentsFolder.getNodes();
        while (it.hasNext()) {
            Node child = it.nextNode();
            if (SlugLookup.isHandle(child)) {
                return child;
            }
        }
        return null;
    }
}
