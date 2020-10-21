package scot.gov.www.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.core.linking.HstLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class NewsLinkProcessor extends SlugProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(NewsLinkProcessor.class);

    public static final String NEWS = "news/";

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
            if (!newsNode.hasProperty("govscot:slug")) {
                LOG.warn("result has no slug property: {}", newsNode.getPath());
                return null;
            }

            return newsNode.getProperty("govscot:slug").getString();
        } catch (RepositoryException e) {
            LOG.error("Unable to get the news slug", e);
            return link.getPath();
        }
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
            Node newsNode = getNodeBySlug(slug, NEWS);

            if (newsNode == null) {
                return link;
            }

            Node handle = newsNode.getParent();
            String newsPath = StringUtils.substringAfter(handle.getPath(), "govscot/");

            link.setPath(newsPath);
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: {}", link.getPath(), e);
            return link;
        }
    }

}