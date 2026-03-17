package scot.gov.www.importer;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.searchjournal.FeatureFlag;
import scot.gov.www.importer.vuelio.VuelioClient;
import scot.gov.www.importer.vuelio.VuelioConfiguration;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scheduled job to import press releases (news, speeches and correspondence).
 */
public class VuelioImporterJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(VuelioImporterJob.class);

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        String namesAttr = context.getAttribute("names");
        if (namesAttr == null) {
            LOG.info("No imnporter names in job config");
            return;
        }

        Session systemSession = context.createSystemSession();
        Session session = null;
        String[] importerNames = namesAttr.split(",");
        try {
            Credentials credentials = new SimpleCredentials("news", "".toCharArray());
            session = systemSession.impersonate(credentials);
            for (String importerName : importerNames) {
                try {
                    doImport(session, importerName);
                } catch (RepositoryException | VuelioImporterException e) {
                    LOG.error("failed ", e);
                }
            }
        } finally {
            if (session != null) {
                session.logout();
            }
            systemSession.logout();
        }
    }

    void doImport(Session session, String importerName) throws RepositoryException {

        VuelioConfiguration config = config(importerName);
        if (config == null) {
            LOG.info("No config for {}", importerName);
            return;
        }

        String flagname = "VuelioImporterJob" + config.getName();
        FeatureFlag featureFlag = new FeatureFlag(session, flagname);
        if (!featureFlag.isEnabled()) {
            LOG.info("{} is disabled", flagname);
            return;
        }

        LOG.info("ContentImporter running {}", config.getName());
        new VuelioImporter(session, config).doImport();
        LOG.info("ContentImporter finished {}", config.getName());

    }
    VuelioConfiguration config(String name) {
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        String prefix = "vuelio." + name + ".";
        String url = containerConfiguration.getString("vuelio.url");
        String token = containerConfiguration.getString(prefix + "token");
        if (StringUtils.isBlank(token)) {
            return null;
        }

        VuelioConfiguration configuration = new VuelioConfiguration();
        configuration.setName(name);
        configuration.setApi(url);
        configuration.setToken(token);
        return configuration;
    }

}