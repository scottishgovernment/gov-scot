package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.PublicationPage;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class PublicationComponent extends AbstractPublicationComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationComponent.class);

    private static final String PAGES = "pages";

    private static final String ISMULTIPAGE = "isMultiPagePublication";

    @Override
    protected void populateRequest(HippoBean publication, HippoBean document, HstRequest request, HstResponse response) {
        setDocuments(publication, request);
        setPages(publication, document, request);
        request.setAttribute("document", publication);
    }

    protected HippoBean getPublication(HippoBean document) {
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
            // only include folders that have visible documents.
            List<HippoFolderBean> folders = documentFolder.getFolders()
                    .stream().filter(this::hasDocuments).collect(toList());
            request.setAttribute("groupedDocumentFolders", folders);
        }
    }

    boolean hasDocuments(HippoFolderBean folderBean) {
        return !folderBean.getDocuments().isEmpty();
    }

    private void setPages(HippoBean publication, HippoBean document, HstRequest request) {
        HippoBean publicationFolder = publication.getParentBean();

        List<HippoFolderBean> pageFolders = publicationFolder.getChildBeansByName(PAGES);

        if (pageFolders.isEmpty()){
            request.setAttribute(ISMULTIPAGE, false);
            return;
        }

        List<HippoDocumentBean> pages = pagestoInclude(pageFolders.get(0));

        if (pages.isEmpty()){
            request.setAttribute(ISMULTIPAGE, false);
            return;
        }

        HippoBean currentPage = isPage(document) && includePage(document) ? document : pages.get(0);
        request.setAttribute(PAGES, pages);
        request.setAttribute(ISMULTIPAGE, true);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("prev", prevBean(currentPage, pages));
        request.setAttribute("next", nextBean(currentPage, pages));
    }

    private boolean isPage(HippoBean document) {
        return document.getClass() == PublicationPage.class;
    }

    private List<HippoDocumentBean> pagestoInclude(HippoFolderBean pagesFolder) {
        return pagesFolder.getDocuments().stream().filter(this::includePage).collect(toList());
    }

    private boolean includePage(HippoBean page) {
        // do not include pages that have been marked as a contents page by the migration
        return !page.getSingleProperty("govscot:contentsPage", false);
    }


}

