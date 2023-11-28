package scot.gov.plugins.field;

import org.apache.wicket.model.IModel;
import org.hippoecm.frontend.editor.TemplateEngineException;
import org.hippoecm.frontend.editor.plugins.field.NodeFieldPlugin;
import org.hippoecm.frontend.model.AbstractProvider;
import org.hippoecm.frontend.model.JcrItemModel;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.types.IFieldDescriptor;
import org.hippoecm.frontend.types.ITypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

public class ExtendedNodeFieldPlugin extends NodeFieldPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendedNodeFieldPlugin.class);

    public ExtendedNodeFieldPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected AbstractProvider<Node, JcrNodeModel> newProvider(IFieldDescriptor descriptor, ITypeDescriptor type, IModel<Node> nodeModel) {

        try {
            final JcrNodeModel prototype = (JcrNodeModel) getTemplateEngine().getPrototype(type);
            return new ExtendedChildNodeProvider(descriptor, prototype, new JcrItemModel<>(nodeModel.getObject()));
        } catch (final TemplateEngineException ex) {
            LOG.warn("Could not find prototype", ex);
            return null;
        }

    }

}
