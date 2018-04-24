package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;

import java.util.List;

public class PublicationComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();
        request.setAttribute("document", document);

        HippoBean publicationParentFolder = document.getParentBean();
        List<HippoFolderBean> publicationFolders = publicationParentFolder.getChildBeansByName("documents");

        request.setAttribute("documents", publicationFolders.get(0).getDocuments());

    }

}

