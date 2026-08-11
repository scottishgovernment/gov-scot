package scot.gov.www;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugin.config.impl.AbstractPluginDecorator;
import org.hippoecm.frontend.plugins.ckeditor.CKEditorNodePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Overrides the CKEditor "insert image" picker so that, for every {@code hippostd:html} field
 * in the CMS (wired in via {@code plugin.class} on
 * {@code /hippo:namespaces/hippostd/html/editor:templates/_default_/root}), it opens in the
 * gallery folder that mirrors the document currently being edited — the same folder
 * {@link GalleryFolderUtils} and {@link OrganiseGalleryImagesJob} file that document's images
 * into — rather than the gallery root.
 *
 * <p>For publication documents ({@link GalleryFolderUtils#isUnderPublications}) the mirrored
 * folder is always used: the editor's last-visited folder is disabled so it can never override
 * it, since a publication's images should always live in that publication's own gallery folder.
 * For every other document type, the last-visited folder (if one is recorded) still takes
 * priority, and the mirrored folder only kicks in as a fallback — the first time the picker is
 * opened, or once there's no last-visited folder to fall back on.
 *
 * <p>This works by decorating the {@code imagepicker} child plugin config that
 * {@link CKEditorNodePlugin} reads its {@code NodePickerControllerSettings} from, so that
 * {@code base.uuid} resolves (lazily, on every dialog open) to the mirrored gallery folder, and
 * {@code last.visited.enabled} is forced off when appropriate.
 */
public class GalleryFolderCKEditorNodePlugin extends CKEditorNodePlugin {

    private static final Logger LOG = LoggerFactory.getLogger(GalleryFolderCKEditorNodePlugin.class);

    public GalleryFolderCKEditorNodePlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected IPluginConfig getPluginConfig() {
        return new FieldPluginConfig(super.getPluginConfig());
    }

    private Node currentDocumentHandle() {
        Object modelObject = getDefaultModelObject();
        if (!(modelObject instanceof Node)) {
            return null;
        }
        try {
            return documentHandle((Node) modelObject);
        } catch (RepositoryException e) {
            LOG.warn("GalleryFolderCKEditorNodePlugin: could not resolve document handle for image picker", e);
            return null;
        }
    }

    private String resolveGalleryFolderUuid() {
        Node handle = currentDocumentHandle();
        if (handle == null) {
            return null;
        }
        try {
            Node galleryFolder = GalleryFolderUtils.ensureGalleryFolderForDocument(handle.getSession(), handle.getPath());
            handle.getSession().save();
            return galleryFolder.getIdentifier();
        } catch (RepositoryException e) {
            LOG.warn("GalleryFolderCKEditorNodePlugin: could not resolve gallery folder for image picker", e);
            return null;
        }
    }

    private boolean isCurrentDocumentAPublication() {
        Node handle = currentDocumentHandle();
        try {
            return handle != null && GalleryFolderUtils.isUnderPublications(handle.getPath());
        } catch (RepositoryException e) {
            LOG.warn("GalleryFolderCKEditorNodePlugin: could not resolve document path for image picker", e);
            return false;
        }
    }

    private static Node documentHandle(Node node) throws RepositoryException {
        Node ancestor = node;
        while (!ancestor.isNodeType("hippo:handle")) {
            ancestor = ancestor.getParent();
        }
        return ancestor;
    }

    /**
     * Decorates the field's own plugin config so that its {@value CONFIG_CHILD_IMAGE_PICKER}
     * child (read by {@link CKEditorNodePlugin} when building the image picker) is itself
     * decorated by {@link ImagePickerPluginConfig}.
     */
    private final class FieldPluginConfig extends AbstractPluginDecorator {

        FieldPluginConfig(IPluginConfig upstream) {
            super(upstream);
        }

        @Override
        public IPluginConfig getPluginConfig(Object key) {
            IPluginConfig config = super.getPluginConfig(key);
            if (!CONFIG_CHILD_IMAGE_PICKER.equals(key)) {
                return config;
            }
            return new ImagePickerPluginConfig(config != null ? config : DEFAULT_IMAGE_PICKER_CONFIG);
        }

        @Override
        protected Object decorate(Object object) {
            return object;
        }
    }

    /**
     * Overrides {@code base.uuid} (as read by {@code NodePickerControllerSettings.fromPluginConfig})
     * to resolve to the current document's mirrored gallery folder, and — for publication
     * documents only — forces {@code last.visited.enabled} off so that folder can never be
     * overridden by a previously-visited one.
     */
    private final class ImagePickerPluginConfig extends AbstractPluginDecorator {

        private static final String BASE_UUID = "base.uuid";
        private static final String LAST_VISITED_ENABLED = "last.visited.enabled";

        ImagePickerPluginConfig(IPluginConfig upstream) {
            super(upstream);
        }

        @Override
        public Object get(Object key) {
            if (BASE_UUID.equals(key)) {
                String uuid = resolveGalleryFolderUuid();
                if (uuid != null) {
                    return uuid;
                }
            } else if (LAST_VISITED_ENABLED.equals(key) && isCurrentDocumentAPublication()) {
                return Boolean.FALSE;
            }
            return super.get(key);
        }

        @Override
        protected Object decorate(Object object) {
            return object;
        }
    }
}
