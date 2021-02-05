package scot.gov.www;

import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.repository.util.JcrUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;
import static org.hippoecm.repository.HippoStdNodeType.PUBLISHED;

public class HippoUtils {

    public Node getPublishedVariant(Node handle) throws RepositoryException {
        return getVariantWithState(handle, PUBLISHED);
    }

    public Node getVariantWithState(Node handle, String state) throws RepositoryException {
        String name = handle.getName();
        NodeIterator iterator = handle.getNodes(name);
        return find(iterator, node -> hasState(node, state));
    }

    boolean hasState(Node node, String state) throws RepositoryException {
        String nodeState = JcrUtils.getStringProperty(node, HIPPOSTD_STATE, null);
        return state.equals(nodeState);
    }

    /**
     * Have factored this into a non static method for the purposes of mocking in unit tests.
     */
    public HstQueryBuilder createQuery(HippoBean scope) {
        return HstQueryBuilder.create(scope);
    }

    @FunctionalInterface
    public interface ThrowingPredicate {
        boolean test(Node t) throws RepositoryException;
    }

    public Node find(NodeIterator it, ThrowingPredicate predicate) throws RepositoryException {
        while (it.hasNext()) {
            Node node = it.nextNode();
            if (predicate.test(node)) {
                return node;
            }
        }
        return null;
    }
}