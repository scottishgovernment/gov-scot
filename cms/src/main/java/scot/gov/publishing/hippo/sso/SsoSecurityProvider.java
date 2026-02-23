package scot.gov.publishing.hippo.sso;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.value.StringValue;
import org.hippoecm.frontend.plugins.cms.admin.users.User;
import org.hippoecm.repository.security.AbstractSecurityProvider;
import org.hippoecm.repository.security.DelegatingSecurityProvider;
import org.hippoecm.repository.security.ManagerContext;
import org.hippoecm.repository.security.SecurityProviderContext;
import org.hippoecm.repository.security.group.GroupManager;
import org.hippoecm.repository.security.group.RepositoryGroupManager;
import org.hippoecm.repository.security.user.HippoUserManager;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

/**
 * Custom SecurityProvider that wraps the repository user manager with an
 * SSO-aware user manager and copies OIDC claims to the user node on login.
 *
 * <p>Extends {@link AbstractSecurityProvider} rather than
 * {@link DelegatingSecurityProvider} in order to override {@link #syncUser}.
 * This allows OpenID Connect claims, such as names and email address, to be
 * synchronized to the repository user, when {@link #synchronizeOnLogin} in
 * the superclass is called.
 */
public class SsoSecurityProvider extends AbstractSecurityProvider {

    private SecurityProviderContext context;

    @Override
    public void init(SecurityProviderContext context) throws RepositoryException {
        this.context = context;
        Session session = context.getSession();

        ManagerContext userContext = userContext(session);
        this.userManager = new SsoUserManager(userContext);

        this.groupManager = new RepositoryGroupManager();
        this.groupManager.init(groupContext(session));
    }

    @Override
    public UserManager getUserManager(Session session) throws RepositoryException {
        return new SsoUserManager(userContext(session));
    }

    @Override
    public GroupManager getGroupManager(Session session) throws RepositoryException {
        RepositoryGroupManager groupManager = new RepositoryGroupManager();
        groupManager.init(groupContext(session));
        return groupManager;
    }

    @Override
    protected void syncUser(SimpleCredentials creds, HippoUserManager userMgr) throws RepositoryException {
        if (creds.getAttribute(SsoAttributes.SSO_ID) != null) {
            Node user = userMgr.getUser(creds.getUserID());
            copyAttribute(creds, SsoAttributes.SSO_EMAIL, user, User.PROP_EMAIL);
            copyAttribute(creds, SsoAttributes.SSO_GIVEN_NAME, user, User.PROP_FIRSTNAME);
            copyAttribute(creds, SsoAttributes.SSO_FAMILY_NAME, user, User.PROP_LASTNAME);
        }
        super.syncUser(creds, userMgr);
    }

    private void copyAttribute(SimpleCredentials creds, String attributeName, Node node, String propertyName)
            throws RepositoryException {
        String attribute = (String) creds.getAttribute(attributeName);
        node.setProperty(propertyName, new StringValue(attribute));
    }

    private ManagerContext userContext(Session session) throws RepositoryException {
        return new ManagerContext(
                session, context.getProviderPath(),
                context.getUsersPath(), context.isMaintenanceMode());
    }

    private ManagerContext groupContext(Session session) throws RepositoryException {
        return new ManagerContext(
                session, context.getProviderPath(),
                context.getGroupsPath(), context.isMaintenanceMode());
    }

}
