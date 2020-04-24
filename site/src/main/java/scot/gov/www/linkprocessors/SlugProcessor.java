package scot.gov.www.linkprocessors;

import org.apache.jackrabbit.util.Text;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * Created by z441571 on 21/04/2020.
 */
public abstract class SlugProcessor extends HstLinkProcessorTemplate {

    protected boolean isFullLink(HstLink link, String typePath, int pathLength) {
        // should match on any link.
        return link.getPath().startsWith(typePath) && link.getPathElements().length >= pathLength;
    }

    protected Node findNode(String path) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        Node folder = session.getNode(path);

        Node handle = handleFromFolder(folder);
        return findPublishedNode(handle.getNodes(handle.getName()));

    }

    private Node handleFromFolder(Node folder) throws RepositoryException {
        if (folder.isNodeType("hippo:handle")) {
            return folder;
        }

        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node candidate = it.nextNode();
            if (candidate.isNodeType("hippo:handle")) {
                return candidate;
            }
        }
        return null;
    }

    protected Node findPublishedNode(NodeIterator nodeIterator) throws RepositoryException {

        Node publishedNode = null;
        Node lastNode = null;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            lastNode = node;
            if (node.isNodeType("govscot:basedocument") && "published".equals(node.getProperty("hippostd:state").getString())) {
                publishedNode = node;
            }
        }

        return publishedNode != null ? publishedNode : lastNode;
    }

    protected boolean isSlugLink(HstLink link, String typePath, int pathLength) {
        return link.getPath().startsWith(typePath) && link.getPathElements().length == pathLength;
    }

    protected Node getNodeBySlug(String slug, String typePath) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String escapedSlug = Text.escapeIllegalJcr10Chars(slug);

        String template =
                "/jcr:root/content/documents/govscot/%s/element(*, govscot:SimpleContent)[govscot:slug = '%s']";
        String sql = String.format(template, typePath, escapedSlug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.XPATH).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }

        // find the index in the results folder
        return findPublishedNode(result.getNodes());
    }
}
