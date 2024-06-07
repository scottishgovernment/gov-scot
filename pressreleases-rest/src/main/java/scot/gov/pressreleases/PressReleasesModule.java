package scot.gov.pressreleases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.onehippo.repository.jaxrs.RepositoryJaxrsService.HIPPO_REST_PERMISSION;

/**
 * PressReleasesModule to register the PressReleasesResource used by the pressreleases service to create content.
 */
public class PressReleasesModule extends AbstractReconfigurableDaemonModule {

    // From the hippo docs here:
    // https://documentation.bloomreach.com/library/concepts/hippo-services/repository-jaxrs-service.html
    //
    // The repository authorization model is implemented such that checking for a permission on a path that does not
    // exist for any user always succeeds. In other words, it is highly recommended to use a path that is guaranteed
    // to exist for the authorization check. For Repository daemon modules, best practise is to use the modules
    // configuration root for this.
    //
    private static final String PATH = "/internal/pressreleases";

    private String modulePath;

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        this.modulePath = module.getParent().getPath();
    }

    @Override
    protected void doInitialize(Session var1) throws RepositoryException {
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        PressReleaseResource resource = new PressReleaseResource(session);
        RepositoryJaxrsEndpoint endpoint = new RepositoryJaxrsEndpoint(PATH)
                .singleton(resource)
                .authorized(modulePath, HIPPO_REST_PERMISSION)
                .singleton(new JacksonJsonProvider(mapper))
                .singleton(new ErrorHandler())
                .singleton(new PressReleaseRequestLogger())
                .singleton(mapper);
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}
