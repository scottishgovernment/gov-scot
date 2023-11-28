package scot.gov.plugins.field;

import org.hippoecm.frontend.model.ChildNodeProvider;
import org.hippoecm.frontend.model.JcrItemModel;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.frontend.types.IFieldDescriptor;
import org.hippoecm.repository.api.HippoSession;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class ExtendedChildNodeProvider extends ChildNodeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendedNodeFieldPlugin.class);

    private final IFieldDescriptor descriptor;

    private final JcrNodeModel prototype;

    public ExtendedChildNodeProvider(IFieldDescriptor descriptor, JcrNodeModel prototype, JcrItemModel<Node> itemModel) {
        super(descriptor, prototype, itemModel);
        this.prototype = prototype;
        this.descriptor = descriptor;
    }

    @Override
    public void addNew() {
        load();

        try {
            doAdd();
        } catch (RepositoryException ex) {
            LOG.error("Error creating child node", ex);
        }

    }

    void doAdd() throws RepositoryException {
        Node parent = (Node) getItemModel().getObject();
        if (parent == null) {
            LOG.warn("No parent available to initialize child node");
        }

        Node node;
        if (prototype != null) {
            HippoSession session = UserSession.get().getJcrSession();
            String src = prototype.getNode().getPath();
            String dest = parent.getPath() + "/" + descriptor.getPath();
            node = JcrUtils.copy(session, src, dest);
            moveToTop(new JcrNodeModel(node));
        } else {
            LOG.info("No prototype available to initialize child node for field {} with type {}", descriptor
                    .getName(), descriptor.getTypeDescriptor().getType());
            node = parent.addNode(descriptor.getPath(), descriptor.getTypeDescriptor().getType());
            moveToTop(new JcrNodeModel(node));
        }
        elements.addFirst(new JcrNodeModel(node));

    }
}
