package scot.gov.www;

import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.*;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.equalsAny;

public class SitemapEventListener extends AbstractReconfigurableDaemonModule {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapEventListener.class);

    public static final String SITEMAP_ROOT = "/content/sitemaps/";

    private static final String PUBLISH_INTERACTION = "default:handle:publish";

    private static final String DEPUBLISH_INTERACTION = "default:handle:depublish";

    private static final String LAST_MOD = "govscot:lastMod";

    private static final String NT_UNSTRUCTURED = "nt:unstructured";

    private static final String EXCLUDED_TYPES = "govscot:excludedTypes";

    private HippoUtils hippoUtils = new HippoUtils();

    private UrlSource urlSource = new UrlSource();

    private Set<String> exludedTypes = new HashSet<>();

    @Override
    protected void doConfigure(Node node) throws RepositoryException {
        if (node.hasProperty(EXCLUDED_TYPES)) {
            Value[] excludedTypesValues = node.getProperty(EXCLUDED_TYPES).getValues();
            for (Value value : excludedTypesValues) {
                exludedTypes.add(value.getString());
            }
        }
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        HippoEventListenerRegistry.get().register(this);
    }

    @Override
    protected void doShutdown() {
        HippoEventListenerRegistry.get().unregister(this);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {

        if (!shouldHandleEvent(event)) {
            return;
        }

        try {
            handleEventWithLogging(event);
        } catch (RepositoryException e) {
            LOG.error("Exception when calling session.refresh(false)", e);
        } catch (Exception t) {
            LOG.error("{} Unexpected exception", this.getClass().getName(), t);
            throw t;
        }
    }

    void handleEventWithLogging(HippoWorkflowEvent event) throws RepositoryException {

        // handle the event and ensure that session.refresh(false) is called if there is a RepositoryException
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            LOG.error("RepositoryException trying to index {}", event.subjectId(), e);
            session.refresh(false);
        } catch (Exception t) {
            LOG.error("{} Unexpected exception", this.getClass().getName(), t);
            throw t;
        }
    }

    public void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNodeByIdentifier(event.subjectId());
        Node node = hippoUtils.getVariant(handle);
        Node sitemapNode = getSitemapSiteNode(node);

        // when running in cargo the sitemap node is not present, so just return early
        if (sitemapNode == null) {
            LOG.warn("no sitemap node for {}", node.getPath());
            return;
        }

        if (isExcludedType(node)) {
            return;
        }

        String url = urlSource.url(node);
        removeExistingSitemapNode(url, node);
        if (PUBLISH_INTERACTION.equals(event.interaction())) {
            ensureSitemapEntry(node, url);
        }
        session.save();
    }

    boolean isExcludedType(Node node) throws RepositoryException {
        for (String type : exludedTypes) {
            if (node.isNodeType(type)) {
                return true;
            }
        }
        return false;
    }

    void removeExistingSitemapNode(String url, Node node) throws RepositoryException {
        String sitename = UrlSource.sitename(node);
        String handleIdentifier = node.getParent().getIdentifier();
        String xpath = String.format("/jcr:root/content/sitemaps/%s//uuid-%s", sitename, handleIdentifier);
        hippoUtils.executeXpathQuery(session, xpath, currentNode -> {
            LOG.info("removing sitemap node for {}, {}, {}", url, node.getPath(), currentNode.getPath());
            currentNode.remove();
        });
    }

    void ensureSitemapEntry(Node node, String url) throws RepositoryException {

        Calendar lastModified = node.getProperty("hippostdpubwf:lastModificationDate").getDate();
        Node monthNode = getMonthNode(node, lastModified);
        Node urlNode = monthNode.addNode("uuid-" + node.getParent().getIdentifier(), NT_UNSTRUCTURED);
        LOG.info("update sitemap node for {}, {}", url, urlNode.getPath());
        urlNode.setProperty("govscot:loc", url);
        urlNode.setProperty(LAST_MOD, lastModified);
    }

    Node getMonthNode(Node node, Calendar lastModified) throws RepositoryException {
        String year = Integer.toString(lastModified.get(Calendar.YEAR));
        String month = Integer.toString(lastModified.get(Calendar.MONTH) + 1);

        Node sitemapNode = getSitemapSiteNode(node);
        if (!sitemapNode.hasNode(year)) {
            LOG.warn("No year node {}, {}", sitemapNode.getPath(), year);
            return null;
        }

        Node yearNode = sitemapNode.getNode(year);
        if (!yearNode.hasNode(month)) {
            LOG.warn("No month node {}, {}", yearNode.getPath(), month);
            return null;
        }

        return yearNode.getNode(month);
    }

    Node getSitemapSiteNode(Node node) throws RepositoryException {
        Node sitemapRoot = session.nodeExists(SITEMAP_ROOT) ? session.getNode(SITEMAP_ROOT) : null;
        if (sitemapRoot == null) {
            return null;
        }
        String sitename = UrlSource.sitename(node);
        return sitemapRoot.getNode(sitename);
    }

    /**
     * we are only interested in successful publish and depublish events for news and publications
     */
    boolean shouldHandleEvent(HippoWorkflowEvent event) {
        if (!event.success()) {
            return false;
        }

        return equalsAny(event.interaction(), PUBLISH_INTERACTION, DEPUBLISH_INTERACTION);
    }
}