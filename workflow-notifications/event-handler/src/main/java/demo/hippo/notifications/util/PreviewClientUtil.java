package demo.hippo.notifications.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class PreviewClientUtil {

    public static PreviewClient getPreviewClient(final String base) {
        PreviewClient client = JAXRSClientFactory.create(base, PreviewClient.class);
        setSecureConduit(client);
        return client;
    }

    public static void setSecureConduit(final Object client) {
        WebClient.getConfig(client).getRequestContext().put("http.redirect.max.same.uri.count", "5");
        WebClient.getConfig(client).getRequestContext().put(org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);
        final HTTPClientPolicy policy = WebClient.getConfig(client).getHttpConduit().getClient();
        policy.setAutoRedirect(true);
        HTTPConduit conduit = WebClient.getConfig(client)
                .getHttpConduit();
        TLSClientParameters params =
                conduit.getTlsClientParameters();
        if (params == null) {
            params = new TLSClientParameters();
            conduit.setTlsClientParameters(params);
        }
        params.setTrustManagers(new TrustManager[]{new
                TrustAllX509TrustManager()});
        params.setDisableCNCheck(true);
    }

    public static class TrustAllX509TrustManager
            implements X509TrustManager {

        /**
         * Empty array of certificate authority certificates.
         */
        private static final X509Certificate[] acceptedIssuers = new X509Certificate[]{};

        /**
         * Always trust for client SSL chain peer certificate chain with any authType authentication types.
         *
         * @param chain    the peer certificate chain.
         * @param authType the authentication type based on the client certificate.
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        /**
         * Always trust for server SSL chain peer certificate chain with any authType exchange algorithm types.
         *
         * @param chain    the peer certificate chain.
         * @param authType the key exchange algorithm used.
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }
}
