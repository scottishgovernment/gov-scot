package scot.gov.pressreleases.domain;

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

}
