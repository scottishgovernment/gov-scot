package scot.gov.publishing.hippo.sso;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.hippoecm.repository.security.DelegatingSecurityProvider;
import org.hippoecm.repository.security.RepositorySecurityProvider;
import org.hippoecm.repository.security.SecurityProvider;
import org.hippoecm.repository.security.group.GroupManager;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.List;

/**
 * Custom <code>org.hippoecm.repository.security.SecurityProvider</code> implementation.
 * <p>
 */
public class SsoSecurityProvider extends DelegatingSecurityProvider {

    private static final Logger log = LoggerFactory.getLogger(SsoSecurityProvider.class);

    private HippoUserManager userManager;

    private SecurityProvider internalProvider;

    /**
     * Constructs by creating the default <code>RepositorySecurityProvider</code> to delegate all the other calls
     * except of authentication calls.
     */
    public SsoSecurityProvider() throws RepositoryException {
        super(new RepositorySecurityProvider());
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by SSO claims.
     */
    @Override
    public UserManager getUserManager() throws RepositoryException {
        if (userManager == null) {
            HippoUserManager hippoUserManager = (HippoUserManager) super.getUserManager();
            userManager = new SsoUserManager(hippoUserManager, getGroupManager());
        }
        return userManager;
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by SSO claims.
     */
    @Override
    public UserManager getUserManager(Session session) throws RepositoryException {
        HippoUserManager hippoUserManager = (HippoUserManager) super.getUserManager(session);
        return new SsoUserManager(hippoUserManager, getGroupManager());
    }

    @Override
    public void synchronizeOnLogin(SimpleCredentials creds) throws RepositoryException {
        GroupManager groupManager = internalProvider.getGroupManager();
        Node user = userManager.getUser(creds.getUserID());
        List<String> groups = ((List<String>) creds.getAttribute(SsoAttributes.SSO_GROUPS))
                .stream()
                .map(g -> g.substring(g.lastIndexOf(' ') + 1))
                .toList();
        for (String group : groups) {
            Node groupNode = groupManager.getGroup(group);
            groupManager.addMember(groupNode, creds.getUserID());
        }
        groupManager.saveGroups();
    }

    public void setInternalProvider(SecurityProvider internal) {
        this.internalProvider = internal;
    }

}
