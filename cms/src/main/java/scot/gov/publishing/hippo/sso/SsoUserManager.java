package scot.gov.publishing.hippo.sso;

import org.hippoecm.repository.api.NodeNameCodec;
import org.hippoecm.repository.security.ManagerContext;
import org.hippoecm.repository.security.user.AbstractUserManager;
import org.hippoecm.repository.security.user.DelegatingHippoUserManager;
import org.hippoecm.repository.security.user.RepositoryUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SSO-aware user manager that adds case-insensitive user ID resolution.
 *
 * <p>Uses delegation rather than extending {@code AbstractUserManager} because
 * {@code AbstractUserManager} declares {@code hasUser} and {@code getUser} as
 * {@code final}. Delegation is the only way to intercept these methods to add
 * case-insensitive lookup via the resolved user ID cache.
 */
public class SsoUserManager extends DelegatingHippoUserManager {

    private static final Logger LOG = LoggerFactory.getLogger(SsoUserManager.class);

    private final Session session;

    // Maps user IDs (as provided by the IdP) to the actual user IDs in the repository.
    private final ConcurrentMap<String, String> resolvedUserIds = new ConcurrentHashMap<>();

    public SsoUserManager(ManagerContext context) throws RepositoryException {
        this(new RepositoryUserManager(), context);
    }

    private SsoUserManager(AbstractUserManager repoUserManager, ManagerContext context)
            throws RepositoryException {
        super(repoUserManager);
        repoUserManager.init(context);
        this.session = context.getSession();
    }

    @Override
    public boolean hasUser(String userId) throws RepositoryException {
        if (super.hasUser(userId)) {
            return true;
        }
        String resolved = resolvedUserIds.get(userId);
        return resolved != null && super.hasUser(resolved);
    }

    @Override
    public boolean isActive(String userId) throws RepositoryException {
        return super.isActive(resolveFromCache(userId));
    }

    @Override
    public boolean isPasswordExpired(String userId) throws RepositoryException {
        return super.isPasswordExpired(resolveFromCache(userId));
    }

    private String resolveFromCache(String userId) {
        String resolved = resolvedUserIds.get(userId);
        return resolved != null ? resolved : userId;
    }

    @Override
    public Node getUser(String userId) throws RepositoryException {
        Node user = super.getUser(userId);
        if (user != null) {
            return user;
        }
        String resolved = resolvedUserIds.get(userId);
        if (resolved != null) {
            return super.getUser(resolved);
        }
        return null;
    }

    @Override
    public boolean authenticate(SimpleCredentials creds) throws RepositoryException {
        if (creds.getAttribute(SsoAttributes.SSO_ID) != null) {
            return resolveUserId(creds.getUserID()) != null;
        }
        return super.authenticate(creds);
    }

    /**
     * Resolve a user ID case-insensitively. If an exact match exists, returns it directly.
     * Otherwise searches all users for a case-insensitive match, caches the mapping, and
     * returns the actual mixed-case user ID. Returns null if no match is found.
     */
    String resolveUserId(String userId) throws RepositoryException {
        if (super.hasUser(userId)) {
            LOG.debug("resolveUserId: {} found by exact match", userId);
            return userId;
        }

        String cached = resolvedUserIds.get(userId);
        if (cached != null) {
            LOG.debug("resolveUserId: {} to {} from cache", userId, cached);
            return cached;
        }

        // Try to find the user by email address, which may have been set to lower-case
        // during a previous SSO login
        String userIdByEmail = resolveUserIdByEmail(userId);
        if (userIdByEmail != null) {
            resolvedUserIds.put(userId, userIdByEmail);
            LOG.info("resolveUserId: {} to {} by email query", userId, userIdByEmail);
            return userIdByEmail;
        }

        // Fall back to linear scan of all users
        String userIdByScan = resolveUserIdByScan(userId);
        if (userIdByScan != null) {
            LOG.info("resolveUserId: {} to {} by linear scan", userId, userIdByScan);
            resolvedUserIds.put(userId, userIdByScan);
            return userIdByScan;
        }
        LOG.info("resolveUserId: {} not found", userId);
        return null;
    }

    /**
     * Query for a user node whose hipposys:email matches the given email address
     * and whose name is a case-insensitive match for the email address.
     * The email property is set during SSO login by synchronizeOnLogin, so this
     * avoids a linear scan on subsequent logins (e.g. after a restart clears the cache).
     */
    private String resolveUserIdByEmail(String userId) throws RepositoryException {
        String sql = "SELECT * FROM [hipposys:user] WHERE [hipposys:email] = $email";
        Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.JCR_SQL2);
        query.bindValue("email", session.getValueFactory().createValue(userId));
        NodeIterator nodes = query.execute().getNodes();
        while (nodes.hasNext()) {
            String nodeName = NodeNameCodec.decode(nodes.nextNode().getName());
            if (nodeName.equalsIgnoreCase(userId)) {
                return nodeName;
            }
        }
        return null;
    }

    /**
     * Scan all users in the repository, looking for a case-insensitive match.
     */
    private String resolveUserIdByScan(String userId) throws RepositoryException {
        NodeIterator nodes = listUsers(0, 0);
        while (nodes.hasNext()) {
            Node node = nodes.nextNode();
            String nodeName = NodeNameCodec.decode(node.getName());
            if (nodeName.equalsIgnoreCase(userId)) {
                return nodeName;
            }
        }
        return null;
    }

}
