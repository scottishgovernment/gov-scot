package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.PageNotFound;

import static scot.gov.www.components.ArchiveUtils.isArchivedUrl;

public class PageNotFoundComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PageNotFoundComponent.class);

    public void doBeforeRender(HstRequest request, HstResponse response) {

        if (isArchivedUrl(request)) {
            request.setAttribute("archiveUrl", ArchiveUtils.archiveUrl(request));
        }

        HstRequestContext context = request.getRequestContext();
        PageNotFound document = (PageNotFound) context.getContentBean();
        LOG.info("404 for {}", request.getRequestURL(), request.getPathInfo());
        request.setAttribute("document", document);
        request.setAttribute("isPageNotFound", true);

        response.setStatus(404);
    }
}