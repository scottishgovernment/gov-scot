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

public class NewsLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(NewsLinkProcessor.class);

    public static final String NEWS = "news/";

    @Override
    protected HstLink doPostProcess(HstLink link) {
        if (isNewsFullLink(link)) {
            // remove the date and time...
            link.setPath(String.format("news/%s", link.getPathElements()[3]));
        }

        return link;
    }

    private boolean isNewsFullLink(HstLink link) {
        return link.getPath().startsWith(NEWS) && link.getPathElements().length == 4;
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {
        if (isNewsSlugLink(link)) {
            return preProcessNewsLink(link);
        }
        return link;
    }

    private boolean isNewsSlugLink(HstLink link) {
        return link.getPath().startsWith(NEWS) && link.getPathElements().length == 2;
    }

    private HstLink preProcessNewsLink(HstLink link) {
        try {
            String slug = link.getPathElements()[link.getPathElements().length - 1];
            Node handle = getHandleBySlug(slug);

            if (handle == null) {
                return link;
            }
            String newPath = String.format("news/%s", StringUtils.substringAfter(handle.getPath(), NEWS));
            link.setPath(newPath);
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to imageprocessing link: {}", link.getPath(), e);
            return link;
        }
    }

    private Node getHandleBySlug(String slug) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String newsPath = "/content/documents/govscot/news/";
        String sql = String.format("SELECT * FROM govscot:News WHERE jcr:path LIKE '%s%%/%s'", newsPath, slug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }
        return result.getNodes().nextNode().getParent();
    }

}