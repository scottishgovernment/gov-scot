package scot.gov.publications.hippo.pages;

import org.apache.commons.io.IOUtils;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.hippo.*;
import scot.gov.publications.hippo.rewriter.PublicationLinkRewriter;
import scot.gov.publications.metadata.Metadata;
import scot.gov.publications.util.ZipEntryUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static scot.gov.publications.hippo.Constants.*;

/**
 * Contains the logic used to ad page nodes from a zip file to a publicaiton folder.
 */
public class PublicationPageUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationPageUpdater.class);

    private static final String PAGES = "pages";

    HippoUtils hippoUtils = new HippoUtils();

    Session session;

    HippoNodeFactory nodeFactory;

    HippoPaths hippoPaths;

    DocumentHelper documentHelper;

    PublicationPathStrategy pathStrategy;

    public PublicationPageUpdater(Session session) {
        this.session = session;
        this.nodeFactory = new HippoNodeFactory(session);
        this.hippoPaths = new HippoPaths(session);
        this.documentHelper = new DocumentHelper(session);
        this.pathStrategy = new PublicationPathStrategy(session);
    }

    /**
     * Ensure that the publication folder contains a pages folder containing a page node for each of the html pages
     * contained in the zip file.
     *
     * @param zipFile The zip file containing the publicaiton
     * @param publicationFolder Node of the folder containing the publication in the repo
     * @param filenameToImageId Map image filenames to the node of tat image in the repo
     * @param docnameToNode Map from the document name to the node of that document in the repo
     * @throws ApsZipImporterException
     */
    public void addPages(
            ZipFile zipFile,
            Metadata metadata,
            Node publicationFolder,
            Map<String, String> filenameToImageId,
            Map<String, Node> docnameToNode) throws ApsZipImporterException {

        try {
            // map the names of all pages we have created
            Map<String, Node> nodesByEntryname = doAddPages(
                    zipFile,
                    metadata,
                    publicationFolder,
                    filenameToImageId);
            nodesByEntryname.putAll(docnameToNode);
            PublicationLinkRewriter linkRewriter = new PublicationLinkRewriter(publicationFolder.getName(), nodesByEntryname);
            linkRewriter.rewrite(publicationFolder);
        } catch (IOException | RepositoryException e) {
            throw new ApsZipImporterException("Failed to upload pages", e);
        }
    }

    private Map<String, Node> doAddPages(
            ZipFile zipFile,
            Metadata metadata,
            Node publicationFolder,
            Map<String, String> filenameToImageId)
                throws IOException, RepositoryException, ApsZipImporterException {

        Node pages = ensurePagesNode(publicationFolder);
        pages.setProperty(HIPPOSTD_FOLDERTYPE, new String[]{"new-publication-page"});
        session.save();

        List<ZipEntry> htmlEntries = zipFile.stream().filter(ZipEntryUtil::isHtml).sorted(Comparator.comparing(ZipEntry::getName)).collect(toList());

        LOG.info("Adding {} pages", htmlEntries.size());
        Map<String, Node> pageNodesByEntry = new HashMap<>();
        int i = 0;
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);

        String pagesPath = publicationFolder.getPath() + "/pages/";
        for (ZipEntry htmlEntry : htmlEntries) {
            InputStream in = zipFile.getInputStream(htmlEntry);
            String pageContent = IOUtils.toString(in, UTF_8);

            ContentNode pageContentNode = new ContentNodes().newPageNode(pageContent, i, filenameToImageId);
            String path = pagesPath + i;
            String publishedPath = importTask.createOrUpdateDocumentFromVariantContentNode(pageContentNode, "govscot:PublicationPage", path, "en", pageContentNode.getProperty("govscot:title").getValue());
            Node pageNode = session.getNode(publishedPath);
            documentHelper.ensurePublicationState(documentManager,
                    pageNode,
                    path,
                    metadata);

            Path entryPath = java.nio.file.Paths.get(htmlEntry.getName());
            pageNodesByEntry.put(entryPath.getFileName().toString(), pageNode);
            i++;
        }
        return pageNodesByEntry;
    }

    private Node ensurePagesNode(Node publicationsFolder) throws RepositoryException {
        if (publicationsFolder.hasNode(PAGES)) {
            Node pages = publicationsFolder.getNode(PAGES);
            hippoUtils.removeChildren(pages);
            return pages;
        }
        return hippoPaths.folderNode(publicationsFolder, PAGES);
    }

}
