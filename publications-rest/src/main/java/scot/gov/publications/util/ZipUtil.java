package scot.gov.publications.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toList;

public class ZipUtil {

    FileUtil fileUtil = new FileUtil();

    public String getDirname(ZipFile zipFile) {
        ZipEntry firstEntry = zipFile.entries().nextElement();
        return firstEntry.getName();
    }

    /**
     * Determine the zip file to process.  If the ZIP contains a nested zip (as they are when downloaded from Simplyasset)
     * then extract the nested zip.  If the file is not nested then just return the original file.
     */
    public File getZipToProcess(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        List<ZipEntry> zipEntries = zipFile.stream()
                .filter(ZipEntryUtil::isZip)
                .filter(entry -> !entry.getName().startsWith("__MACOSX/"))
                .collect(toList());

        if (zipEntries.isEmpty()) {
            return file;
        }

        if (zipEntries.size() > 1) {
            throw new IllegalArgumentException("More than one zip in the zip!");
        }

        ZipEntry entry = zipEntries.get(0);
        return fileUtil.createTempFile("extractedFromZip", "zip", zipFile.getInputStream(entry));
    }
}
