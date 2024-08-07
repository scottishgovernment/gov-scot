package scot.gov.www.linkprocessors;

import org.hippoecm.hst.core.linking.HstLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.sluglookup.PathForSlugSource;
import scot.gov.publishing.sluglookup.PathSourceFactory;
import scot.gov.www.linkprocessors.pathlookup.PRGlooSlugLookupSource;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class NewsLinkProcessor extends SlugProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(NewsLinkProcessor.class);

    public static final String NEWS = "news/";

    PathForSlugSource pathSource = PathSourceFactory.withFallback(new PRGlooSlugLookupSource());

    @Override
    protected HstLink doPostProcess(HstLink link) {
        if (isFullLink(link, NEWS, 4)) {
            // get the slug from the govscot:News item field
            link.setPath(String.format("news/%s", slug(link)));
        }

        return link;
    }

    private String slug(HstLink link) {

        String path = String.format("/content/documents/govscot/%s", link.getPath());

        try {
            Node newsNode = findNode(path);
            if (newsNode == null) {
                LOG.warn("Unable to find publication node for path {}", link.getPath());
                return null;
            }

            return slugProperty(newsNode);
        } catch (RepositoryException e) {
            LOG.error("Unable to get the news slug", e);
            return link.getPath();
        }
    }


    String slugProperty(Node newsNode) throws RepositoryException {
        String slug = prop(newsNode, "govscot:slug");
        if (isNotBlank(slug)) {
            return slug;
        }

        String prglooslug = prop(newsNode, "govscot:prglooslug");
        if (isNotBlank(prglooslug)) {
            LOG.warn("using govscot:prglooslug property for node {}", newsNode.getPath());
            return prglooslug;
        }

        LOG.error("result has no govscot:slug or govscot:prglooslug property {}", newsNode.getPath());
        return null;
    }

    String prop(Node node, String prop) throws RepositoryException {
        if (!node.hasProperty(prop)) {
            return null;
        }

        return node.getProperty(prop).getString();
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {
        if (isSlugLink(link, NEWS, 2)) {
            return preProcessNewsLink(link);
        }
        return link;
    }

    private HstLink preProcessNewsLink(HstLink link) {
        try {
            String slug = link.getPathElements()[link.getPathElements().length - 1];

            String path = pathSource.get(slug, "govscot", "news", link.getMount().getType());

            if (path == null) {
                return link;
            }

            link.setPath(path);
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: {}", link.getPath(), e);
            return link;
        }
    }

}