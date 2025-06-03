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

public class SsoUserManager extends DelegatingHippoUserManager {

    private static final Logger log = LoggerFactory.getLogger(SsoUserManager.class);

    private final GroupManager groupManager;

    public SsoUserManager(HippoUserManager delegatee, GroupManager groupManager) {
        super(delegatee);
        this.groupManager = groupManager;
    }

    @Override
    public boolean authenticate(SimpleCredentials creds) throws RepositoryException {
        if (!validSsoCredentials(creds)) {
            return false;
        }
        String userId = creds.getUserID();
        Node userNode;
        if (!hasUser(userId)) {
            userNode = createUser(userId);
        } else {
            userNode = getUser(userId);
        }
        saveUsers();
        return true;
    }

    protected boolean validSsoCredentials(SimpleCredentials creds) {
        log.info("Validating credentials for: {}", creds.getUserID());
        return creds.getAttribute(SsoAttributes.SSO_ID) != null;
    }

    @Override
    public void syncUserInfo(String userId) {
        super.syncUserInfo(userId);
        log.info("Sync user {}", userId);
        try {
            Node userNode = getUser(userId);
            syncUser(userNode, groupManager.getGroup("admin"));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void syncUser(Node user, Node group) throws RepositoryException {
        user.setProperty("hipposys:active", true);
        String userId = user.getName();
        Set<String> members = groupManager.getMembers(group);
        if (!members.contains(userId)) {
            members.add(userId);
            groupManager.setMembers(group, members);
        }
    }

}
