package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.www.beans.PublicationPage;

import java.util.List;

public class PublicationComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();

        HippoBean publicationParentFolder;

        if (document.getClass() == PublicationPage.class){
            HippoBean pagesFolder = document.getParentBean();
            publicationParentFolder = pagesFolder.getParentBean();
            HippoBean index = (HippoBean) publicationParentFolder.getChildBeansByName("index").get(0);
            request.setAttribute("document", index);
        } else {
            publicationParentFolder = document.getParentBean();
            request.setAttribute("document", document);
        }

        List<HippoFolderBean> documentFolders = publicationParentFolder.getChildBeansByName("documents");
        List<HippoFolderBean> pageFolders = publicationParentFolder.getChildBeansByName("pages");

        if (!documentFolders.isEmpty()) {
            HippoFolderBean documentFolder = documentFolders.get(0);
            request.setAttribute("documents", documentFolder.getDocuments());

            // look for grouped documents which are stored in their own named sub-folders
            if (!documentFolder.getFolders().isEmpty()){
                request.setAttribute("groupedDocumentFolders", documentFolder.getFolders());
            }
        }

        if (!pageFolders.isEmpty()){
            List<HippoDocumentBean> pages = pageFolders.get(0).getDocuments();

            // if there is a Contents page (named 0), remove it
            HippoDocumentBean contentsPage = null;

            for(HippoDocumentBean page:pages){
                if ("0".equals(page.getName())){
                    contentsPage = page;
                }
            }

            pages.remove(contentsPage);

            request.setAttribute("pages", pages);
            request.setAttribute("isMultiPagePublication", true);

            HippoBean currentPage;

            if (document.getClass() == PublicationPage.class){
                currentPage = document;
            } else {
                currentPage = (HippoBean) pageFolders.get(0).getChildBeansByName("1").get(0);
            }

            request.setAttribute("currentPage", currentPage);

            HippoBean prev = prevBean(currentPage, pages);
            HippoBean next = nextBean(currentPage, pages);

            request.setAttribute("prev", prev);
            request.setAttribute("next", next);
        } else {
            request.setAttribute("isMultiPagePublication", false);
        }
    }

    private HippoBean prevBean(HippoBean currentPage, List<HippoDocumentBean> pages) {
        int index = pages.indexOf(currentPage);

        // if this is the first page, then there is no previous bean
        if (index == 0) {
            return null;
        }

        return pages.get(index - 1);
    }

    private HippoBean nextBean(HippoBean currentPage, List<HippoDocumentBean> pages) {
        int index = pages.indexOf(currentPage);

        // if this is the last page, then there is no next bean
        if (index == pages.size() - 1) {
            return null;
        }

        return pages.get(index + 1);
    }

}

