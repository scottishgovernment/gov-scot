package scot.gov.publications.hippo;

import scot.gov.imageprocessing.ImageProcessing;
import scot.gov.publications.ApsZipImporterException;
import scot.gov.publications.util.ZipEntryUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toList;

public class ImageUploader {

    HippoPaths hippoPaths;

    HippoUtils hippoUtils = new HippoUtils();

    HippoImageNodeFactory imageNodeFactory = new HippoImageNodeFactory();

    public ImageUploader(Session session, ImageProcessing imageProcessing) {
        this.hippoPaths = new HippoPaths(session);
        this.imageNodeFactory = new HippoImageNodeFactory(session, imageProcessing);
    }

    public Map<String, String> createImages(ZipFile zipFile, Node pubFolder) throws ApsZipImporterException {
        try {
            List<String> path = hippoUtils.pathFromNode(pubFolder);
            return doCreateImages(zipFile, path);
        } catch (RepositoryException e) {
            throw new ApsZipImporterException("Failed to upload images", e);
        }
    }

    private Map<String, String> doCreateImages(ZipFile zipFile, List<String> path) throws RepositoryException {
        Map<String, String> filenameToImage = new HashMap<>();

        List<ZipEntry> imgEntries = zipFile.stream()
                .filter(ZipEntryUtil::isImg)
                .collect(toList());

        for (ZipEntry imgEntry : imgEntries) {
            Node galleryNode = hippoPaths.ensureImagePath(path);
            Path imagePath = Paths.get(imgEntry.getName());
            String imageFileName = imagePath.getFileName().toString();
            Node imgSetNode = imageNodeFactory.ensureImageSetNodeExists(
                    zipFile,
                    imgEntry,
                    galleryNode,
                    "govscot:PublicationInlineImage",
                    imageFileName);
            String imageNodeIdentifier = imgSetNode.getParent().getIdentifier();
            filenameToImage.put(imageFileName, imageNodeIdentifier);
        }
        return filenameToImage;
    }

}
