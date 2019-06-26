package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.ComplexDocumentSection;

import java.util.ArrayList;
import java.util.List;

public class ComplexDocumentComponent extends AbstractPublicationComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ComplexDocumentComponent.class);

    private static final String DOCUMENTS = "documents";

    private static final String CHAPTERS = "chapters";

    @Override
    protected void populateRequest(HippoBean publication, HippoBean document, HstRequest request, HstResponse response) {
        setDocuments(publication, request, response);

        // complex specific part
        setChapters(publication, document, request);
        if (request.getPathInfo().endsWith("/about/")) {
            request.setAttribute("isAboutPage", true);
        }
    }

    @Override
    protected HippoBean getPublication(HippoBean document) {
        if (document.isHippoFolderBean()) {
            List<HippoBean> publications = document.getChildBeans("govscot:ComplexDocument2");
            if (publications.size() > 1) {
                LOG.warn("Multiple publications found in folder {}, will use first", document.getPath());
            }
            return publications.isEmpty() ? null : publications.get(0);
        }

        if (isSection(document)) {
            HippoBean publicationFolder = document.getParentBean().getParentBean().getParentBean();
            List<HippoBean> publications = publicationFolder.getChildBeans("govscot:ComplexDocument2");
            if (publications.size() > 1) {
                LOG.warn("Multiple publications found in folder {}, will use first", publicationFolder.getPath());
            }
            return publications.isEmpty() ? null : publications.get(0);
        }

        return document;
    }

    private void setDocuments(HippoBean publication, HstRequest request, HstResponse response) {

        HippoBean publicationFolder = publication.getParentBean();
        Boolean isDocumentsPage = request.getPathInfo().endsWith("/documents/");

        if (!hasDocuments(publication.getParentBean())) {
            if (isDocumentsPage) {
                send404(response);
            }
            return;
        }

        List<HippoFolderBean> documentFolders = publicationFolder.getChildBeansByName(DOCUMENTS);
        HippoFolderBean documentFolder = documentFolders.get(0);
        request.setAttribute(DOCUMENTS, documentFolder.getDocuments());

        // look for grouped documents which are stored in their own named sub-folders
        Boolean hasGroupedFolderContent = false;

        // check if any of those folders have content
        for (HippoFolderBean folder : documentFolder.getFolders()){
            if (!folder.getDocuments().isEmpty()){
                hasGroupedFolderContent = true;
            }
        }

        if (hasGroupedFolderContent) {
            request.setAttribute("groupedDocumentFolders", documentFolder.getFolders());
        }

        // send a 404 for the /documents/ path if there's only one or zero documents and no grouped folders
        if (isDocumentsPage && (hasGroupedFolderContent || documentFolder.getDocuments().size() > 1)){
            request.setAttribute("isDocumentsPage", true);
        } else if (isDocumentsPage) {
            send404(response);
        }
    }

    private void setChapters(HippoBean publication, HippoBean document, HstRequest request) {
        HippoBean publicationFolder = publication.getParentBean();
        boolean hasChapters = hasChapters(publicationFolder);

        if (!hasChapters){
            request.setAttribute("currentPage", publication);
            return;
        }

        List<HippoFolderBean> chaptersFolders = publicationFolder.getChildBeansByName(CHAPTERS);
        HippoFolderBean chaptersFolder = chaptersFolders.get(0);
        List<HippoFolderBean> chapters = chaptersFolder.getFolders();
        HippoBean currentPage = isSection(document) ? document : publication;
        HippoBean currentChapter = isSection(document) ? document.getParentBean() : null;
        request.setAttribute(CHAPTERS, chapters);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("currentChapter", currentChapter);

        // make an array of all the sections plus the home at the start
        ArrayList<HippoDocumentBean> sections = new ArrayList<>();
        sections.add((HippoDocumentBean) publication);

        for(HippoFolderBean folder : chapters){
            sections.addAll(folder.getDocuments());
        }

        request.setAttribute("prev", prevBean(currentPage, sections));
        request.setAttribute("next", nextBean(currentPage, sections));
    }

    private boolean isSection(HippoBean document) {
        return document.getClass() == ComplexDocumentSection.class;
    }

    private boolean hasChapters(HippoBean publicationParentFolder) {
        return hasChildBeans(publicationParentFolder.getChildBeansByName(CHAPTERS));
    }
}

