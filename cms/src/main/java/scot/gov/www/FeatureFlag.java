package scot.gov.www;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Lookup feature flags for event listeners and scheduled tasks.  Featured flags are stored as boolean propertries of the
 * node /content/featureflags/
 *
 * If the featured flags node does not exist or the flag in qwuestion does not exist then the flag will default to false.
 */
public class FeatureFlag {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureFlag.class);

    private static final String FEATURE_FLAGS_PATH = "/content/featureflags/";

    private final Session session;

    private final String flag;

    public FeatureFlag(Session session, String flag) {
        this.session = session;
        this.flag = flag;
    }

    public boolean isEnabled() {

        try {
            if (!session.nodeExists(FEATURE_FLAGS_PATH)) {
                LOG.warn("no feature flags defined, defaulting to {} = false", flag);
                return false;
            }

            Node featureFlags = session.getNode(FEATURE_FLAGS_PATH);
            if (!featureFlags.hasProperty(flag)) {
                LOG.warn("feature flag {} does not exist, defaulting to enabled = false", flag);
                return false;
            }

            return featureFlags.getProperty(flag).getBoolean();
        } catch (RepositoryException e) {
            LOG.warn("unexpected exception getting feature flag {}, defaulting to enabled = false", flag, e);
            return false;
        }
    }
}
