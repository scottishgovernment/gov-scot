package scot.gov.publications;

import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Loads {@link PublicationsConfiguration} from HST's platform.properties, via
 * HstServices.getComponentManager().getContainerConfiguration() - the same mechanism already used
 * elsewhere in gov-scot for environment-specific external service configuration (see
 * VuelioImporterJob's "vuelio.url", FunnelbackFactory's "squiz.*"). Puppet writes values directly
 * into platform.properties on the deployed Tomcat's conf directory (conf/platform-dev.properties
 * is the bundled local-dev default, copied there by the cargo plugin) rather than via process
 * environment variables.
 */
public final class PublicationsConfigurationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsConfigurationLoader.class);

    private static final String KEY_BUCKET = "publications.s3.bucket";

    private static final String KEY_KEY = "publications.s3.key";

    private static final String KEY_SECRET = "publications.s3.secret";

    private static final String KEY_REGION = "publications.s3.region";

    private static final String KEY_PATH = "publications.s3.path";

    private PublicationsConfigurationLoader() {
        // static only class
    }

    /**
     * Loaded fresh on every call rather than cached, since the HST container may not have
     * finished bootstrapping yet at the point this module itself initialises (the two are
     * separate subsystems within the same webapp, with no guaranteed startup order) - by the
     * time an actual upload comes in and this is called, HST is expected to be up.
     */
    public static PublicationsConfiguration load() {
        PublicationsConfiguration configuration = new PublicationsConfiguration();
        ContainerConfiguration containerConfiguration = containerConfiguration();
        if (containerConfiguration == null) {
            LOG.info("HST container configuration is not available, {} etc cannot be read yet", KEY_BUCKET);
            return configuration;
        }

        PublicationsConfiguration.S3 s3 = configuration.getS3();
        s3.setBucket(property(containerConfiguration, KEY_BUCKET, false));
        s3.setKey(property(containerConfiguration, KEY_KEY, false));
        s3.setSecret(property(containerConfiguration, KEY_SECRET, true));
        s3.setRegion(property(containerConfiguration, KEY_REGION, false));
        String path = property(containerConfiguration, KEY_PATH, false);
        if (path != null) {
            s3.setPath(path);
        }
        return configuration;
    }

    private static ContainerConfiguration containerConfiguration() {
        try {
            if (!HstServices.isAvailable()) {
                return null;
            }
            return HstServices.getComponentManager().getContainerConfiguration();
        } catch (RuntimeException e) {
            LOG.warn("Failed to read HST container configuration", e);
            return null;
        }
    }

    private static String property(ContainerConfiguration containerConfiguration, String key, boolean isSecret) {
        if (!containerConfiguration.containsKey(key)) {
            LOG.info("platform.properties key {} is not set", key);
            return null;
        }
        String value = trimToNull(containerConfiguration.getString(key));
        LOG.info("Using platform.properties key {}{}", key, isSecret ? " (value not logged)" : "=" + value);
        return value;
    }
}
