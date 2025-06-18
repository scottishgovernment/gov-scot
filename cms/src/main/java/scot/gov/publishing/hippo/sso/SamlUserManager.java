package scot.gov.publishing.hippo.sso;

import org.apache.commons.lang.StringUtils;
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
        if (!validateAuthentication(creds)) {
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

    protected boolean validateAuthentication(SimpleCredentials creds) {
        log.info("Validating credentials: {}", creds);
        SSOUserState userState = PostAuthorisationFilter.getCurrentSSOUserState();

        if (userState != null) {
            // CMS context
            return StringUtils.isNotEmpty(userState.getCredentials().getUsername());
        } else {
            // Site context
            String samlId = (String) creds.getAttribute(SSOUserState.SAML_ID);
            if (StringUtils.isNotBlank(samlId)) {
                log.info("Authentication allowed to: {}", samlId);
                return true;
            }
        }
        return false;
    }

    @Override
    public void syncUserInfo(String userId) {
        super.syncUserInfo(userId);
        log.info("Sync user {}", userId);
    }

    protected void syncUser(Node user, Node group) throws RepositoryException {
        log.info("Auth sync user {}", user.getName());
        user.setProperty("hipposys:securityprovider", "saml");
        user.setProperty("hipposys:active", true);
        String userId = user.getName();
        Set<String> members = groupManger.getMembers(group);
        if (!members.contains(userId)) {
            members.add(userId);
            groupManger.setMembers(group, members);
        }
    }

}
