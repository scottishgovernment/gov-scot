package scot.gov.www;

import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.onehippo.cms7.services.HippoServiceRegistry.registerService;
import static org.onehippo.cms7.services.HippoServiceRegistry.unregisterService;

/**
 * Base class intended for out event handlers. Logs start and end time and provides a simple template
 */
public abstract class DaemonModuleBase implements DaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(DaemonModuleBase.class);

    protected Session session;

    /**
     * determine whether this class handles this type of event
     */
    public abstract boolean canHandleEvent(HippoWorkflowEvent event);

    /**
     * handle this event
     */
    public abstract void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException;

    @Override
    public void initialize(Session session) throws RepositoryException {
        this.session = session;

        HippoEventListenerRegistry.get().register(this);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        if (!canHandleEvent(event)) {
            return;
        }

        try {
            handleEventWithLogging(event);
        } catch (RepositoryException e) {
            LOG.error("Exception when calling session.refresh(false)", e);
        }
    }

    void handleEventWithLogging(HippoWorkflowEvent event) throws RepositoryException {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            LOG.error("{} Unexpected exception while doing simple JCR read operations, calling session.refresh(false), subject path is {}",
                    this.getClass().getName(), event.subjectPath(), e);
            session.refresh(false);
        } catch (Exception t) {
            LOG.error("{} Unexpected exception", this.getClass().getName(), t);
            throw t;
        }
    }

    @Override
    public void shutdown() {
        HippoEventListenerRegistry.get().unregister(this);
    }


}
