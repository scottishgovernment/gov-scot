package scot.gov.www.importer.sink;

import org.onehippo.forge.content.pojo.model.ContentNode;
import scot.gov.www.importer.domain.PressRelease;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class Policies {

    public void addPolicies(PressRelease pressRelease, ContentNode node, Session session) throws RepositoryException {
        List<String> policyIds = new ArrayList<>();
        if (!pressRelease.getPolicies().isEmpty()) {
            Map<String, String> policies = getPolicyTags(session);
            policies.forEach((key, value) -> {
                if (releaseContainsPolicy(pressRelease, value)) {
                    policyIds.add(key);
                }
            });
        }
        node.setProperty("govscot:policyTags", policyIds.toArray(new String [policyIds.size()]));

    }

    boolean releaseContainsPolicy(PressRelease release, String policy) {
        return release.getPolicies().stream().anyMatch(s -> equalsIgnoreCase(s, policy));
    }

    Map<String, String> getPolicyTags(Session session) throws RepositoryException {
            Map<String, String> map = new HashMap<>();
            String path = String.format("/content/documents/govscot/valuelists/policyTags/policyTags");
            Node policyValueList = session.getNode(path);
            NodeIterator it = policyValueList.getNodes("selection:listitem");
            while (it.hasNext()) {
                Node n = it.nextNode();
                map.put(
                        n.getProperty("selection:key").getString(),
                        n.getProperty("selection:label").getString());
            }
            return map;
    }

}
