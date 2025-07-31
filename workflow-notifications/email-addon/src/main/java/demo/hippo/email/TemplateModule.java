package demo.hippo.email;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.onehippo.repository.modules.ProvidesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@ProvidesService(types = TemplateService.class)
public class TemplateModule extends AbstractReconfigurableDaemonModule {

    private static Logger log = LoggerFactory.getLogger(TemplateModule.class);
    private FreemarkerTemplateService service;
    private String basePath = null;

    @Override
    protected void doConfigure(final Node moduleConfig) throws RepositoryException {
        if (moduleConfig.hasProperty("base.template.path")) {
            basePath = moduleConfig.getProperty("base.template.path").getString();
        }
        if (service != null) {
            service.setBasePath(basePath);
        }
    }

    @Override
    protected void doInitialize(final Session session) throws RepositoryException {
        log.info("Initialized template module");
        service = new FreemarkerTemplateService(session);
        service.setBasePath(basePath);
        HippoServiceRegistry.register(service, TemplateService.class);
    }

    @Override
    protected void doShutdown() {
        HippoServiceRegistry.unregister(service, TemplateService.class);
    }
}
