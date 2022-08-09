package scot.gov.www.searchjournal.funnelback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.httpclient.HttpClientSource;
import scot.gov.searchjournal.JournalPositionSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Implements operations we want to perform with FunnelbackImpl: indexing content and maintaining the journal position.
 */
public class FunnelbackImpl implements Funnelback {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackImpl.class);

    private static final String SECURITY_TOKEN = "X-Security-Token";

    private static final String CHARSET = "UTF-8";

    private FunnelbackConfiguration configuration;

    CloseableHttpClient httpClient;

    HttpHost funnelbackHost;

    URI funnelBackUri;

    private final String positionUrl;

    private final String filters;

    public FunnelbackImpl(FunnelbackConfiguration configuration, String filters) {
        this.configuration = configuration;
        this.filters = filters;
        funnelBackUri = URI.create(configuration.getApiUrl());
        funnelbackHost = new HttpHost(funnelBackUri.getHost(), funnelBackUri.getPort(), funnelBackUri.getScheme());
        this.positionUrl = funnelbackCollectionUrl(FunnelbackCollection.JOURNAL.getCollectionName(), "https://www.gov.scot/journalposition");
        LOG.info("positionUrl {}", positionUrl);
        httpClient = HttpClientSource.newClient();
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOG.error("Failed to close httpclient", e);
        }
    }

    public void publish(String collection, String key, String html) throws FunnelbackException {
        String url = funnelbackCollectionUrl(collection, key);
        LOG.info("publish {}", url);
        try {
            HttpPut request = new HttpPut(url);
            request.setEntity(new StringEntity(html));
            request.setEntity(new StringEntity(html, Charset.forName(CHARSET)));
            execute(request);
        } catch (IOException e) {
            throw new FunnelbackException("Failed to index content", e);
        }
    }

    public void depublish(String collection, String key) throws FunnelbackException {
        String url = funnelbackCollectionUrl(collection, key);
        try {
            execute(new HttpDelete(url));
        } catch (IOException e) {
            throw new FunnelbackException("Failed to index content", e);
        }
    }

    public Calendar getJournalPosition() throws FunnelbackException {
        String position;
        try {
            position = fetchJournalPosition(positionUrl);
        } catch (IOException e) {
            throw new FunnelbackException("failed to fetch position", e);
        }

        if (position == null) {
            LOG.warn("No journal position found");
            return null;
        }

        try {
            ZonedDateTime dt = ZonedDateTime.parse(position);
            LOG.info("journal position is {}", dt);
            return GregorianCalendar.from(dt);
        } catch (DateTimeParseException e) {
            LOG.error("Invalid position {}", position);
            throw new FunnelbackException("Invalid position " + position, e);
        }
    }

    public void storeJournalPosition(Calendar position) throws FunnelbackException {

        try {
            storeJournalPosition(positionUrl, position);
            JournalPositionSource.getInstance().setLastJournalPosition(position);
        } catch (IOException e) {
            throw new FunnelbackException("failed to store journal position", e);
        }
    }

    String funnelbackCollectionUrl(String collection, String key) {
        try {
            String encodedKey = URLEncoder.encode(key, CHARSET);
            String encodedFilters = URLEncoder.encode(filters, CHARSET);
            return String.format(
                    "%s%s/documents?key=%s&filters=%s",
                    "/push-api/v2/collections/",
                    collection,
                    encodedKey,
                    encodedFilters);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UnsupportedEncodingException trying to encode position key", e);
        }
    }

    void execute(HttpRequestBase request) throws IOException, FunnelbackException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        request.addHeader(SECURITY_TOKEN, configuration.getApiKey());

        CloseableHttpResponse response = httpClient.execute(funnelbackHost, request);
        try {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new FunnelbackException("HTTP request to funnelback not successful: " + response.getStatusLine());
            }

            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity);
            LOG.debug("entity {}", entityString);
        } finally {
            stopWatch.stop();
            LOG.debug("{} execute took {}", request.getURI(), stopWatch.getTime());
            response.close();
        }
    }

    String fetchJournalPosition(String url) throws IOException, FunnelbackException {

        HttpGet request = new HttpGet(url);
        request.addHeader(SECURITY_TOKEN, configuration.getApiKey());
        CloseableHttpResponse response = httpClient.execute(funnelbackHost, request);
        try {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new FunnelbackException("HTTP request to funnelback not successful: " + response.getStatusLine());
            }

            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity);
            JsonNode jsonNode = new ObjectMapper().readTree(body);
            JsonNode pushContent = jsonNode.get("pushContent");
            if (pushContent.isNull()) {
                LOG.info("No journal position found");
                return null;
            }
            String content = jsonNode.get("pushContent").get("content").asText();
            String decodedContent =  new String(Base64.getDecoder().decode(content));
            LOG.info("fetchJournalPosition {}", decodedContent);
            return decodedContent;
        } finally {
            response.close();
        }
    }

    void storeJournalPosition(String url, Calendar position) throws IOException, FunnelbackException {

        if (position == null) {
            LOG.warn("No journal position to store.");
            return;
        }

        LOG.info("storeJournalPosition {}", url);
        HttpPut request = new HttpPut(url);
        request.addHeader(SECURITY_TOKEN, configuration.getApiKey());
        GregorianCalendar cal = (GregorianCalendar) position;
        ZonedDateTime zdt = cal.toZonedDateTime();
        LOG.info("storeJournalPosition {}", zdt);
        request.setEntity(new StringEntity(zdt.toString()));
        request.setHeader("X-Journal-Position", zdt.toString());
        CloseableHttpResponse response = httpClient.execute(funnelbackHost, request);
        try {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new FunnelbackException("Failed to store journal position: " + response.getStatusLine()) ;
            }
        } finally {
            response.close();
        }
    }
}