package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Set fields required to correctly output google tag manager fields.
 *
 * We set the following attributes:
 * - gtmName: the format of the page, e.g. article
 * - gtmId: slug for this page
 * - gtmContainerId: the google tag manager container id
 * - gtmAuth: value to use for the gtm_auth parameter
 * - gtmEnv: value to use in the gtm_preview parameter
 */
public class GoogleTagManagerComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleTagManagerComponent.class);

    private HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        setGtmName(request);
        setGtmId(request);
        setMountDependentAttributes(request);
    }

    /**
     * set gtmName based on the page component from the resolved sitemap item
     *
     * the gtm name is the format of the page, e.g. 'article'
     */
    void setGtmName(HstRequest request) {
        HstComponentConfiguration componentConfig = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration();
        String gtmName = componentConfig.getName();
        request.setAttribute("gtmName", gtmName);
    }

    /**
     * set gtmId on the path from the resolved sitemap item
     * the gtmId is the slug of this page
     */
    void setGtmId(HstRequest request) {
        String gtmId = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getPathInfo();
        request.setAttribute("gtmId", gtmId);
    }

    /**
     * gtmContainerId, gtmAuth and gtmEnv are set based on the path stored on the mount.
     *
     * This path is used to look up a node containing the relevant values.
     */
    void setMountDependentAttributes(HstRequest request) {

        Mount mount = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getResolvedMount()
                .getMount();
        String gtmPath = mount.getProperty("govscot:gtm");

        if (isBlank(gtmPath)) {
            LOG.error("Mount has no govscot:gtm: {}", mount.getName());
            setEmptyGtmValues(request);
            return;
        }

        try {
            Node gtmNode = getGtmNode(request, gtmPath);
            if (gtmNode == null) {
                setEmptyGtmValues(request);
                return;
            }
            setGtmValues(request,
                    gtmNode.getProperty("govscot:containerid").getString(),
                    gtmNode.getProperty("govscot:auth").getString(),
                    gtmNode.getProperty("govscot:env").getString());
        } catch (RepositoryException e) {
            LOG.error("Unexpected repository exception trying to set gtm values, gtmPath is {}", gtmPath, e);
            setEmptyGtmValues(request);
        }
    }

    void setGtmValues(HstRequest request, String containerId, String auth, String env) {
        request.setAttribute("gtmAuth", auth);
        request.setAttribute("gtmEnv", env);
        request.setAttribute("gtmContainerId", containerId);
    }

    void setEmptyGtmValues(HstRequest request) {
        setGtmValues(request, "", "", "");
    }

    Node getGtmNode(HstRequest request, String path) throws RepositoryException {
        HstRequestContext requestContext = request.getRequestContext();
        Session session = requestContext.getSession();

        if (!session.nodeExists(path)) {
            LOG.error("gtm path does not exist: {}", path);
            return null;
        }

        Node gtmHandle = session.getNode(path);
        Node gtmNode = hippoUtils.getPublishedVariant(gtmHandle);
        if (gtmNode == null) {
            LOG.info("No published gtm document for path: {}", path);
        }

        return gtmNode;
    }

}