package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.PublicationPage;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PublicationComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationComponent.class);

    private static final String DOCUMENTS = "documents";

    private static final String PAGES = "pages";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();

        HippoBean publication = getPublication(document);
        setDocuments(publication, request);
        setPages(publication, document, request);
        request.setAttribute("document", publication);

        if (publication == null) {
            try {
                response.setStatus(404);
                response.forward("/pagenotfound");
            }  catch (IOException e) {
                throw new HstComponentException("Forward failed", e);
            }
        }
    }

    private HippoBean getPublication(HippoBean document) {
        if (document.isHippoFolderBean()) {
            List<HippoBean> publications = document.getChildBeans("govscot:Publication");
            if (publications.size() > 1) {
                LOG.warn("Multiple publications found in folder {}, will use first", document.getPath());
            }
            return publications.isEmpty() ? null : publications.get(0);
        }

        if (isPage(document)) {
            HippoBean publicationFolder = document.getParentBean().getParentBean();
            List<HippoBean> publications = publicationFolder.getChildBeans("govscot:Publication");
            if (publications.size() > 1) {
                LOG.warn("Multiple publications found in folder {}, will use first", publicationFolder.getPath());
            }
            return publications.isEmpty() ? null : publications.get(0);
        }

        return document;
    }
    private void setDocuments(HippoBean publication, HstRequest request) {

        HippoBean publicationFolder = publication.getParentBean();
        if (!hasDocuments(publication.getParentBean())) {
            return;
        }

        List<HippoFolderBean> documentFolders = publicationFolder.getChildBeansByName(DOCUMENTS);
        HippoFolderBean documentFolder = documentFolders.get(0);
        request.setAttribute(DOCUMENTS, documentFolder.getDocuments());

        // look for grouped documents which are stored in their own named sub-folders
        if (!documentFolder.getFolders().isEmpty()) {
            request.setAttribute("groupedDocumentFolders", documentFolder.getFolders());
        }
    }

    private void setPages(HippoBean publication, HippoBean document, HstRequest request) {
        HippoBean publicationFolder = publication.getParentBean();
        boolean hasPages = hasPages(publicationFolder);
        request.setAttribute("isMultiPagePublication", hasPages);

        if (!hasPages(publicationFolder)){
            return;
        }

        List<HippoFolderBean> pageFolders = publicationFolder.getChildBeansByName(PAGES);
        List<HippoDocumentBean> pages = pagestoInclude(pageFolders.get(0));
        HippoBean currentPage = isPage(document) ? document : pages.get(0);
        request.setAttribute(PAGES, pages);
        request.setAttribute("isMultiPagePublication", true);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("prev", prevBean(currentPage, pages));
        request.setAttribute("next", nextBean(currentPage, pages));
    }

    private boolean isPage(HippoBean document) {
        return document.getClass() == PublicationPage.class;
    }

    private boolean hasDocuments(HippoBean publicationParentFolder) {
        return hasChildBeans(publicationParentFolder.getChildBeansByName(DOCUMENTS));
    }

    private boolean hasPages(HippoBean publicationParentFolder) {
        return hasChildBeans(publicationParentFolder.getChildBeansByName(PAGES));
    }

    boolean hasChildBeans(List<HippoFolderBean> folders) {
        for (HippoFolderBean docFolder : folders) {
            if (docFolder.getDocumentSize() > 0 || docFolder.getFolders().size() > 0) {
                return true;
            }
        }
        return false;
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

