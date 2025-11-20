package scot.gov.www.importer.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * ImporterModule to register the healthcheck endpoint for Vuelio importer
 */
public class ImporterModule extends AbstractReconfigurableDaemonModule {

    private static final String PATH = "/internal/vuelio-importer";

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // nothing required
    }

    @Override
    protected void doInitialize(Session var1) throws RepositoryException {
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        Healthcheck resource = new Healthcheck(session);
        RepositoryJaxrsEndpoint endpoint =
                new CXFRepositoryJaxrsEndpoint(PATH)
                        .invoker(new org.apache.cxf.jaxrs.JAXRSInvoker())
                        .singleton(resource)
                        .singleton(new JacksonJsonProvider(mapper))
                        .singleton(new ErrorHandler())
                        .singleton(mapper);
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}