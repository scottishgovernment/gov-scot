package scot.gov.www.searchjournal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publishing.searchjournal.JournalEntrySource;
import scot.gov.publishing.searchjournal.SearchJournalEntry;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Gov.scot implementation of JournalEntrySource.
 *
 * Encapsulates all site-specific knowledge: which events and content types are indexed,
 * how to build the public URL for each type, and how to handle folder-move events.
 *
 * URLs are constructed directly from JCR slug properties and node paths. The
 * {@code contentId} field is set to the handle UUID so that the consumer can determine
 * the Funnelback collection independently — collection assignment is the consumer's
 * responsibility, not the journal recording layer's.
 */
public class GovJournalEntrySource implements JournalEntrySource {

    private static final Logger LOG = LoggerFactory.getLogger(GovJournalEntrySource.class);

    public static final String URL_BASE = "https://www.gov.scot/";

    private static final String PAGES = "pages";

    private static final String PUBLICATIONS_URL_SEGMENT = "publications";

    private static final String DOCUMENTS = "documents";

    private static final String PUBLISH_ACTION = "publish";

    private static final String DEPUBLISH_ACTION = "depublish";

    private static final String PUBLISH_INTERACTION = "default:handle:publish";

    private static final String DEPUBLISH_INTERACTION = "default:handle:depublish";

    private static final String MOVE_INTERACTION = "threepane:folder-permissions:moveFolder";

    private static final String PUBLICATIONS_PATH_PREFIX = "/content/documents/govscot/publications";

    private static final String NEWS_PATH_PREFIX = "/content/documents/govscot/news";

    private static final String POLICY_PATH_PREFIX = "/content/documents/govscot/policies";

    private static final int PUBLICATION_FOLDER_DEPTH = 8;

    private final Session session;

    private final HippoUtils hippoUtils = new HippoUtils();

    public GovJournalEntrySource(Session session) {
        this.session = session;
    }

    // --- JournalEntrySource interface ---

    @Override
    public List<SearchJournalEntry> entriesForEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (!event.success()) {
            return Collections.emptyList();
        }

        if (!Strings.CS.startsWithAny(event.subjectPath(), PUBLICATIONS_PATH_PREFIX, NEWS_PATH_PREFIX, POLICY_PATH_PREFIX)) {
            return Collections.emptyList();
        }

        if (event.interaction().equals(MOVE_INTERACTION)) {
            return entriesForMoveEvent(event);
        }

        if (!isPublishOrDepublish(event)) {
            return Collections.emptyList();
        }

        // scheduled publishes have arguments; the real event has none
        if (event.arguments() != null) {
            return Collections.emptyList();
        }

        Node handle = session.getNodeByIdentifier(event.subjectId());
        if (isExcludedPage(handle)) {
            return Collections.emptyList();
        }

