package scot.gov.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class HttpClientSource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientSource.class);

    private HttpClientSource() {
        // hide constructor
    }

    public static CloseableHttpClient newClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setConnectTimeout(1000 * 30)
                .setSocketTimeout(1000 * 30)
                .build();
        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> executionCount < 5;
        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionTimeToLive(50, TimeUnit.SECONDS)
                .setRetryHandler(retryHandler);

        // proxy setting
        if (HttpClientInitializer.getProxyUri() != null) {
            LOG.info("newClient, no proxy defined");
            // configure with the proxy
            URI proxyURI = HttpClientInitializer.getProxyUri();
            int port = proxyURI.getPort() == -1 ? 80 : proxyURI.getPort();
            HttpHost proxy = new HttpHost(proxyURI.getHost(), port, proxyURI.getScheme());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            builder.setRoutePlanner(routePlanner);
        }

        return builder.build();
    }

}
