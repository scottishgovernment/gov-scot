package scot.gov.www;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.hippo.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.LocalDate;

import static javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED;

/**
 * SitemapEventListener maintains the data structure used to efficiently deliver the sitemap.xml's
 *
 * This scheduled job runs once a day to ensure that the year and month nodes required for the next day exist.  This
 * ensures that the listener does not have to create or modify any nodes that are not specific to the page that has
 * changed.
 */
public class SitemapJob implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapJob.class);

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {
        LOG.info("running SitemapJob");
        Session session = context.createSystemSession();
        Node sitemap = session.getNode("/content/sitemaps");
        new HippoUtils().apply(sitemap.getNodes(), this::ensureSitemapNodes);
        session.save();
    }

    void ensureSitemapNodes(Node sitemap) throws RepositoryException {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String year = Integer.toString(tomorrow.getYear());
        String month = Integer.toString(tomorrow.getMonth().getValue());
        Node yearNode;
        if (sitemap.hasNode(year)) {
            yearNode = sitemap.getNode(year);
        } else {
            yearNode = sitemap.addNode(year, NT_UNSTRUCTURED);
            LOG.info("adding sitemap node {}", yearNode.getPath());
        }

        if (!yearNode.hasNode(month)) {
            Node monthNode = yearNode.addNode(month, NT_UNSTRUCTURED);
            LOG.info("adding sitemap node {}", monthNode.getPath());
        }
    }
}
