package scot.gov.www.importer.vuelio;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.httpclient.HttpClientSource;
import scot.gov.www.importer.vuelio.rest.ContentItem;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class VuelioClient {

    private static final Logger LOG = LoggerFactory.getLogger(VuelioClient.class);

    CloseableHttpClient httpClient;

    HttpHost host;

    String baseUrl;

    ObjectMapper objectMapper = new ObjectMapper();

    public VuelioClient() {
        VuelioConfiguration config = config();
        URI uri = URI.create(config.getApi());
        baseUrl = config.getApi();
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        httpClient = HttpClientSource.newClient();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOG.error("Failed to close httpclient", e);
        }
    }

    public static VuelioConfiguration config() {
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        String url = containerConfiguration.getString("vuelio.url");
        String token = containerConfiguration.getString("vuelio.token");
        if (StringUtils.isBlank(token)) {
            return null;
        }

        VuelioConfiguration configuration = new VuelioConfiguration();
        configuration.setApi(url);
        configuration.setToken(token);
        return configuration;
    }

    public List<ContentItem> content() throws VuelioException {
        ContentItem[] contentArray = readValue(baseUrl, ContentItem[].class);
        return Arrays.stream(contentArray).toList();
    }

    <T> T readValue(String url, Class<T> type) throws VuelioException {
        try {
            CloseableHttpResponse response = get(url);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new VuelioException("Failed Vuelio request " + url + " " + response.getStatusLine());
            }

            HttpEntity entity = response.getEntity();
            return objectMapper.readValue(entity.getContent(), type);
        } catch (IOException e) {
            throw new VuelioException(e);
        }
    }

    CloseableHttpResponse get(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.addHeader("Content-Type", "application/json; charset=UTF-8");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + config().getToken());
        return httpClient.execute(host, request);

    }
}
