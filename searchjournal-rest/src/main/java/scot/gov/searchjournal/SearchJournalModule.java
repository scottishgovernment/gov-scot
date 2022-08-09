package scot.gov.searchjournal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class SearchJournalModule extends AbstractReconfigurableDaemonModule {

    private static final String FEATUREFLAG_PATH = "/internal/flags";

    private static final String HEALTHCHECK_PATH = "/internal/health";

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // no configuration needed
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        JAXRSInvoker invoker = new JAXRSInvoker();
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(new ObjectMapper());
        addEndpoint(FEATUREFLAG_PATH, new FeatureFlagsResource(session), invoker, jacksonJsonProvider);
        addEndpoint(HEALTHCHECK_PATH, new FunnelbackHealthcheck(session), invoker, jacksonJsonProvider);
    }

    void addEndpoint(String path, Object resource, JAXRSInvoker invoker, JacksonJsonProvider jacksonJsonProvider) {
        RepositoryJaxrsEndpoint endpoint
                = new CXFRepositoryJaxrsEndpoint(path).invoker(invoker).singleton(resource).singleton(jacksonJsonProvider);
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(FEATUREFLAG_PATH);
        RepositoryJaxrsService.removeEndpoint(HEALTHCHECK_PATH);
    }

}
