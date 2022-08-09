package scot.gov.www.searchjournal.funnelback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FunnelbackConfigurationInitializer implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackConfigurationInitializer.class);

    private static final String FUNNELBACK_API_KEY_PARAM = "funnelback-api-key";

    private static final String FUNNELBACK_API_URL_PARAM = "funnelback-api-url";

    private static FunnelbackConfiguration configuration = null;

    public static FunnelbackConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String apiKey = sce.getServletContext().getInitParameter(FUNNELBACK_API_KEY_PARAM);
        String apiUrl = sce.getServletContext().getInitParameter(FUNNELBACK_API_URL_PARAM);
        if (isNotBlank(apiKey) && isNotBlank(apiUrl)) {
            configuration = new FunnelbackConfiguration();
            configuration.setApiKey(apiKey);
            configuration.setApiUrl(apiUrl);
        } else {
            LOG.warn("FunnelbackImpl not configured");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing required
    }
}