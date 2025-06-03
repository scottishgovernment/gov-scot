package scot.gov.publishing.hippo.sso;

import org.hippoecm.repository.security.group.GroupManager;
import org.hippoecm.repository.security.user.DelegatingHippoUserManager;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import java.util.Set;

public class SamlUserManager extends DelegatingHippoUserManager {

    private static final Logger log = LoggerFactory.getLogger(SamlUserManager.class);

    private final GroupManager groupManger;

    public SamlUserManager(HippoUserManager delegatee, GroupManager groupManager) {
        super(delegatee);
        this.groupManger = groupManager;
    }

    @Override
    public boolean authenticate(SimpleCredentials creds) throws RepositoryException {
        if (!validSamlCredentials(creds)) {
            return false;
        }
        String userId = creds.getUserID();
        Node userNode;
        if (!hasUser(userId)) {
            userNode = createUser(userId);
        } else {
            userNode = getUser(userId);
        }
        syncUser(userNode, groupManger.getGroup("admin"));
        return true;
    }

    protected boolean validSamlCredentials(SimpleCredentials creds) {
        log.info("Validating credentials: {}", creds);
        return creds.getAttribute(Saml2JcrCredentials.SAML_ID) != null;
    }

    @Override
    public void syncUserInfo(String userId) {
        super.syncUserInfo(userId);
        log.info("Sync user {}", userId);
    }

    protected void syncUser(Node user, Node group) throws RepositoryException {
        log.info("Auth sync user {}", user.getName());
        user.setProperty("hipposys:active", true);
        String userId = user.getName();
        Set<String> members = groupManger.getMembers(group);
        if (!members.contains(userId)) {
            members.add(userId);
            groupManger.setMembers(group, members);
        }
    }

}
