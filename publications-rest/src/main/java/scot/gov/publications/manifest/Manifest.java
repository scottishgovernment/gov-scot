package scot.gov.publications.manifest;

import org.apache.commons.lang3.StringUtils;
import scot.gov.publications.hippo.HippoPaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Parsed version of the manifest file.
 */
public class Manifest {

    private final List<ManifestEntry> entries = new ArrayList<>();

    public List<ManifestEntry> getEntries() {
        return entries;
    }

    public ZipEntry findZipEntry(ZipFile zipFile, ManifestEntry manifestEntry) {

        List<ZipEntry> zipEntries = zipFile.stream()
                .filter(zipEntry -> isManifestEntry(zipEntry, manifestEntry))
                .collect(toList());

        if (zipEntries.isEmpty()) {
            return null;
        }

        return zipEntries.get(0);
    }

    private boolean isManifestEntry(ZipEntry zipEntry, ManifestEntry manifestEntry) {
        String filename = StringUtils.substringAfterLast(zipEntry.getName(), "/");
        return manifestEntry.getFilename().equals(filename);
    }

    /**
     * Assign a friendly filename to each entry
     */
    public void assignFriendlyFilenames() {
        Map<String, List<ManifestEntry>> byTitle = getEntries().stream().collect(groupingBy(e -> titleAndExtension(e)));
        for (Map.Entry<String, List<ManifestEntry>> entriesWithSameTitle : byTitle.entrySet()) {
            int i = 0;
            for (ManifestEntry entry : entriesWithSameTitle.getValue()) {
                entry.setFriendlyFilename(friendlyFilename(entry, i));
                i++;
            }
        }
    }

    private String titleAndExtension(ManifestEntry entry) {
        return entry.getTitle() + extension(entry.getFilename());
    }

    /**
     * The friendlyFilename to use for this manifest entry
     *
     * First we slugify the title.  If this has an index then add a hypen with a number for it, then add the extension
     * from the friendlyFilename contained in the manifest
     */
    private String friendlyFilename(ManifestEntry entry, int i) {
        return String.format("%s%s.%s",
                HippoPaths.slugify(entry.getTitle()),
                i == 0 ? "" : "-"+i,
                extension(entry.getFilename()));
    }

    private String extension(String filename) {
        return StringUtils.substringAfterLast(filename, ".");
    }
}
