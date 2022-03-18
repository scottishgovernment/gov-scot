package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.DocumentInformation;
import scot.gov.www.beans.PublicationPage;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PublicationComponent extends AbstractPublicationComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationComponent.class);

    private static final String PAGES = "pages";

    private static final String ISMULTIPAGE = "isMultiPagePublication";

    @Override
    protected void populateRequest(HippoBean publication, HippoBean document, HstRequest request, HstResponse response) {
        setDocuments(publication, document, request);
        setPages(publication, document, request);
        request.setAttribute("document", publication);
    }

    protected HippoBean getPublication(HippoBean document) {

        if (document.isHippoFolderBean()) {
            if (isDocumentsFolder(document)) {
                document = document.getParentBean();
            }
            return getPublicationFromFolder(document);
        }

        if (isPage(document)) {
            return getPublicationFromFolder(document.getParentBean().getParentBean());
        }

        if (document instanceof DocumentInformation) {
            HippoBean documentsFolder = getDocumentsFolder(document);
            return getPublicationFromFolder(documentsFolder.getParentBean());
        }

        return document;
    }

    HippoBean getDocumentsFolder(HippoBean document) {
        HippoBean folder = document.getParentBean();
        return isDocumentsFolder(folder) ? folder : getDocumentsFolder(folder);
    }

    boolean isDocumentsFolder(HippoBean document) {
        return "documents".equals(document.getName());
    }

    HippoBean getPublicationFromFolder(HippoBean folder) {
        List<HippoBean> publications = folder.getChildBeans("govscot:Publication");
        if (publications.isEmpty()) {
            publications = folder.getChildBeans("govscot:ComplexDocument2");
        }
        if (publications.size() > 1) {
            LOG.warn("Multiple publications found in folder {}, will use first", folder.getPath());
        }
        return publications.isEmpty() ? null : publications.get(0);
    }

    private void setDocuments(HippoBean publication, HippoBean document, HstRequest request) {

        HippoBean publicationFolder = publication.getParentBean();
        if (!hasDocuments(publication.getParentBean())) {
            request.setAttribute(DOCUMENTS, Collections.emptyList());
            return;
        }

        List<HippoFolderBean> documentFolders = publicationFolder.getChildBeansByName(DOCUMENTS);
        HippoFolderBean documentFolder = documentFolders.get(0);
        request.setAttribute(DOCUMENTS, documentFolder.getDocuments());

        // look for grouped documents which are stored in their own named sub-folders
        List<HippoFolderBean> nonEmptyFolders = documentFolder.getFolders()
                .stream()
                .filter(this::hasDocuments)
                .collect(toList());
        if (!nonEmptyFolders.isEmpty()) {
            request.setAttribute("groupedDocumentFolders", nonEmptyFolders);
        }

        if (document instanceof DocumentInformation) {
            request.setAttribute("doc", document);
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
        return document instanceof PublicationPage;
    }

    private List<HippoDocumentBean> pagestoInclude(HippoFolderBean pagesFolder) {
        return pagesFolder.getDocuments().stream().filter(this::includePage).collect(toList());
    }

    private boolean includePage(HippoBean page) {
        // do not include pages that have been marked as a contents page by the migration
        return !page.getSingleProperty("govscot:contentsPage", false);
    }


}

