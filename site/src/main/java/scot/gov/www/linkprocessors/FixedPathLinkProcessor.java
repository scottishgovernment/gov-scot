package scot.gov.www.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public abstract class  FixedPathLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(FixedPathLinkProcessor.class);

    @Override
    protected HstLink doPostProcess(HstLink link) {
        if (isSiteItemFullLink(link)) {
            // remove the date and time...
            link.setPath(String.format("%s", link.getPathElements()[1]));
        }

        return link;
    }

    private boolean isSiteItemFullLink(HstLink link) {
        return link.getPath().startsWith(getPath()) && link.getPathElements().length == 2;
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {

        if (link.getPathElements().length != 1) {
            return link;
        }

        try {
            String slug = link.getPathElements()[link.getPathElements().length - 1];
            Node handle = getHandleBySlug(slug);
            if (handle == null) {
                return link;
            }
            String newPath = String.format("%s%s", getPath(), StringUtils.substringAfter(handle.getPath(), getPath()));
            link.setPath(newPath);
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: \"{}\"", link.getPath(), e);
            return link;
        }
    }

    private Node getHandleBySlug(String slug) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String topicPath = String.format("/content/documents/govscot/%s%s", getPath(), Text.escapeIllegalJcr10Chars(slug));
        if (session.nodeExists(topicPath)) {
            return session.getNode(topicPath);
        } else {
            return null;
        }
    }

    protected abstract String getPath();
}