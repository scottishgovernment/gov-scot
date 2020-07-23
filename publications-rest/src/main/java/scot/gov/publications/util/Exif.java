package scot.gov.publications.util;

import javax.jcr.Binary;

/**
 * Interface fo object capable of extracting page count from binaries.
 */
public interface Exif {

    /**
     * Extract tje page count from a bionary containing a pdf.
     */
    long pageCount(Binary binary, String mimeType);
}
