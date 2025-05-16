package scot.gov.www.redirects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.onehippo.repository.jaxrs.*;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.onehippo.repository.jaxrs.RepositoryJaxrsService.HIPPO_REST_PERMISSION;

public class RedirectsModule extends AbstractReconfigurableDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectsModule.class);

    private static final String PATH = "/redirects";

    private String modulePath;

    @Override
    protected void doConfigure(Node moduleConfig) throws RepositoryException {
        this.modulePath = moduleConfig.getParent().getPath();
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        LOG.info("Initialising redirects rest api");
        RedirectsRepository redirectsRepository = new RedirectsRepository(session);
        RedirectsResource resource = new RedirectsResource(redirectsRepository);
        JAXRSInvoker invoker = new AuthorizingRepositoryJaxrsInvoker(modulePath, HIPPO_REST_PERMISSION);
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(new ObjectMapper());
        RepositoryJaxrsEndpoint endpoint  = new CXFRepositoryJaxrsEndpoint(PATH)
                .invoker(invoker)
                .singleton(resource)
                .singleton(jacksonJsonProvider);
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}