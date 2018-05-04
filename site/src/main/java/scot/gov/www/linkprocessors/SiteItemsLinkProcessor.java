package scot.gov.www.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class SiteItemsLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(SiteItemsLinkProcessor.class);

    @Override
    protected HstLink doPostProcess(HstLink link) {
        if (isSiteItemFullLink(link)) {
            // remove the date and time...
            link.setPath(String.format("%s", link.getPathElements()[1]));
        }

        return link;
    }

    private boolean isSiteItemFullLink(HstLink link) {
        return link.getPath().startsWith("siteitems/") && link.getPathElements().length == 2;
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
            String newPath = String.format("siteitems/%s", StringUtils.substringAfter(handle.getPath(), "siteitems/"));
            link.setPath(newPath);
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: {}", link.getPath(), e);
            return link;
        }
    }

    private Node getHandleBySlug(String slug) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String sql = String.format("SELECT * FROM govscot:SiteItem WHERE jcr:path LIKE '%%/%s'", slug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }
        return result.getNodes().nextNode().getParent();
    }

}