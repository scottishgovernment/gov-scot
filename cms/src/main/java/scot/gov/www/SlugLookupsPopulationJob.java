package scot.gov.www;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;
import scot.gov.publishing.searchjournal.FeatureFlag;
import scot.gov.publishing.searchjournal.SessionSaver;
import scot.gov.publishing.sluglookup.SlugLookups;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.substringAfter;

public class SlugLookupsPopulationJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(SlugLookupsPopulationJob.class);

    private static final String SITE = "govscot";

    private static final String SLUG = "govscot:slug";

    HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        Session session = context.createSystemSession();
        try {
            FeatureFlag featureFlag = new FeatureFlag(session, "SlugLookupsPopulationJob");
            if (featureFlag.isEnabled()) {
                doExecute(session);
                deactivateJob(session);
            }
        } catch (RepositoryException e) {
            LOG.error("RepositoryException during slug population", e);
            throw e;
        } finally {
            session.logout();
        }
    }

    void doExecute(Session session) throws RepositoryException {
        populateNewsLookups(session);
        populatePublicationLookups(session);
    }

    void deactivateJob(Session session) throws RepositoryException {
        LOG.info("Deactivating SlugLookupsPopulationJob");
        FeatureFlag featureFlag = new FeatureFlag(session, "SlugLookupsPopulationJob");
        featureFlag.setEnabled(false);
        session.save();
    }

    void populatePublicationLookups(Session session) throws RepositoryException {
        Node publications = session.getNode("/content/documents/govscot/publications");
        SortedMap<String, String> previewMap = new TreeMap<>();
        SortedMap<String, String> liveMap = new TreeMap<>();
        hippoUtils.apply(publications.getNodes(), this::isFolder, type ->
            hippoUtils.apply(type.getNodes(), year ->
                hippoUtils.apply(year.getNodes(), month ->
                    hippoUtils.apply(month.getNodes(),
                        publicationFolder -> recordPublicationSlugs(publicationFolder, previewMap, liveMap)))));
        LOG.info("{} preview entries, {} live entries");
        createLookups(session, "publications","preview", previewMap);
        createLookups(session, "publications","live", liveMap);
    }

    void populateNewsLookups(Session session) throws RepositoryException {
        Node news = session.getNode("/content/documents/govscot/news");
        SortedMap<String, String> previewMap = new TreeMap<>();
        SortedMap<String, String> liveMap = new TreeMap<>();
        hippoUtils.apply(news.getNodes(), this::isFolder, year ->
            hippoUtils.apply(year.getNodes(), month ->
                hippoUtils.apply(month.getNodes(),
                    newsHandle -> recordNewsSlugs(newsHandle, previewMap, liveMap))));

        LOG.info("{} preview entries, {} live entries");
        createLookups(session, "news","preview", previewMap);
        createLookups(session, "news","live", liveMap);
    }

    void createLookups(Session session, String type, String mount, SortedMap<String, String> lookups) throws RepositoryException {
        LOG.info("createLookups {}, {}, {}", type, mount, lookups.size());
        SessionSaver sessionSaver = new SessionSaver(session, 100);
        SlugLookups slugLookups = new SlugLookups(session);
        for (Map.Entry<String, String> entry : lookups.entrySet()) {
            LOG.info("{}, {} ... {} -> {}", type, mount, entry.getKey(), entry.getValue());
            String slug = entry.getKey();
            String path = entry.getValue();
            slugLookups.ensureLookupPath(slug, path, SITE, type, mount);
            sessionSaver.save();
        }
    }

    boolean isFolder(Node node) throws RepositoryException {
        return node.isNodeType("hippostd:folder");
    }

    void recordNewsSlugs(Node newsHandle, SortedMap<String, String> previewMap, SortedMap<String, String> liveMap) throws RepositoryException {
        LOG.info("createNewsLookup {}", newsHandle.getPath());
        Node variant = hippoUtils.getVariant(newsHandle);
        String slug = variant.getProperty(SLUG).getString();
        String path = path(newsHandle.getPath());
        if (hippoUtils.isPublished(variant)) {
            liveMap.put(slug, path);
        }
        previewMap.put(slug, path);
    }

    void recordPublicationSlugs(Node pubFolder, SortedMap<String, String> previewMap, SortedMap<String, String> liveMap) throws RepositoryException {
        LOG.info("recordPublicationSlugs {}", pubFolder.getPath());
        Node index = publicationNode(pubFolder);
        if (index == null) {
            return;
        }

        Node variant = hippoUtils.getVariant(index);
        if (!variant.hasProperty(SLUG)) {
            LOG.warn("variant no slug {}", pubFolder.getPath());
            return;
        }
        String slug = variant.getProperty(SLUG).getString();
        String path = path(index.getPath());
        if (hippoUtils.isPublished(variant)) {
            liveMap.put(slug, path);
        }
        previewMap.put(slug, path);
    }

    Node publicationNode(Node folder) throws RepositoryException {
        if (folder.hasNode("index")) {
            return folder.getNode("index");
        }

        if (!folder.hasNodes()) {
            LOG.warn("folder is empty {}", folder.getPath());
            return null;
        }

        Node firstHandle = hippoUtils.find(folder.getNodes(), n -> n.isNodeType("hippo:handle"));
        if (firstHandle == null) {
            LOG.info("no index {}", folder.getPath());
            return null;
        }

        if (firstHandle.hasNodes() && firstHandle.getNodes().nextNode().hasProperty(SLUG)) {
            return firstHandle;
        }

        LOG.info("no index {}", folder.getPath());
        return null;
    }

    String path(String fullPath) {
        return substringAfter(fullPath, "/content/documents/govscot");
    }
}
