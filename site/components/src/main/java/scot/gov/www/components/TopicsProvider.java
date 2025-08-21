package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.component.MapProvider;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TopicsProvider implements MapProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TopicsProvider.class);

    @Override
    public Map<String, String> get(HstRequestContext context) {
        HashMap<String, String> topics = new HashMap<>();

        try {
            Session session = context.getSession();
            if (session.nodeExists("/content/documents/govscot")) {
                HippoBean baseBean = context.getSiteContentBaseBean();
                HippoFolderBean topicsFolder = baseBean.getBean("topics", HippoFolderBean.class);

                String xpath = String.format("//*[(@hippo:paths='%s') and (@hippo:availability='live') and not(@jcr:primaryType='nt:frozenNode') and ((@jcr:primaryType='govscot:Issue' or @jcr:primaryType='govscot:AboutTopic' or @jcr:primaryType='govscot:Topic' or @jcr:primaryType='govscot:DynamicIssue'))]", topicsFolder.getIdentifier());
                Query queryObj = session
                        .getWorkspace()
                        .getQueryManager()
                        .createQuery(xpath, Query.XPATH);
                QueryResult result = queryObj.execute();
                NodeIterator it = result.getNodes();
                while (it.hasNext()) {
                    Node topic = it.nextNode();
                    topics.put(topic.getName(), topic.getProperty("govscot:title").getString());
                }
            }
            return topics;
        } catch (RepositoryException e) {
            LOG.error("Failed to get list of topics and issues", e);
            return Collections.emptyMap();
        }
    }

}
