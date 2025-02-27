package scot.gov.www.pressreleases.prgloo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.httpclient.HttpClientSource;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.prgloo.rest.PressReleaseItem;
import scot.gov.www.pressreleases.prgloo.rest.TagGroups;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

public class PRGlooClient {

    private static final Logger LOG = LoggerFactory.getLogger(PRGlooClient.class);

    public static final String SPEECH_STREAM_ID = "57c7f3b406a210103429f646";

    public static final String PRESS_RELEASE_STREAM_ID = "57c7f3b406a210103429f644";

    public static final String CORRESPONDENCE_STREAM_ID = "6369117cf675a15621def57c";

    CloseableHttpClient httpClient;

    HttpHost host;

    String baseUrl;

    ObjectMapper objectMapper = new ObjectMapper();

    public PRGlooClient() {
        PRGlooConfiguration config = config();
        URI uri = URI.create(config.getApi());
        baseUrl = config.getApi();
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        httpClient = HttpClientSource.newClient();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public PressReleaseItem item(String id) throws PRGlooException {
        return readValue("content/" + id, PressReleaseItem.class);
    }

    public ChangeHistory changesNews(Instant time) throws PRGlooException {
        return changes(PRESS_RELEASE_STREAM_ID, time);
    }

    public ChangeHistory changesSpeeches(Instant time) throws PRGlooException {
        return changes(SPEECH_STREAM_ID, time);
    }

    public ChangeHistory changesCorrespondences(Instant time) throws PRGlooException {
        return changes(CORRESPONDENCE_STREAM_ID, time);
    }

    public TagGroups tagGroups() throws PRGlooException {
        String url = "business/";
        return readValue(url, TagGroups.class);
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOG.error("Failed to close httpclient", e);
        }
    }

    ChangeHistory changes(String stream, Instant time) throws PRGlooException {
        String url = String.format("content/changes?contentStreamId=%s&startDateTime=%s", stream, time.toString());
        return readValue(url, ChangeHistory.class);
    }

    public static PRGlooConfiguration config() {
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        String token = containerConfiguration.getString("prgloo.token");
        if (StringUtils.isBlank(token)) {
            return null;
        }

        PRGlooConfiguration configuration = new PRGlooConfiguration();
        configuration.setToken(token);
        return configuration;
    }

    <T> T readValue(String url, Class<T> type) throws PRGlooException {
        try {
            CloseableHttpResponse response = get(url);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new PRGlooException("Failed prgloo request " + url + " " + response.getStatusLine());
            }

            HttpEntity entity = response.getEntity();
            return objectMapper.readValue(entity.getContent(), type);
        } catch (IOException e) {
            throw new PRGlooException(e);
        }
    }

    CloseableHttpResponse get(String url) throws IOException {
        String fullUrl = baseUrl + url;
        HttpGet request = new HttpGet(fullUrl);
        request.addHeader("Content-Type", "application/json; charset=UTF-8");
        request.addHeader("X-Account-Id", config().getToken());
        return httpClient.execute(host, request);

    }
}
