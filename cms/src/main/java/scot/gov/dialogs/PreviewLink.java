package scot.gov.dialogs;

import org.apache.wicket.util.io.IClusterable;

import javax.jcr.Node;
import java.util.Calendar;

public class PreviewLink implements IClusterable {

    private static final long serialVersionUID = 1L;
    private final String previewKey;
    private final String url;
    private final String username;
    private final Calendar creationDate;
    private final Calendar expirationDate;
    private final boolean isValid;
    private final Node node;

    public PreviewLink(final String previewKey, final String url, final String username, final Calendar creationDate, final Calendar expirationDate, final boolean isValid, final Node node) {
        this.previewKey = previewKey;
        this.url = url;
        this.username = username;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.isValid = isValid;
        this.node = node;
    }

    public String getPreviewKey() {
        return previewKey;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public boolean isValid() {
        return isValid;
    }

    public Node getNode() {
        return node;
    }
}