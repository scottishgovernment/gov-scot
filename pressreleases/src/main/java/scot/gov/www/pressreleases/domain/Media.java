package scot.gov.www.pressreleases.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by z418868 on 07/10/2015.
 */
public class Media {

    protected String title;
    protected String type;
    protected String url;
    protected String originalName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Media)) {
            return false;
        }

        Media media = (Media) o;

        return new EqualsBuilder()
                .append(title, media.title)
                .append(type, media.type)
                .append(url, media.url)
                .append(originalName, media.originalName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(type)
                .append(url)
                .append(originalName)
                .toHashCode();
    }
}
