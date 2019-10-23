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
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;
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

            // replace the folder name with the slug
            newElements[1] = slug(link);

            // if this is a link to govscot:Publication then remove the name part of the url e.g. index
            if (link.getPathElements().length == 6) {
                newElements = removeElements(newElements, link.getPathElements()[5]);
            }

            link.setPath(Arrays.stream(newElements).collect(joining("/")));
        }
        return link;
    }

    private boolean isPublicationsFullLink(HstLink link) {
        // should match on any publication or publication page link.
        return link.getPath().startsWith(PUBLICATIONS) && link.getPathElements().length >= 5;
    }

    private String slug(HstLink link) {
        String path =
                String.format("/content/documents/govscot/%s",
                        Arrays.stream(Arrays.copyOf(link.getPathElements(), 5)).collect(joining("/")));
        try {
            Node publicationNode = publicationNode(path);
            if (publicationNode == null) {
                LOG.warn("Unable to find publication node for path {}", link.getPath());
                return null;
            }
            if (!publicationNode.hasProperty("govscot:slug")) {
                LOG.warn("result has no slug property: {}", publicationNode.getPath());
                return null;
            }

            return publicationNode.getProperty("govscot:slug").getString();
        } catch (RepositoryException e) {
            LOG.error("Unable to get the publication slug", e);
            return link.getPathElements()[4];
        }
    }

    private Node publicationNode(String path) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        Node folder = session.getNode(path);
        Node handle = handleFromFolder(folder);
        return findPublishedNode(handle.getNodes(handle.getName()));
    }


    private Node handleFromFolder(Node folder) throws RepositoryException {
        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node candidate = it.nextNode();
            if (candidate.isNodeType("hippo:handle")) {
                return candidate;
            }
        }
        return null;
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
            Node publication = getPublicationBySlug(slug);
            if(publication == null) {
                link.setNotFound(true);
                link.setPath("/pagenotfound");
                return link;
            }

            Node handle = publication.getParent();
            String pubPath = StringUtils.substringAfter(handle.getPath(), PUBLICATIONS);

            if (remaining.length > 0) {
                // if there is more then one element then remove the 'index' part
                String lastPathElement = StringUtils.substringAfterLast(pubPath, "/");
                pubPath = StringUtils.substringBeforeLast(pubPath, lastPathElement);
            }

            String escapedRemaining = remaining.length == 0 ? "" : Arrays.stream(remaining).map(Text::escapeIllegalJcr10Chars).collect(joining("/"));
            String path = determinePath(pubPath, escapedRemaining);

            if (path == null) {
                link.setNotFound(true);
                link.setPath("/pagenotfound");
            } else {
                link.setPath(path);
            }
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: {}", link.getPath(), e);
            return link;
        }
    }

    String determinePath(String pubPath, String escapedRemaining) throws RepositoryException {
        String path = String.format("publications/%s%s", pubPath, escapedRemaining);
        String chapterPath = String.format("publications/%schapters/%s", pubPath, escapedRemaining);
        String stripped = stripAboutAndDownloads(path);

        LOG.info("stripped path is {}", stripped);

        if (anyExist(path, stripped, chapterPath)) {
            return path;
        } else {
            return null;
        }
    }

    String stripAboutAndDownloads(String path) {
        String one = StringUtils.substringBeforeLast(path, "/about");
        return StringUtils.substringBeforeLast(one, "/downloads");
    }

    boolean anyExist(String ...paths) throws RepositoryException {
        Session session = RequestContextProvider.get().getSession();
        for (String path : paths) {
            String testPath = "/content/documents/govscot/" + path;

            // we test that it is not a folder because we do not want a 200 for /chapters, /pages etc.
            if (session.nodeExists(testPath) && findPublishedNode(session.getNode(testPath).getNodes()) != null) {
                return true;
            }
        }
        return false;
    }

    private Node getPublicationBySlug(String slug) throws RepositoryException {
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String escapedSlug = Text.escapeIllegalJcr10Chars(slug);

        String template =
                "/jcr:root/content/documents/govscot/publications//element(*, govscot:SimpleContent)[govscot:slug = '%s']";
        String sql = String.format(template, escapedSlug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.XPATH).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }

        // find the index in the results folder
        return findPublishedNode(result.getNodes());
    }

    private Node findPublishedNode(NodeIterator nodeIterator) throws RepositoryException {

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


}