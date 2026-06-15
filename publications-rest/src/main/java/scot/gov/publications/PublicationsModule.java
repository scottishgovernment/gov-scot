package scot.gov.publications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.cxf.jaxrs.provider.MultipartProvider;
import org.flywaydb.core.Flyway;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.jaxrs.api.ManagedUserSessionInvoker;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.repo.PublicationRepositorySqlImpl;
import scot.gov.publications.rest.PublicationsExceptionMapper;
import scot.gov.publications.rest.PublicationsResource;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.Clock;

public class PublicationsModule extends AbstractReconfigurableDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationsModule.class);

    private static final String PATH = "/publications-importer";

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        dbUrl = module.getProperty("publications.db.url").getString();
        dbUsername = module.getProperty("publications.db.username").getString();
        dbPassword = module.getProperty("publications.db.password").getString();
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        LOG.info("Initialising publications rest api");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setMaximumPoolSize(5);

        Flyway.configure().dataSource(dataSource).load().migrate();

        QueryRunner queryRunner = new QueryRunner(dataSource);
        PublicationRepositorySqlImpl publicationRepository = new PublicationRepositorySqlImpl(queryRunner, Clock.systemDefaultZone());

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        PublicationsResource resource = new PublicationsResource(session, publicationRepository);
        ManagedUserSessionInvoker invoker = new ManagedUserSessionInvoker(session);
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(objectMapper);
        MultipartProvider multipartProvider = new MultipartProvider();
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
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}
