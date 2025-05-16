package scot.gov.searchjournal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.onehippo.repository.jaxrs.AuthorizingRepositoryJaxrsInvoker;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.onehippo.repository.jaxrs.RepositoryJaxrsService.HIPPO_REST_PERMISSION;

public class SearchJournalModule extends AbstractReconfigurableDaemonModule {

    private static final String FEATUREFLAG_PATH = "/flags";

    private static final String JOURNAL_PATH = "/journal";

    private static final String HEALTHCHECK_PATH = "/internal/health";

    private String modulePath;

    @Override
    protected void doConfigure(Node moduleConfig) throws RepositoryException {
        this.modulePath = moduleConfig.getParent().getPath();
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        JAXRSInvoker invoker = new AuthorizingRepositoryJaxrsInvoker(modulePath, HIPPO_REST_PERMISSION);
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(new ObjectMapper());
        addEndpoint(FEATUREFLAG_PATH, new FeatureFlagsResource(session), invoker, jacksonJsonProvider);
        addEndpoint(JOURNAL_PATH, new JournalPopulationResource(session), invoker, jacksonJsonProvider);
        addEndpoint(HEALTHCHECK_PATH, new FunnelbackHealthcheck(session), invoker, jacksonJsonProvider);
    }

    void addEndpoint(String path, Object resource, JAXRSInvoker invoker, JacksonJsonProvider jacksonJsonProvider) {
        RepositoryJaxrsEndpoint endpoint = new CXFRepositoryJaxrsEndpoint(path)
                .invoker(invoker)
                .singleton(resource)
                .singleton(jacksonJsonProvider);
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(FEATUREFLAG_PATH);
        RepositoryJaxrsService.removeEndpoint(JOURNAL_PATH);
        RepositoryJaxrsService.removeEndpoint(HEALTHCHECK_PATH);
    }

}
