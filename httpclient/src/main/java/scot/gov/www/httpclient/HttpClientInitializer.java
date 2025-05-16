package scot.gov.www.httpclient;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.net.URI;

public class HttpClientInitializer implements ServletContextListener {

    private static URI proxyUri;

    public static URI getProxyUri() {
        return proxyUri;
    }

    static void setProxyUri(URI proxyUri) {
        HttpClientInitializer.proxyUri = proxyUri;
    }
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String param = sce.getServletContext().getInitParameter("apache-http-client-proxy-url");
        if (StringUtils.isNotBlank(param)) {
            setProxyUri(URI.create(param));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing required
    }

}
