package scot.gov.publications.hippo;

import org.apache.commons.io.FileUtils;
import scot.gov.imageprocessing.ImageProcessing;
import scot.gov.imageprocessing.ImageProcessingException;
import scot.gov.publications.util.MimeTypeUtils;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HippoImageNodeFactory {

    Session session;

    BufferedImageSource bufferedImageSource = new BufferedImageSource();

    BinarySource binarySource;

    HippoUtils hippoUtils = new HippoUtils();

    ImageProcessing imageProcessing;

    HippoImageNodeFactory() {
        // default constructor useful in tests
    }

    public HippoImageNodeFactory(Session session, ImageProcessing imageProcessing) {
        this.session = session;
        this.binarySource = new BinarySource(session);
        this.imageProcessing = imageProcessing;
    }

    public Node ensureImageSetNodeExists(
            ZipFile zipFile,
            ZipEntry zipEntry,
            Node folder,
            String type,
            String name) throws RepositoryException {

        Node imageSet = imageSetNode(folder, type, name);
        String mimetype = MimeTypeUtils.detectContentType(name);
        ensureImageNode(imageSet, "hippogallery:original", mimetype, zipFile, zipEntry);

        File thumbnail = null;

        try {
            thumbnail = imageProcessing.thumbnail(zipFile.getInputStream(zipEntry), 60);
            ensureImageNode(imageSet, "hippogallery:thumbnail", mimetype, binarySource.get(new FileInputStream(thumbnail)), 60, 60);
            return imageSet;
        } catch (IOException | ImageProcessingException e) {
            throw new RepositoryException("Failed to create images", e);
        } finally {
            FileUtils.deleteQuietly(thumbnail);
        }
    }

    Node imageSetNode(Node galleryNode, String type, String name) throws RepositoryException {
        Node handle = hippoUtils.ensureNode(galleryNode, name, "hippo:handle", "mix:referenceable");
        Node imageSet = hippoUtils.ensureNode(handle, name, type);
        imageSet.setProperty("hippogallery:filename", name);
        return imageSet;
    }

    Node ensureImageNode(Node imageSet, String name, String mimetype, ZipFile zipFile, ZipEntry zipEntry) throws RepositoryException {
        try {
            BufferedImage bimg = bufferedImageSource.get(zipFile.getInputStream(zipEntry));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            return ensureImageNode(imageSet, name, mimetype, binarySource.get(zipFile.getInputStream(zipEntry)), width, height);
        } catch (IOException e) {
            throw new RepositoryException("Unable to read image", e);
        }
    }

    Node ensureImageNode(Node imageSet, String name, String mimetype, Binary bin, int width, int height)
            throws RepositoryException {

        Node imageNode = hippoUtils.ensureNode(imageSet, name, "hippogallery:image");
        imageNode.setProperty("hippo:filename", name);
        imageNode.setProperty("hippogallery:width", width);
        imageNode.setProperty("hippogallery:height", height);
        imageNode.setProperty("jcr:data", bin);
        imageNode.setProperty("jcr:mimeType", mimetype);
        imageNode.setProperty("jcr:lastModified", Calendar.getInstance());
        return imageNode;
    }
}