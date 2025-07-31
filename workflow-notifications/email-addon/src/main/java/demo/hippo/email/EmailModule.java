package demo.hippo.email;

import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.onehippo.repository.modules.ProvidesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@ProvidesService(types = MailService.class)
public class EmailModule extends AbstractReconfigurableDaemonModule {

    private static Logger log = LoggerFactory.getLogger(EmailModule.class);

    private MailService mailService;
    private String cmsRoot = null;

    @Override
    protected void doConfigure(final Node moduleConfig) throws RepositoryException {
        if(moduleConfig.hasProperty("email.cmsroot")) {
            cmsRoot = moduleConfig.getProperty("email.cmsroot").getString();
        }
        if(mailService!=null) {
            mailService.setCmsRoot(cmsRoot);
        }
    }

    @Override
    protected void doInitialize(final Session session) {
        log.info("Initialized mail module");
        mailService = new MailServiceImpl();
        HippoServiceRegistry.register(mailService, MailService.class);
    }

    @Override
    protected void doShutdown() {
        HippoServiceRegistry.unregister(mailService, MailService.class);
    }
}
