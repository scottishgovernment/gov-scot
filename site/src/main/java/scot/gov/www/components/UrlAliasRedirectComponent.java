package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.HstResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static scot.gov.www.components.ArchiveUtils.isArchivedUrl;
import static scot.gov.www.components.ArchiveUtils.sendArchiveRedirect;

/**
 * Componenet used to support url aliases from Rubric.
 */
public class UrlAliasRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UrlAliasRedirectComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        // check if this url is a known url alias
        String url = findAlias(request);
        if (url != null) {
            LOG.info("Redirecting to url alias {} -> {}", request.getPathInfo(), url);
            HstResponseUtils.sendPermanentRedirect(request, response, url);
            return;
        }

        // check if this url is an archived url
        if (isArchivedUrl(request)) {
            sendArchiveRedirect(request.getPathInfo(), request, response);
            return;
        }

        // we do not know this url, send a 404
        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();
        request.setAttribute("document", document);
        request.setAttribute("isPageNotFound", true);
        response.setStatus(404);
    }

    private String findAlias(HstRequest request)  {
        try {
            Session session = request.getRequestContext().getSession();
            String path = String.format("/content/redirects/Aliases%s", request.getPathInfo());
            if (session.nodeExists(path)) {
                Node node = session.getNode(path);
                return node.getProperty("govscot:url").getString();
            }
            return null;
        } catch (RepositoryException e) {
            LOG.error("Failed to find url alias {}", request.getPathInfo(), e);
            return null;
        }
    }

}

