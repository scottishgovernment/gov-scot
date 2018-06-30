package gov.scot.www;

import gov.scot.www.exif.Exif;
import gov.scot.www.thumbnails.FileType;
import gov.scot.www.thumbnails.ThumbnailsProvider;
import gov.scot.www.thumbnails.ThumbnailsProviderException;
import org.apache.commons.io.FileUtils;
import org.hippoecm.repository.api.HippoNode;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Event listener to generate thumbnails whenever a document is edited.
 */
public class ThumbnailsDaemonModule implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(ThumbnailsDaemonModule.class);

    private Session session;

    @Override
    public void initialize(final Session session) throws RepositoryException {
        this.session = session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);
    }

    @Override
    public void shutdown() {
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }

    @Subscribe
    public void handleEvent(final HippoWorkflowEvent event) {
        if (!event.success()) {
            return;
        }
        try {
            HippoNode subject = (HippoNode) session.getNodeByIdentifier(event.subjectId());

            if (!subject.getName().equals("govscot:document")) {
                return;
            }

            deleteExistingThumbnails(subject);
            createThumbnails(subject);
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while doing simple JCR read operations", e);
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