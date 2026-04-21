package scot.gov.www.importer;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjournal.FeatureFlag;
import scot.gov.www.importer.vuelio.VuelioConfiguration;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * Scheduled job to import press releases (news, speeches and correspondence).
 */
public class VuelioImporterJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(VuelioImporterJob.class);

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        Session systemSession = context.createSystemSession();
        Session session = null;
        try {
            Credentials credentials = new SimpleCredentials("news", "".toCharArray());
            session = systemSession.impersonate(credentials);
            doImports(session);
        } finally {
            if (session != null) {
                session.logout();
            }
            systemSession.logout();
        }
    }

    void doImports(Session session) throws RepositoryException {
        for (Importer importer : Importer.values()) {
            try {
                doImport(session, importer);
            } catch (RepositoryException | VuelioImporterException e) {
                LOG.error("failed ", e);
            }
        }
    }

    void doImport(Session session, Importer importer) throws RepositoryException {
        String flagname = "VuelioImporterJob" + importer.getName();
        FeatureFlag featureFlag = new FeatureFlag(session, flagname);
        if (!featureFlag.isEnabled()) {
            LOG.info("{} is disabled", flagname);
            return;
        }

        VuelioConfiguration config = config(importer);
        if (config == null) {
            LOG.info("no config for {}", importer.getName());
            return;
        }

        LOG.info("ContentImporter running {}", config.getImporter().getName());
        new VuelioImporter(session, config).doImport();
        LOG.info("ContentImporter finished {}", config.getImporter().getName());
    }

    VuelioConfiguration config(Importer importer) {
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        String url = containerConfiguration.getString("vuelio.url");
        String tokenProperty = "vuelio.token." + importer.getName();
        String token = containerConfiguration.getString(tokenProperty);
        if (StringUtils.isBlank(token)) {
            LOG.warn("no token {}", tokenProperty);
            return null;
        }

        VuelioConfiguration configuration = new VuelioConfiguration();
        configuration.setImporter(importer);
        configuration.setApi(url);
        configuration.setToken(token);
        return configuration;
    }

}