        return entriesForHandle(handle, getEventAction(event));
    }

    // --- Event routing ---

    List<SearchJournalEntry> entriesForMoveEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (event.arguments().contains(DOCUMENTS)) {
            return entriesForMovedDocumentsFolder(event);
        }
        if (event.arguments().contains(PAGES)) {
            return entriesForMovedPagesFolder(event);
        }
        return Collections.emptyList();
    }

    List<SearchJournalEntry> entriesForMovedDocumentsFolder(HippoWorkflowEvent event) throws RepositoryException {
        Node documentFolder = session.getNodeByIdentifier(event.subjectId());
        if (!isFolderWithPublishedDocs(documentFolder)) {
            return Collections.emptyList();
        }
        NodeIterator it = documentFolder.getNodes();
        while (it.hasNext()) {
            Node documentHandle = it.nextNode();
            if (publishedVariant(documentHandle) != null) {
                List<SearchJournalEntry> entries = new ArrayList<>();
                entries.add(depublishSubfolderEntry(event.subjectPath(), null, false));
                entries.addAll(entriesForHandle(documentHandle, getEventAction(event)));
                return entries;
            }
        }
        return Collections.emptyList();
    }

    List<SearchJournalEntry> entriesForMovedPagesFolder(HippoWorkflowEvent event) throws RepositoryException {
        Node pagesFolder = session.getNodeByIdentifier(event.subjectId());
        if (!isFolderWithPublishedDocs(pagesFolder)) {
            return Collections.emptyList();
        }
        List<SearchJournalEntry> entries = new ArrayList<>();
        NodeIterator it = pagesFolder.getNodes();
        while (it.hasNext()) {
            Node pageHandle = it.nextNode();
            if (publishedVariant(pageHandle) != null) {
                entries.add(depublishSubfolderEntry(event.subjectPath(), pageHandle.getName(), true));
                entries.add(depublishPublicationEntryForSubfolderMove(event.subjectPath()));
                entries.addAll(entriesForHandle(pageHandle, getEventAction(event)));
            }
        }
        return entries;
    }

    // --- Handle-level entry building ---

    List<SearchJournalEntry> entriesForHandle(Node handle, String action) throws RepositoryException {
        Node variant = hippoUtils.getVariant(handle);
        if (variant == null) {
            return Collections.emptyList();
        }

        if (variant.isNodeType("govscot:News")) {
            return Collections.singletonList(newEntry(newsUrl(variant), action, handle.getIdentifier()));
        }

        if (isAnyNodeType(variant, "govscot:Policy", "govscot:PolicyInDetail")) {
            return Collections.singletonList(newEntry(policyUrl(variant), action, handle.getIdentifier()));
        }

        return journalEntriesForPublication(variant, handle, action);
    }

    // --- Publication entry building ---

    List<SearchJournalEntry> journalEntriesForPublication(Node variant, Node handle, String action) throws RepositoryException {
        Node publication = getPublication(variant);
        String publicationHandleId = publication.getParent().getIdentifier();
        SearchJournalEntry entry = newEntry(publicationUrl(publication, variant, action, handle.getPath()), action, publicationHandleId);
        Node publicationFolder = publicationFolder(publication);
        boolean hasPages = hasPages(publication, publicationFolder);
        boolean hasDocuments = hasDocuments(publicationFolder);
        boolean needsDocumentsPage = hasPages && hasDocuments;

        if (isAnyNodeType(variant, "govscot:ComplexDocumentSection")) {
            return Collections.singletonList(entry);
        }

        if (isAnyNodeType(variant, "govscot:PublicationPage")) {
            SearchJournalEntry documentsEntry = documentsEntry(needsDocumentsPage, publication, action);
            SearchJournalEntry publicationEntry = publicationEntry(publication, action);
            if (entry.getUrl().equals(publicationEntry.getUrl())) {
                entry.setAction(publicationEntry.getAction());
            }
            return Arrays.asList(entry, documentsEntry, publicationEntry);
        }

        if (isAnyNodeType(variant, "govscot:DocumentInformation")) {
            SearchJournalEntry documentsEntry = documentsEntry(needsDocumentsPage, publication, action);
            SearchJournalEntry publicationEntry = publicationEntry(publication, action);
            return Arrays.asList(documentsEntry, publicationEntry);
        }

        if (isAnyNodeType(variant, "govscot:ComplexDocument2")) {
            List<SearchJournalEntry> journalEntries = new ArrayList<>();
            journalEntries.add(entry);
            for (Node chapter : getChapters(publicationFolder)) {
                journalEntries.add(publicationChapterEntry(chapter, publication, action));
            }
            if (hasDocuments) {
                journalEntries.add(documentsEntry(hasDocuments, publication, action));
            }
            return journalEntries;
        }

        List<SearchJournalEntry> journalEntries = new ArrayList<>();
        journalEntries.add(entry);
        journalEntries.addAll(addPageEntries(hasPages, publicationFolder, publication, action));
        if (hasDocuments) {
            journalEntries.add(documentsEntry(needsDocumentsPage, publication, action));
        }
        return journalEntries;
    }

    List<SearchJournalEntry> addPageEntries(boolean hasPages, Node publicationFolder, Node publication, String action) throws RepositoryException {
        List<SearchJournalEntry> journalEntries = new ArrayList<>();
        if (hasPages) {
            for (Node page : getPages(publicationFolder)) {
                journalEntries.add(publicationPageEntry(page, publication, action));
            }
        }
        return journalEntries;
    }

    SearchJournalEntry documentsEntry(boolean needsDocumentsPage, Node publication, String action) throws RepositoryException {
        String entryAction = needsDocumentsPage ? PUBLISH_ACTION : DEPUBLISH_ACTION;
        return newEntry(documentsUrl(publication), entryAction, publication.getParent().getIdentifier());
    }

    SearchJournalEntry publicationEntry(Node publication, String action) throws RepositoryException {
        String entryAction = actionDependingOnState(publication, action);
        return newEntry(publicationUrl(publication), entryAction, publication.getParent().getIdentifier());
    }

    SearchJournalEntry publicationPageEntry(Node page, Node publication, String action) throws RepositoryException {
        String entryAction = actionDependingOnState(publication, action);
        return newEntry(publicationPageUrl(publication, page, entryAction), entryAction, publication.getParent().getIdentifier());
    }

    SearchJournalEntry publicationChapterEntry(Node chapter, Node publication, String action) throws RepositoryException {
        String entryAction = actionDependingOnState(publication, action);
        return newEntry(complexDocumentChapterUrl(publication, chapter, entryAction), entryAction, publication.getParent().getIdentifier());
    }

    SearchJournalEntry depublishSubfolderEntry(String oldPath, String pageTitle, boolean isPages) {
        String[] pathSections = oldPath.split("/");
        StringBuilder url = new StringBuilder(URL_BASE).append("publications/")
                .append(pathSections[8]).append('/')
                .append(pathSections[9]).append('/');
        if (isPages) {
            url.append(pageTitle).append("/");
        }
        return newEntry(url.toString(), DEPUBLISH_ACTION);
    }

    SearchJournalEntry depublishPublicationEntryForSubfolderMove(String oldPath) {
        String[] pathSections = oldPath.split("/");
        String url = new StringBuilder(URL_BASE).append("publications/")
                .append(pathSections[8]).append('/')
                .toString();
        return newEntry(url, DEPUBLISH_ACTION);
    }

    String actionDependingOnState(Node publication, String baseAction) throws RepositoryException {
        Node publishedVariant = publishedVariant(publication.getParent());
        return publishedVariant != null ? PUBLISH_ACTION : DEPUBLISH_ACTION;
    }

    // --- URL calculation ---

    String policyUrl(Node node) throws RepositoryException {
        String path = StringUtils.substringAfter(node.getParent().getPath(), "/content/documents/govscot/");
        path = StringUtils.substringBefore(path, "/index");
        return new StringBuilder(URL_BASE).append(path).append('/').toString();
    }

    String newsUrl(Node node) throws RepositoryException {
        return slugUrl("news", node);
    }

    String publicationUrl(Node publication) throws RepositoryException {
        return slugUrl(PUBLICATIONS_URL_SEGMENT, publication);
    }

    String publicationUrl(Node publication, Node variant, String action, String handlePath) throws RepositoryException {
        String pubUrl = publicationUrl(publication);

        if (variant.isNodeType("govscot:Publication")) {
            return pubUrl;
        }

        if (variant.isNodeType("govscot:PublicationPage")) {
            return publicationPageUrl(publication, variant, action);
        }

        if (variant.isNodeType("govscot:DocumentInformation")) {
            return hasPages(publication) ? documentsUrl(pubUrl) : pubUrl;
        }

        if (variant.isNodeType("govscot:ComplexDocumentSection")) {
            String chapterPath = StringUtils.substringAfter(handlePath, "/chapters/");
            return new StringBuilder(pubUrl).append(chapterPath).append('/').toString();
        }

        throw new IllegalArgumentException("Unexpected node type trying to maintain search journal: "
                + variant.getPrimaryNodeType().getName());
    }

    String documentsUrl(Node publication) throws RepositoryException {
        return documentsUrl(publicationUrl(publication));
    }

    String documentsUrl(String publicationUrl) {
        return new StringBuilder(publicationUrl).append("documents").append('/').toString();
    }

    String publicationPageUrl(Node publication, Node page, String action) throws RepositoryException {
        String pubUrl = slugUrl(PUBLICATIONS_URL_SEGMENT, publication);
        boolean confirmAction = PUBLISH_ACTION.equals(action) || "moveFolder".equals(action);
        return isFirstVisiblePage(page, confirmAction) ?
                pubUrl :
                new StringBuilder(pubUrl)
                        .append(PAGES).append('/').append(page.getName()).append('/')
                        .toString();
    }

    String complexDocumentChapterUrl(Node publication, Node chapter, String action) throws RepositoryException {
        String pubUrl = slugUrl(PUBLICATIONS_URL_SEGMENT, publication);
        return new StringBuilder(pubUrl)
                .append(chapter.getParent().getName()).append('/')
                .append(chapter.getName()).append('/')
                .toString();
    }

    String slugUrl(String type, Node node) throws RepositoryException {
        String slug = node.getProperty("govscot:slug").getString();
        return new StringBuilder(URL_BASE)
                .append(type).append('/')
                .append(slug).append('/')
                .toString();
    }

    // --- Filtering ---

    boolean isExcludedPage(Node handle) throws RepositoryException {
        Node variant = hippoUtils.getVariant(handle);
        if (variant == null) {
            return false;
        }
        return isPublicationContentsPage(variant) || isPolicyLatestPage(variant);
    }

    boolean isPublicationContentsPage(Node variant) throws RepositoryException {
        return variant.hasProperty("govscot:contentsPage")
                && variant.getProperty("govscot:contentsPage").getBoolean();
    }

    boolean isPolicyLatestPage(Node variant) throws RepositoryException {
        return variant.isNodeType("govscot:PolicyLatest");
    }

    boolean isPublishOrDepublish(HippoWorkflowEvent event) {
        return PUBLISH_INTERACTION.equals(event.interaction())
                || DEPUBLISH_INTERACTION.equals(event.interaction());
    }

    String getEventAction(HippoWorkflowEvent event) {
        return "moveFolder".equals(event.action()) ? PUBLISH_ACTION : event.action();
    }

    // --- JCR helpers ---

    Node getPublication(Node node) throws RepositoryException {
        if (node.isNodeType("govscot:Publication")) {
            return node;
        }
        Node folder = publicationFolder(node);
        Node handle = folder.getNode("index");
        return hippoUtils.getVariant(handle);
    }

    Node publicationFolder(Node node) throws RepositoryException {
        int depth = StringUtils.countMatches(node.getPath(), "/");
        if (node.isNodeType("hippostd:folder") && depth == PUBLICATION_FOLDER_DEPTH) {
            return node;
        }
        return publicationFolder(node.getParent());
    }

    List<Node> getPages(Node publicationFolder) throws RepositoryException {
        List<Node> pages = new ArrayList<>();
        Node pagesFolder = publicationFolder.getNode(PAGES);
        NodeIterator it = pagesFolder.getNodes();
        while (it.hasNext()) {
            Node pageHandle = it.nextNode();
            if (publishedNonContentPage(pageHandle)) {
                pages.add(pageHandle);
            }
        }
        return pages;
    }

    List<Node> getChapters(Node publicationFolder) throws RepositoryException {
        List<Node> chapters = new ArrayList<>();
        Node chaptersFolder = publicationFolder.getNode("chapters");
        NodeIterator it = chaptersFolder.getNodes();
        while (it.hasNext()) {
            Node chapterFolder = it.nextNode();
            NodeIterator chapterIt = chapterFolder.getNodes();
            while (chapterIt.hasNext()) {
                Node chapterHandle = chapterIt.nextNode();
                if (publishedNonContentPage(chapterHandle)) {
                    chapters.add(chapterHandle);
                }
            }
        }
        return chapters;
    }

    boolean hasPages(Node publication, Node publicationFolder) throws RepositoryException {
        if (publication.isNodeType("govscot:ComplexDocument2")) {
            return true;
        }
        if (!publication.getParent().getParent().hasNode(PAGES)) {
            return false;
        }
        boolean seenFirstPage = false;
        NodeIterator it = publicationFolder.getNode(PAGES).getNodes();
        while (it.hasNext()) {
            Node pageHandle = it.nextNode();
            if (publishedNonContentPage(pageHandle)) {
                if (!seenFirstPage) {
                    seenFirstPage = true;
                } else {
                    return true;
                }
            }
        }
        return seenFirstPage;
    }

    boolean hasPages(Node publication) throws RepositoryException {
        Node publicationFolder = publication.getParent().getParent();
        if (!publicationFolder.hasNode(PAGES)) {
            return false;
        }
        return publicationFolder.getNode(PAGES).getNodes().hasNext();
    }

    boolean hasDocuments(Node publicationFolder) throws RepositoryException {
        if (!publicationFolder.hasNode(DOCUMENTS)) {
            return false;
        }
        Node documentsFolder = publicationFolder.getNode(DOCUMENTS);
        return null != hippoUtils.find(documentsFolder.getNodes(),
                node -> isHandleWithPublishedVariant(node) || isFolderWithPublishedDocs(node));
    }

    boolean isFolderWithPublishedDocs(Node node) throws RepositoryException {
        if (!node.isNodeType("hippostd:folder")) {
            return false;
        }
        return null != hippoUtils.find(node.getNodes(), this::isHandleWithPublishedVariant);
    }

    boolean isHandleWithPublishedVariant(Node node) throws RepositoryException {
        if (!node.isNodeType("hippo:handle")) {
            return false;
        }
        return publishedVariant(node) != null;
    }

    boolean publishedNonContentPage(Node handle) throws RepositoryException {
        return publishedVariant(handle) != null && !isContentsPage(handle);
    }

    boolean isContentsPage(Node handle) throws RepositoryException {
        Node variant = handle.getNode(handle.getName());
        return variant.hasProperty("govscot:contentsPage")
                && variant.getProperty("govscot:contentsPage").getBoolean();
    }

    Node publishedVariant(Node handle) throws RepositoryException {
        return hippoUtils.find(handle.getNodes(handle.getName()),
                v -> hippoUtils.contains(v, "hippo:availability", "live"));
    }

    boolean isFirstVisiblePage(Node variant, boolean publishEvent) throws RepositoryException {
        Node pagesfolder = variant.getParent().getParent();
        NodeIterator it = pagesfolder.getNodes();
        while (it.hasNext()) {
            Node nextHandle = it.nextNode();
            if (!isContentsPage(nextHandle)) {
                if (publishEvent && variant.getParent().isSame(nextHandle.getParent())) {
                    return true;
                }
                Node handleVariant = hippoUtils.getVariant(nextHandle);
                if (handleVariant != null) {
                    return variant.getParent().isSame(nextHandle);
                }
            }
        }
        return false;
    }

    boolean isAnyNodeType(Node variant, String... types) throws RepositoryException {
        for (String type : types) {
            if (variant.isNodeType(type)) {
                return true;
            }
        }
        return false;
    }

    private SearchJournalEntry newEntry(String url, String action) {
        SearchJournalEntry entry = new SearchJournalEntry();
        entry.setUrl(url);
        entry.setAction(action);
        return entry;
    }

    private SearchJournalEntry newEntry(String url, String action, String contentId) {
        SearchJournalEntry entry = newEntry(url, action);
        entry.setContentId(contentId);
        return entry;
    }
}