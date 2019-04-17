package scot.gov.www.resourcecontainers;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.AbstractResourceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * By default the links generated for govscot:DocumentInformation/govscot:document look like:
 * /binaries/content/documents/govscot/publications/publication/2019/04/mydoc/documents/mydoc/govscot:document
 *
 * This resource container improves this by adding the hippo:filename to the end of the url, e.g.
 * /binaries/content/documents/govscot/publications/publication/2019/04/mydoc/documents/mydoc/govscot:document/filename.doc
 */
public class DocumentInformationResourceContainer extends AbstractResourceContainer {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentInformationResourceContainer.class);

    public String getNodeType() {
        return "govscot:DocumentInformation";
    }

    @Override
    public String resolveToPathInfo(Node resourceContainerNode, Node resourceNode, Mount mount) {
        String pathInfo = super.resolveToPathInfo(resourceContainerNode, resourceNode, mount);

        if (pathInfo == null) {
            return null;
        }

        try {

            // if this is not a document link then use the super implementation
            if (!isDocument(resourceNode.getName())) {
                return pathInfo;
            }

            // for document links we want to add the encoded hippo:filename to the end
            String filename = resourceNode.getProperty("hippo:filename").getString();
            String encodedFilename = URLEncoder.encode(filename, "UTF-8");
            return String.format("%s/%s", pathInfo, encodedFilename);
        } catch (RepositoryException | UnsupportedEncodingException e) {
            LOG.error("Failed to generate custom link for {}", pathInfo, e);
            return pathInfo;
        }
    }

    @Override
    public Node resolveToResourceNode(Session session, String pathInfo) {
        Path path = Paths.get(pathInfo);
        Path parent = path.getParent();

        // if the parent of this path is govscot:document then we need to strip off the filename
        if (isDocument(parent.getFileName().toString())) {
            path = parent;
        }
        return super.resolveToResourceNode(session, path.toString());
    }

    boolean isDocument(String path) {
        return "govscot:document".equals(path);
    }
}