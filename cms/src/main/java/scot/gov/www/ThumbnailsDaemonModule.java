package scot.gov.www;

import org.apache.commons.io.FileUtils;
import org.hippoecm.repository.api.HippoNode;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.imageprocessing.exif.Exif;
import scot.gov.imageprocessing.thumbnails.FileType;
import scot.gov.imageprocessing.thumbnails.ThumbnailsProvider;
import scot.gov.imageprocessing.thumbnails.ThumbnailsProviderException;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Event listener to generate thumbnails whenever a document is edited.
 */
public class ThumbnailsDaemonModule extends DaemonModuleBase {

    private static final Logger LOG = LoggerFactory.getLogger(ThumbnailsDaemonModule.class);

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success();
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {

        try {
            HippoNode subject = (HippoNode) session.getNodeByIdentifier(event.subjectId());

            if (!"govscot:document".equals(subject.getName())) {
                return;
            }
            LOG.info("updating thumbnails for {}", subject.getPath());
            deleteExistingThumbnails(subject);
            createThumbnails(subject);
            session.save();
        } catch (ThumbnailsProviderException e) {
            LOG.error("Unexpected exception while generating thumbnail", e);
        } catch (FileNotFoundException e) {
            LOG.error("Unexpected exception reading thumbnail tmp file", e);
        }
    }

    private void deleteExistingThumbnails(Node node) throws RepositoryException {
        NodeIterator nodeIterator = node.getParent().getNodes("govscot:thumbnails");
        while (nodeIterator.hasNext()) {
            Node thumbnail = nodeIterator.nextNode();
            thumbnail.remove();
        }
    }

    private void createThumbnails(Node documentNode)
            throws RepositoryException, FileNotFoundException, ThumbnailsProviderException {

        Node documentInformationNode = documentNode.getParent();
        Binary data = documentNode.getProperty("jcr:data").getBinary();
        String mimeType = documentNode.getProperty("jcr:mimeType").getString();
        String filename = documentNode.getProperty("hippo:filename").getString();

        if (mimeType == null) {
            LOG.warn("A document has been uploaded with no mimetype: {}", documentNode.getPath());
        }

        Map<Integer, File> thumbnails = ThumbnailsProvider.thumbnails(data.getStream(), mimeType);

        List<Integer> sortedKeys = new ArrayList<>(thumbnails.keySet());
        Collections.sort(sortedKeys);
        for (Integer size : sortedKeys) {
            File thumbnail = thumbnails.get(size);
            Node resourceNode = documentInformationNode.addNode("govscot:thumbnails", "hippo:resource");
            resourceNode.addMixin("hippo:skipindex");
            Binary binary = session.getValueFactory().createBinary(new FileInputStream(thumbnail));
            String thumbnailFilename = String.format("%s_%s.png", filename, size);
            resourceNode.setProperty("hippo:filename", thumbnailFilename);
            resourceNode.setProperty("jcr:data", binary);
            resourceNode.setProperty("jcr:mimeType", FileType.PNG.getMimeType());
            resourceNode.setProperty("jcr:lastModified", Calendar.getInstance());
            FileUtils.deleteQuietly(thumbnail);
        }

        documentInformationNode.setProperty("govscot:size", data.getSize());
        if (FileType.forMimeType(mimeType) == FileType.PDF) {
            documentInformationNode.setProperty("govscot:pageCount", Exif.pageCount(data));
        } else {
            documentInformationNode.setProperty("govscot:pageCount", 0);
        }
    }

}
