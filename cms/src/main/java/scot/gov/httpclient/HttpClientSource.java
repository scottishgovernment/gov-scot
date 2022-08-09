package scot.gov.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


public class HttpClientSource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientSource.class);

    private HttpClientSource() {
        // hide constructor
    }
    public static CloseableHttpClient newClient() {
        // proxy setting here...
        if (HttpClientInitializer.getProxyUri() == null) {
            LOG.info("newClient, no proxy defined");
            return HttpClients.createDefault();
        }

        // configure with the proxy
        URI proxyURI = HttpClientInitializer.getProxyUri();
        int port = proxyURI.getPort() == -1 ? 80 : proxyURI.getPort();
        HttpHost proxy = new HttpHost(proxyURI.getHost(), port, proxyURI.getScheme());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        return HttpClients.custom().setRoutePlanner(routePlanner).build();
    }

}
