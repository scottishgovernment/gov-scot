package scot.gov.www.searchjournal;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Calculate urls for news and publication nodes for use in search indexing.
 */
public class UrlSource {

    private static final Logger LOG = LoggerFactory.getLogger(UrlSource.class);

    static final String URL_BASE = "https://www.gov.scot/";

    String newsUrl(Node node) throws RepositoryException {
        return slugUrl("news", node);
    }

    String publicationUrl(Node publication, Node variant, HippoWorkflowEvent event) throws RepositoryException {
        String publicationUrl = slugUrl("publications", publication);
        LOG.info("publicationUrl for {}", variant.getPath());

        if (variant.isNodeType("govscot:Publication")) {
            return publicationUrl;
        }

        if (variant.isNodeType("govscot:PublicationPage")) {
            return new StringBuilder(publicationUrl).append("pages").append('/').append(variant.getName()).append('/').toString();
        }

        if (variant.isNodeType("govscot:DocumentInformation")) {
            // we do not index individual document information pages, but a change to one means that the /documents/
            // page needs to be reindexd
            return new StringBuilder(publicationUrl).append("documents").append('/').toString();
        }

        if (variant.isNodeType("govscot:ComplexDocumentSection")) {
            String chapterPath = StringUtils.substringAfter(event.subjectPath(), "/chapters/");
            return new StringBuilder(publicationUrl).append(chapterPath).append('/').toString();
        }

        // should never get here
        throw new IllegalArgumentException("Unexpected node type trying to maintain search journal :"
                + variant.getPrimaryNodeType().getName());
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