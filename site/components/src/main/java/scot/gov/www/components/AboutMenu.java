package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.sitemenu.EditableMenu;
import org.hippoecm.hst.core.sitemenu.EditableMenuItem;
import org.hippoecm.hst.core.sitemenu.HstSiteMenu;

public class AboutMenu extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        final HstRequestContext requestContext = request.getRequestContext();
        HstSiteMenu menu = requestContext.getHstSiteMenus().getSiteMenu("about");

        String isMobile = this.getComponentLocalParameter("mobile");
        request.setAttribute("isMobile", isMobile);

        if (menu != null) {
            EditableMenu editable = menu.getEditableMenu();
            EditableMenuItem item = editable.getDeepestExpandedItem();

            if (item != null && item.isRepositoryBased() && item.getDepth() > 0) {

                HippoBean aboutIndexPageBean = this.getBeanForResolvedSiteMapItem(request, item.resolveToSiteMapItem());
                HippoBean aboutFolderBean = aboutIndexPageBean.getParentBean();

                EditableMenuItem aboutMenuItem = new RepoBasedMenuItem((HippoFolderBean) aboutFolderBean, item, request, requestContext.getContentBean());

                request.setAttribute("aboutMenuItem", aboutMenuItem);
            }
        }

    }
}
