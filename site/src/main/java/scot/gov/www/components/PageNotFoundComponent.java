package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.gov.www.beans.PageNotFound;

import static scot.gov.www.components.ArchiveUtils.isArchivedUrl;

public class PageNotFoundComponent extends CommonComponent {

    public void doBeforeRender(HstRequest request, HstResponse response) {

        if (isArchivedUrl(request)) {
            request.setAttribute("archiveUrl", ArchiveUtils.archiveUrl(request));
        }

        HstRequestContext context = request.getRequestContext();
        PageNotFound document = (PageNotFound) context.getContentBean();
        request.setAttribute("document", document);
        request.setAttribute("isPageNotFound", true);
        response.setStatus(404);
    }
}