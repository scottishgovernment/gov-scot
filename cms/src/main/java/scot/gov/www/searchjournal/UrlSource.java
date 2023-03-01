package scot.gov.www.searchjournal;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * Calculate urls for news and publication nodes for use in search indexing.
 */
public class UrlSource {

    static final String URL_BASE = "https://www.gov.scot/";

    private static final String PAGES = "pages";

    private HippoUtils hippoUtils = new HippoUtils();

    String policyUrl(Node node) throws RepositoryException {
        String path = StringUtils.substringAfter(node.getParent().getPath(), "/content/documents/govscot/");
        path = StringUtils.substringBefore(path, "/index");

        return new StringBuilder(URL_BASE).append(path).append('/').toString();
    }

    String newsUrl(Node node) throws RepositoryException {
        return slugUrl("news", node);
    }

    String publicationUrl(Node publication, Node variant, HippoWorkflowEvent event) throws RepositoryException {
        String publicationUrl = slugUrl("publications", publication);

        if (variant.isNodeType("govscot:Publication")) {
            return publicationUrl;
        }

        if (variant.isNodeType("govscot:PublicationPage")) {
            // if this is the first published non contents page then use the publication url
            return isFirstVisiblePage(variant, "publish".equals(event.action())) ?
                    publicationUrl :
                    new StringBuilder(publicationUrl)
                            .append(PAGES).append('/').append(variant.getName()).append('/')
                            .toString();
        }

        if (variant.isNodeType("govscot:DocumentInformation")) {

            // if this publication does not have any pages then we jurts want to index the publication itself
            if (!hasPages(publication)) {
                return publicationUrl;
            } else {
                // we do not index individual document information pages, but a change to one means that the /documents/
                // page needs to be reindexd
                return new StringBuilder(publicationUrl).append("documents").append('/').toString();
            }
        }

        if (variant.isNodeType("govscot:ComplexDocumentSection")) {
            String chapterPath = StringUtils.substringAfter(event.subjectPath(), "/chapters/");
            return new StringBuilder(publicationUrl).append(chapterPath).append('/').toString();
        }

        // should never get here
        throw new IllegalArgumentException("Unexpected node type trying to maintain search journal :"
                + variant.getPrimaryNodeType().getName());
    }

    boolean hasPages(Node publication) throws RepositoryException {
        Node publicationFolder = publication.getParent().getParent();
        if (!publicationFolder.hasNode(PAGES)) {
            return false;
        }

        Node pagesfolder = publicationFolder.getNode(PAGES);
        return pagesfolder.getNodes().hasNext();
    }

    /**
     * Determine if the node is the first visible page of the publication.  If it is then it will use the url of
     * the publication since this will be its canonical url and will avoid the same content being in funnelback twice.
     *
     * if this is a publish event then the page does not have to be published since the event has not taken effect yet.
     */
    boolean isFirstVisiblePage(Node variant, boolean publishEvent) throws RepositoryException {
        Node pagesfolder = variant.getParent().getParent();
        NodeIterator it = pagesfolder.getNodes();
        while (it.hasNext()) {
            Node nextHandle = it.nextNode();
            if (!isContentPage(nextHandle)) {
                if (publishEvent && variant.getParent().isSame(nextHandle.getParent())) {
                    return true;
                }
                Node published = hippoUtils.getPublishedVariant(nextHandle);
                if (published != null) {
                    return variant.getParent().isSame(nextHandle);
                }
            }
        }

        return false;
    }

    boolean isContentPage(Node handle) throws RepositoryException {
        Node variant = handle.getNode(handle.getName());
        return variant.hasProperty("govscot:contentsPage") && variant.getProperty("govscot:contentsPage").getBoolean();
    }

    String slugUrl(String type, Node node) throws RepositoryException {
        String slug = node.getProperty("govscot:slug").getString();
        return new StringBuilder(URL_BASE)
                .append(type)
                .append('/')
                .append(slug)
                .append('/')
                .toString();
    }
}