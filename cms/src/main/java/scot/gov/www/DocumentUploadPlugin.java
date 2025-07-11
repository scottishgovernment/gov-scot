package scot.gov.www;

import org.apache.commons.lang.ArrayUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.hippoecm.frontend.behaviors.EventStoppingBehavior;
import org.hippoecm.frontend.editor.plugins.resource.ResourceHelper;
import org.hippoecm.frontend.editor.plugins.resource.ResourceUploadPlugin;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.jquery.upload.FileUploadViolationException;
import org.hippoecm.frontend.plugins.jquery.upload.single.FileUploadPanel;
import org.hippoecm.frontend.plugins.yui.upload.processor.DefaultFileUploadPreProcessorService;
import org.hippoecm.frontend.plugins.yui.upload.processor.FileUploadPreProcessorService;
import org.hippoecm.frontend.plugins.yui.upload.validation.DefaultUploadValidationService;
import org.hippoecm.frontend.plugins.yui.upload.validation.FileUploadValidationService;
import org.hippoecm.frontend.service.IEditor.Mode;
import org.hippoecm.repository.api.HippoNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hippoecm.frontend.editor.plugins.resource.ResourceHelper.getValueFactory;

/**
 * Extends built-in ResourceUploadPlugin class, and prevents
 * uploaded document text extraction.
 */
public class DocumentUploadPlugin extends ResourceUploadPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentUploadPlugin.class);
    public static final String DEFAULT_ASSET_VALIDATION_SERVICE_ID = "service.gallery.asset.validation";

    private final Mode mode;

    public DocumentUploadPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        mode = Mode.fromString(config.getString("mode"), Mode.EDIT);
        this.add(new Component[]{this.createFileUploadPanel()});
        this.add(new Behavior[]{new EventStoppingBehavior("click")});
    }

    private FileUploadPanel createFileUploadPanel() {
        FileUploadPanel panel = new FileUploadPanel("fileUpload", this.getPluginConfig(),
                this.getValidationService(), this.getPreProcessorService()) {
            @Override
            public void onFileUpload(FileUpload fileUpload) throws FileUploadViolationException {
                DocumentUploadPlugin.this.handleUpload(fileUpload);
            }
        };
        panel.setVisible(mode == Mode.EDIT);
        return panel;
    }

    private FileUploadValidationService getValidationService() {
        return DefaultUploadValidationService.getValidationService(this.getPluginContext(), this.getPluginConfig(),
                DEFAULT_ASSET_VALIDATION_SERVICE_ID);
    }

    private FileUploadPreProcessorService getPreProcessorService() {
        return DefaultFileUploadPreProcessorService.getPreProcessorService(this.getPluginContext(),
                this.getPluginConfig());
    }

    /**
     * Handles the file upload from the form.
     *
     * @param upload the {@link FileUpload} containing the upload information
     */
    private void handleUpload(FileUpload upload) throws FileUploadViolationException {
        String fileName = upload.getClientFileName();
        String mimeType = upload.getContentType();
        JcrNodeModel nodeModel = (JcrNodeModel) this.getDefaultModel();
        Node node = nodeModel.getNode();

        try {
            ResourceHelper.setDefaultResourceProperties(node, mimeType, upload.getInputStream(), fileName);
            setEmptyHippoTextBinary(node);
        } catch (IOException | RepositoryException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Cannot upload resource", ex);
            } else {
                LOG.error("Cannot upload resource: {}", ex.getMessage());
            }
            throw new FileUploadViolationException(ex.getMessage());
        }
    }

    /**
     * Sets an empty 'hippo:text' binary. This is to prevent
     * expensive text extraction from occurring.
     *
     * @param node the Node to set the 'hippo:text' property on
     */
    private static void setEmptyHippoTextBinary(final Node node) {
        String nodePath = null;
        try {
            nodePath = node.getPath();
            final ByteArrayInputStream emptyByteArrayInputStream = new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY);
            // Set empty 'hippo:text' binary
            node.setProperty(HippoNodeType.HIPPO_TEXT, getValueFactory(node).createBinary(emptyByteArrayInputStream));
        } catch (RepositoryException e) {
            LOG.error("Unable to store empty hippo:text binary for node at {}.", nodePath, e);
        }
    }
}
