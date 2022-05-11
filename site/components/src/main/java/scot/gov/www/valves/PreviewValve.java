package scot.gov.www.valves;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.valves.AbstractOrderableValve;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import static scot.gov.www.valves.PreviewKeyUtils.isPreviewMount;

public class PreviewValve extends AbstractOrderableValve {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewValve.class);

    @Override
    public void invoke(ValveContext context) throws ContainerException {
        HstRequestContext requestContext = context.getRequestContext();
        Mount resolvedMount = requestContext.getResolvedMount().getMount();
        try {
            if (isPreviewMount(requestContext)) {
                doInvoke(context, requestContext, resolvedMount);
            }
        } catch (RepositoryException repositoryException) {
            LOG.error("Something with repo went wrong while accessing this node {}.", requestContext.getSiteContentBaseBean(), repositoryException);
        } catch (IOException ioException) {
            LOG.error("Something with IO went wrong while accessing this node {}.", requestContext.getSiteContentBaseBean(), ioException);
        } finally {
            context.invokeNext();
        }
    }

    void doInvoke(ValveContext valveContext, HstRequestContext requestContext, Mount resolvedMount)
            throws RepositoryException, IOException {

        //fetching the content bean
        HippoBean contentBean = requestContext.getContentBean();

        //fetching the previewkey
        Set<String> previewKeys = PreviewKeyUtils.getPreviewKeys(
                valveContext.getServletRequest(), valveContext.getServletResponse(), resolvedMount);

        if (isExempt(requestContext)) {
            return;
        }

        if (contentBean == null) {
            LOG.info("Preview request doesn't contain content bean");
            requestContext.getServletResponse().sendError(403);
            return;
        }

        // intercepting requests having the id in the url
        if (previewKeys.isEmpty()) {
            LOG.info("Preview request doesn't contain preview key");
            requestContext.getServletResponse().sendError(403);
            return;
        }

        if (!hasValidStagingKey(contentBean, previewKeys)) {
            LOG.info("Preview key {} for document {} is invalid or preview link has expired.", previewKeys, contentBean.getPath());
            requestContext.getServletResponse().sendError(403);
        }
    }

    /**
     * anything starting with /fragments is exempt as it is a dynamic endpoint and have to implement its
     * own logic determining visibility.
     */
    boolean isExempt(HstRequestContext context) {
        return StringUtils.startsWith(context.getBaseURL().getPathInfo(), "/fragments/");
    }

    boolean hasValidStagingKey(HippoBean contentBean, Set<String> previewKeys) throws RepositoryException {
        Node unpublishedNode = getUnpublishedNode(contentBean);
        NodeIterator iterator = unpublishedNode.getNodes("previewId");
        while (iterator.hasNext()) {
            Node node = iterator.nextNode();
            Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null);
            String key = JcrUtils.getStringProperty(node, "staging:key", "");
            if (previewKeys.contains(key) && isValid(expirationCalendar)) {
                return true;
            }
        }
        return false;
    }

    Node getUnpublishedNode(HippoBean contentBean) throws RepositoryException {
        Node handle = contentBean.getNode().getParent();
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (HippoStdNodeType.UNPUBLISHED.equals(state)) {
                return variant;
            }
        }
        return null;
    }

    private boolean isValid(final Calendar expirationCalendar) {
        return expirationCalendar == null || Calendar.getInstance().before(expirationCalendar);
    }

}

