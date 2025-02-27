package scot.gov.www.pressreleases.prgloo;

public class PRGlooConfiguration {

    private String api = "https://cdn.prgloo.com/api/";

    private String media = "https://cdn.prgloo.com/media/";

    private String token;

    public String getApi() {
        return api;
    }

    public void setUrl(String api) {
        this.api = api;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
