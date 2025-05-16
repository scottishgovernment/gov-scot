package scot.gov.www.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.hosting.VirtualHosts;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.platform.model.HstModel;
import org.onehippo.cms7.services.cmscontext.CmsSessionContext;

import jakarta.servlet.http.HttpSession;

import static org.hippoecm.hst.core.container.ContainerConstants.CMS_REQUEST_RENDERING_MOUNT_ID;

public class MountUtils {

    private MountUtils() {
        // prevent instantiation
    }

    public static Mount getEditingMount(HstRequestContext hstRequestContext) {
        String mountId = getRenderingMountId(hstRequestContext);
        Mount mount = getEditingPreviewVirtualHosts(hstRequestContext).getMountByIdentifier(mountId);
        if (mount == null) {
            String msg = String.format("Could not find a Mount for identifier + '%s'", mountId);
            throw new IllegalStateException(msg);
        }
        if (!Mount.PREVIEW_NAME.equals(mount.getType())) {
            String msg = String.format("Expected a preview (decorated) mount but '%s' is not of " +
                    "type preview.", mount.toString());
            throw new IllegalStateException(msg);
        }
        return mount;
    }

    private static String getRenderingMountId( HstRequestContext hstRequestContext ) {
        HttpSession httpSession = hstRequestContext.getServletRequest().getSession();
        CmsSessionContext cmsSessionContext = CmsSessionContext.getContext(httpSession);
        String renderingMountId = (String) cmsSessionContext.getContextPayload().get(CMS_REQUEST_RENDERING_MOUNT_ID);
        if (renderingMountId == null) {
            throw new IllegalStateException("Could not find rendering mount id on request session.");
        }

        return renderingMountId;
    }

    private static VirtualHosts getEditingPreviewVirtualHosts(HstRequestContext hstRequestContext) {
        return getPreviewHstModel( hstRequestContext).getVirtualHosts();
    }

    private static HstModel getPreviewHstModel(HstRequestContext hstRequestContext ) {
        return (HstModel) hstRequestContext.getAttribute("org.hippoecm.hst.pagecomposer.jaxrs.services.PageComposerContextService.preview");
    }
}
