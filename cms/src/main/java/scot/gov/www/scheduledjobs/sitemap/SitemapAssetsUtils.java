package scot.gov.www.scheduledjobs.sitemap;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;

import static java.lang.String.format;

/**
 * Code used to turn a byte array into an asset to represent sitemaps
 */
public class SitemapAssetsUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapAssetsUtils.class);

    private static final String ASSET_ROOT = "/content/assets/";

    private static final String ASSET_ROOT_SITEMAPS = "/content/assets/sitemaps";

    public static final String SITEMAPS = "sitemaps";
    public static final String JCR_DATA = "jcr:data";

    private SitemapAssetsUtils() {
        // utility class
    }

    protected static void createOrUpdateResource(Session session, String prefixIn, byte [] bytes)
            throws RepositoryException, IOException {
        String prefix = StringUtils.isEmpty(prefixIn) ? "" : "." + prefixIn;
        String name = format("sitemap%s.xml", prefix);
        Node resource = getResource(session, name);
        byte [] existingData = null;
        if (resource.hasProperty(JCR_DATA)) {
            existingData = IOUtils.toByteArray(resource.getProperty(JCR_DATA).getBinary().getStream());
        }

        // do not save if the content has not changed
        if (Arrays.equals(bytes, existingData)) {
            return;
        }

        InputStream in = new ByteArrayInputStream(bytes);
        resource.setProperty("jcr:lastModified", Calendar.getInstance());
        resource.setProperty("jcr:mimeType", "application/xml");
        resource.setProperty(JCR_DATA, session.getValueFactory().createBinary(in));

        LOG.info("Saved sitemap to {}", name);
        session.save();
    }

    private static Node getResource(Session session, String name) throws RepositoryException {
        String path = format("%s/%s/%s/hippogallery:asset", ASSET_ROOT_SITEMAPS, name, name);
        if (session.nodeExists(path)) {
            return session.getNode(path);
        }

        Node assetRoot = session.getNode(ASSET_ROOT);
        Node gallery = getGallery(assetRoot);
        Node handle = getHandle(gallery, name);
        Node assetSet = getAssertSet(handle, name);
        assetSet.setProperty("hippo:availability", new String [] {"live", "preview"});
        return assetSet.getNode("hippogallery:asset");
    }

    private static Node getAssertSet(Node handle, String name) throws RepositoryException {
        if (handle.hasNode(name)) {
            return handle.getNode(name);
        }
        return handle.addNode(name, "hippogallery:exampleAssetSet");
    }

    private static Node getGallery(Node root) throws RepositoryException {
        if (root.hasNode(SITEMAPS)) {
            return root.getNode(SITEMAPS);
        }

        Node gallery = root.addNode(SITEMAPS, "hippogallery:stdAssetGallery");
        gallery.addMixin("mix:versionable");
        gallery.addMixin("mix:referenceable");
        gallery.addMixin("mix:simpleVersionable");
        return gallery;
    }

    private static Node getHandle(Node gallery, String name) throws RepositoryException {
        if (gallery.hasNode(name)) {
            return gallery.getNode(name);
        }

        Node handle = gallery.addNode(name, "hippo:handle");
        handle.addMixin("mix:referenceable");
        return handle;
    }

}
