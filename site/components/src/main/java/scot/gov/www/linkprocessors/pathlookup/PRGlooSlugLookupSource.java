package scot.gov.www.linkprocessors.pathlookup;

import org.apache.jackrabbit.util.Text;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.publishing.sluglookup.PathForSlugSource;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * If a news slug is not found, try searching for it by its prgloo slug.  This is temporary until we identify the issue
 * causing the slug to not be added to some news items.
 * See: https://sg-dtd.atlassian.net/browse/MGS-7694
 */
public class PRGlooSlugLookupSource implements PathForSlugSource {

    @Override
    public String get(String slug, String site, String type, String mount) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String escapedSlug = Text.escapeIllegalJcr10Chars(slug);
        String template =
                "/jcr:root/content/documents/govscot/news/element(*, govscot:SimpleContent)[govscot:prglooslug = '%s']";
        String sql = String.format(template, escapedSlug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.XPATH).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }

        // find the index in the results folder
        Node publishedNode = findPublishedNode(result.getNodes());
        if (publishedNode == null) {
            return null;
        }

        return substringAfter(publishedNode.getParent().getPath(), "/content/documents/" + site);
    }

    protected Node findPublishedNode(NodeIterator nodeIterator) throws RepositoryException {
        Node publishedNode = null;
        Node lastNode = null;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            lastNode = node;
            if (isPublished(node)) {
                publishedNode = node;
            }
        }
        return publishedNode != null ? publishedNode : lastNode;
    }

    boolean isPublished(Node node) throws RepositoryException {
        return node.isNodeType("govscot:basedocument")
                && "published".equals(node.getProperty("hippostd:state").getString());
    }
}