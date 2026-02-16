package scot.gov.www.searchjournal.funnelback;

import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.SearchType;
import scot.gov.www.searchjournal.FunnelbackReconciliationLoop;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class FunnelbackFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackReconciliationLoop.class);

    private static final String ERROR_RATE_ATTRIBUTE = "errorRate";

    private static final String FILTERS_ATTRIBUTE = "filters";

    private FunnelbackFactory() {
        // hide implicit constructor
    }

    public static Funnelback newFunnelback(RepositoryJobExecutionContext context, Session session) throws RepositoryException {

        // decide what kind of funnelback instance to create based on the job context.
        String filters = filters(context);
        String searchType = SearchType.getSearchType(session);

        FunnelbackConfiguration funnelbackConfiguration = configuration(searchType);
        if (funnelbackConfiguration == null) {
            return null;
        }
        Funnelback funnelback = new FunnelbackImpl(funnelbackConfiguration, filters);

        LOG.info("filters: {}", filters);
        // for testing error conditions it is possible to configure the repository to add artificial errors to our
        // implementation of talking to funnelback
        double errorRate = 0.0;
        if (context.getAttributeNames().contains(ERROR_RATE_ATTRIBUTE)) {
            String errorRateString = context.getAttribute(ERROR_RATE_ATTRIBUTE);
            errorRate = Double.parseDouble(errorRateString);
        }

        if (errorRate > 0.0) {
            LOG.warn("WARNING, using FlakyFunnelback to simulate errors.  Error rate is {}", errorRate);
            funnelback = new FlakyFunnelback(funnelback, errorRate);
        }

        return new MetricsCollectingFunnelbackImpl(funnelback);
    }

    static FunnelbackConfiguration configuration(String searchType) {

        FunnelbackConfiguration configuration = new FunnelbackConfiguration();
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        if (!containerConfiguration.containsKey("squiz.admin.token")) {
            return null;
        }
        configuration.setSearchType(searchType);
        if ("funnelback-dxp".equals(searchType)) {
            String url = containerConfiguration.getString("squiz.admin.url");
            String clientId = containerConfiguration.getString("squiz.clientId");
            String token = containerConfiguration.getString("squiz.admin.token");
            configuration.setApiUrl(url);
            configuration.setClientId(clientId);
            configuration.setApiKey(token);
        }
        return configuration;
    }

    static String filters(RepositoryJobExecutionContext context) {
        return context.getAttributeNames().contains(FILTERS_ATTRIBUTE)
                ? context.getAttribute(FILTERS_ATTRIBUTE) : "";
    }
}