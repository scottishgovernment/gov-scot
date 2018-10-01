package scot.gov.www.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.ISO9075;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.apache.commons.lang3.ArrayUtils.removeElements;

public class PublicationLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationLinkProcessor.class);

    public static final String PUBLICATIONS = "publications/";

    @Override
    protected HstLink doPostProcess(HstLink link) {
        if (isPublicationsFullLink(link)) {
            // remove the type, year and month...
            String [] newElements = removeElements(link.getPathElements(),
                    link.getPathElements()[1],
                    link.getPathElements()[2],
                    link.getPathElements()[3]);

            // if this is a link to govscot:Publication then remove the name part of the url e.g. index
            if (link.getPathElements().length == 6) {
                newElements = removeElements(newElements, link.getPathElements()[5]);
            }

            link.setPath(String.join("/", newElements));
        }
        return link;
    }

    private boolean isPublicationsFullLink(HstLink link) {
        // should match on any publication or publication page link.
        return link.getPath().startsWith(PUBLICATIONS) && link.getPathElements().length >= 5;
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {
        if (isPublicationsSlugLink(link)) {
            return preProcessPublicationsLink(link);
        }
        return link;
    }

    private boolean isPublicationsSlugLink(HstLink link) {
        // match any slug style link for a publication or page
        return link.getPath().startsWith(PUBLICATIONS) && link.getPathElements().length >= 2;
    }

    private HstLink preProcessPublicationsLink(HstLink link) {

        /**
         * Turn a publication link into a path that the document can be fetched from.
         * Some examples:
         * /publication/mypublication -> /publications/report/2018/01/mypublication/index
         * /publication/mypublication/pages/1 -> /publications/report/2018/01/mypublication/pages/1
         */
        try {
            // the slug is in element 1 (/publications/slug)
            String slug = link.getPathElements()[1];

            // remove /publications/slug and remember any remaining path elements
            String [] remaining = removeElements(link.getPathElements(),
                    link.getPathElements()[0],
                    link.getPathElements()[1]);

            //get the publication by the publication slug
            Node handle = getHandleBySlug(slug);
            if(handle == null) {
                link.setNotFound(true);
                link.setPath("/pagenotfound");
                return link;
            }

            String pubPath = StringUtils.substringAfter(handle.getPath(), PUBLICATIONS);
            if (remaining.length > 0) {
                // if there is more then one element then remove the 'index' part
                String lastPathElement = StringUtils.substringAfterLast(pubPath, "/");
                pubPath = StringUtils.substringBefore(pubPath, lastPathElement);
            }
            String newPath = String.format("publications/%s%s", pubPath, String.join("/", remaining));
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
        String escapedSlug = ISO9075.encodePath(slug);

        String template =
                "/jcr:root/content/documents/govscot/publications//element(%s, hippostd:folder)" +
                "/element(*, hippo:handle)/element(*, govscot:Publication)";
        String sql = String.format(template, escapedSlug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.XPATH).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }

        // find the index in the results folder
        return findPublication(result.getNodes());
    }

    private Node findPublication(NodeIterator nodeIterator) throws RepositoryException {

        Node publishedNode = null;
        Node lastNode = null;

        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            lastNode = node.getParent();
            if ("published".equals(node.getProperty("hippostd:state").getString())) {
                publishedNode = node.getParent();
            }
        }

        return publishedNode != null ? publishedNode : lastNode;
    }

}