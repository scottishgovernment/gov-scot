package scot.gov.publications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.repo.PublicationRepositoryJcrImpl;
import scot.gov.publications.rest.PublicationsResource;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class PublicationsModule extends AbstractReconfigurableDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsModule.class);

    private static final String PATH = "/publications-importer";

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // no configuration needed
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        LOG.info("Initialising publications rest api");
        PublicationRepositoryJcrImpl publicationRepository = new PublicationRepositoryJcrImpl(session);
        PublicationsResource resource = new PublicationsResource(session, publicationRepository);
        JAXRSInvoker invoker = new JAXRSInvoker();
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(new ObjectMapper());
        RequestLogger requestLogger = new RequestLogger();
        RepositoryJaxrsEndpoint endpoint  = new CXFRepositoryJaxrsEndpoint(PATH)
                .invoker(invoker)
                .singleton(resource)
                .singleton(requestLogger)
                .singleton(jacksonJsonProvider);
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}
