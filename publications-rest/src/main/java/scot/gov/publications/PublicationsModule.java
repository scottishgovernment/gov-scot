package scot.gov.publications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.jaxrs.provider.MultipartProvider;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.jaxrs.api.ManagedUserSessionInvoker;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.health.PublicationsHealthcheck;
import scot.gov.publications.repo.PublicationRepository;
import scot.gov.publications.repo.PublicationRepositoryJcrImpl;
import scot.gov.publications.rest.PublicationsExceptionMapper;
import scot.gov.publications.rest.PublicationsResource;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class PublicationsModule extends AbstractReconfigurableDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsModule.class);

    private static final String PATH = "/publications-importer";

    private static final String HEALTH_PATH = "/internal/publications-importer";

    private static final String MAX_UPLOAD_SIZE = Long.toString(50 * 1024 * 1024L);

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // nothing needed
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        LOG.info("Initialising publications rest api");

        PublicationRepository publicationRepository = new PublicationRepositoryJcrImpl(session);
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        PublicationsResource resource = new PublicationsResource(session, publicationRepository);
        ManagedUserSessionInvoker invoker = new ManagedUserSessionInvoker(session);
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(objectMapper);
        MultipartProvider multipartProvider = new MultipartProvider();
        // match the 50MB upload cap the standalone importer enforced via UndertowOptions.MAX_ENTITY_SIZE
        multipartProvider.setAttachmentMaxSize(MAX_UPLOAD_SIZE);
        RequestLogger requestLogger = new RequestLogger();
        PublicationsExceptionMapper exceptionMapper = new PublicationsExceptionMapper();
        RepositoryJaxrsEndpoint endpoint  = new CXFRepositoryJaxrsEndpoint(PATH)
                .invoker(invoker)
                .singleton(resource)
                .singleton(requestLogger)
                .singleton(exceptionMapper)
                .singleton(jacksonJsonProvider)
                .singleton(multipartProvider);
        RepositoryJaxrsService.addEndpoint(endpoint);

        // unauthenticated so infrastructure health checks can reach it without a CMS login
        PublicationsHealthcheck healthcheck = new PublicationsHealthcheck(publicationRepository);
        RepositoryJaxrsEndpoint healthEndpoint = new CXFRepositoryJaxrsEndpoint(HEALTH_PATH)
                .invoker(new JAXRSInvoker())
                .singleton(healthcheck)
                .singleton(jacksonJsonProvider);
        RepositoryJaxrsService.addEndpoint(healthEndpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
        RepositoryJaxrsService.removeEndpoint(HEALTH_PATH);
    }

}
