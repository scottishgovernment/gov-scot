package gov.scot.www.thumbnails;

import gov.scot.www.thumbnails.imageprocessing.ImageProcessing;
import gov.scot.www.thumbnails.imageprocessing.TempFileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class ThumbnailsProvider {

    private static final List<Integer> SIZES = new ArrayList<>();

    static {
        Collections.addAll(SIZES, 330, 214, 165, 107);
    }

    /**
     * Create thumbnails for document attachments.
     */
    public static Map<Integer, File> thumbnails(InputStream documentStream, String mimeType)
            throws ThumbnailsProviderException {

        FileType type = FileType.forMimeType(mimeType);

        if (type == FileType.PDF) {
            return pdfThumbnails(documentStream);
        }

            if (type.isImage()) {
            return imageThumbnails(documentStream, type);
        }

        return fixedThumbnails(type);
    }

    private static Map<Integer, File> pdfThumbnails(InputStream documentStream) throws ThumbnailsProviderException {
        File pdfImage = pdfImageStream(documentStream);
        return imageThumbnails(pdfImage);
    }

    private static File pdfImageStream(InputStream documentStream) throws ThumbnailsProviderException {
        File pdfImg = null;
        try {
            pdfImg = ImageProcessing.extractPdfCoverImage(documentStream);
            return pdfImg;
        } catch (Exception e) {
            FileUtils.deleteQuietly(pdfImg);
            throw new ThumbnailsProviderException(e);
        }
    }

    private static Map<Integer, File> imageThumbnails(InputStream input, FileType type) throws ThumbnailsProviderException {
        File imageFile = null;
        try {
            imageFile = TempFileUtil.createTempFile(type, input);
            return imageThumbnails(imageFile);
        } catch (IOException e) {
            throw new ThumbnailsProviderException(e);
        } finally {
            FileUtils.deleteQuietly(imageFile);
        }
    }

    private static Map<Integer, File> imageThumbnails(File image) throws ThumbnailsProviderException {
        Map<Integer, File> thumbs = new HashMap();
        for (Integer size : SIZES) {
            File thumb = imageThumbnail(image, size);
            thumbs.put(size, thumb);
        }
        return thumbs;
    }

    private static File imageThumbnail(File image, int size) throws ThumbnailsProviderException {
        File thumbnail = null;
        try {
            thumbnail = ImageProcessing.thumbnail(new FileInputStream(image), size);
            return thumbnail;
        } catch (Exception e) {
            FileUtils.deleteQuietly(thumbnail);
            throw new ThumbnailsProviderException(e);
        }

    }

    private static Map<Integer, File> fixedThumbnails(FileType type) throws ThumbnailsProviderException {
        Map<Integer, File> thumbs = new HashMap();
        for (Integer size : SIZES) {
            File thumb = fixedThumbnail(type, size);
            thumbs.put(size, thumb);
        }
        return thumbs;
    }

    private static File fixedThumbnail(FileType type, int size) throws ThumbnailsProviderException {

        // lookup based on the required size
        String filename = String.format("/thumbnails/%s_%dpx.png", FileType.iconName(type), size);
        InputStream inputStream = ThumbnailsProvider.class.getResourceAsStream(filename);
        if (isNull(inputStream)) {
            // check we have default icon for this size
            filename = String.format("/thumbnails/gen_%dpx.png", size);
            inputStream = ThumbnailsProvider.class.getResourceAsStream(filename);
        }
        if (isNull(inputStream)) {
            throw new ThumbnailsProviderException("Could not load thumbnail " + filename);
        }

        try {
            return TempFileUtil.createTempFile(FileType.PNG, inputStream);
        } catch (IOException e) {
            throw new ThumbnailsProviderException(e);
        }
    }

}
