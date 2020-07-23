package scot.gov.publications.manifest;

import org.apache.commons.lang3.StringUtils;

/**
 * An entry in the manifest file contains a filename and its title.
 */
public class ManifestEntry {

    private String filename;

    private String title;

    private String friendlyFilename;

    public ManifestEntry(String filename, String title) {
        this.filename = filename;
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleOrFilename() {
        return StringUtils.isEmpty(title) ?  getFilename() : title;
    }

    public String getFriendlyFilename() {
        return friendlyFilename;
    }

    public void setFriendlyFilename(String friendlyFilename) {
        this.friendlyFilename = friendlyFilename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
