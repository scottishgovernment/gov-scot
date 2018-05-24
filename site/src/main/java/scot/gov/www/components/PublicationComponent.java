package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.www.beans.PublicationPage;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PublicationComponent extends BaseHstComponent {

    private static final String PAGENOTFOUND = "/pagenotfound";
    private static final String FORWARDFAILED = "forward failed";

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        HippoBean document;
        HippoBean publicationParentFolder;

        try {
            document = context.getContentBean();
            if(document == null) {
                response.setStatus(404);
                response.forward(PAGENOTFOUND);
                return;
            }
        }  catch (IOException e) {
            throw new HstComponentException(FORWARDFAILED, e);
        }

        if (document.getClass() == PublicationPage.class){
            HippoBean pagesFolder = document.getParentBean();
            publicationParentFolder = pagesFolder.getParentBean();

            HippoBean index;

            try {
                index = (HippoBean) publicationParentFolder.getChildBeansByName("index").get(0);
                if(index == null) {
                    response.setStatus(404);
                    response.forward(PAGENOTFOUND);
                    return;
                }
            }  catch (IOException e) {
                throw new HstComponentException(FORWARDFAILED, e);
            }

            request.setAttribute("document", index);
        } else if ("index".equals(document.getName())) {
            publicationParentFolder = document.getParentBean();
            request.setAttribute("document", document);
        } else {
            try {
                response.setStatus(404);
                response.forward(PAGENOTFOUND);
                return;
            }  catch (IOException e) {
                throw new HstComponentException(FORWARDFAILED, e);
            }
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
            List<HippoDocumentBean> pages = pagestoInclude(pageFolders.get(0));
            request.setAttribute("pages", pages);
            request.setAttribute("isMultiPagePublication", true);

            HippoBean currentPage;

            if (document.getClass() == PublicationPage.class){
                currentPage = document;
            } else {
                currentPage = pages.get(0);
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

    private List<HippoDocumentBean> pagestoInclude(HippoFolderBean pagesFolder) {

        return pagesFolder.getDocuments().stream().filter(this::includePage).collect(toList());
    }

    private boolean includePage(HippoDocumentBean page) {
        // do not include pages that have been marked as a contents page by the migration
        return !page.getProperty("govscot:contentsPage", false);
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

