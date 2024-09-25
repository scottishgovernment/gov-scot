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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        LOG.info("populateRequest {}", publication.getPath());
        if (isConsultationWithDates(publication)) {
            populateConsultationFields(publication, request);
        }
    }

    boolean isConsultationWithDates(HippoBean publication) {
        String publicationType = publication.getSingleProperty("govscot:publicationType");
        LOG.info("isConsultationWithDates {}, {}",publication.getPath(), publicationType);
        if (!"consultation-paper".equals(publicationType)) {
            LOG.info("not a consultation: {}", publicationType);
            return false;
        }

        Calendar closingDate = publication.getSingleProperty("govscot:closingDate");
        LOG.info("closingDate {}", closingDate == null ? "no closing date" : closingDate);
        return closingDate != null;
    }

    void populateConsultationFields(HippoBean publication, HstRequest request) {
        LOG.info("populateConsultationFields {}", publication.getPath());
        Calendar closingDate = publication.getSingleProperty("govscot:closingDate");
        Calendar currentDate = Calendar.getInstance();
        long diffInMillis = closingDate.getTimeInMillis() - currentDate.getTimeInMillis();
        LOG.info("populateConsultationFields {}", diffInMillis);
        request.setAttribute("diffInMillis", diffInMillis);
        request.setAttribute("isOpen", diffInMillis > 0);
        request.setAttribute("responseTime", responseTimeString(diffInMillis));
    }

    String responseTimeString(long diffInMillis) {
        if (diffInMillis < 0) {
            return "Closed";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        if (days > 1) {
            return days + " days left to respond";
        } else if (days == 1) {
            return "1 day left to respond";
        }

        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        if (hours > 1) {
            return hours + " hours left to respond";
        } else if (hours == 1) {
            return "1 hour left to respond";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
        if (minutes > 1) {
            return minutes + " minutes left to respond";
        } else if (minutes == 1) {
            return "1 minute left to respond";
        }

        return "Less than a minute left to respond";
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
        List<HippoBean> publications = getAllPublicaitons(folder);
        if (publications.size() > 1) {
            LOG.warn("Multiple publications found in folder {}, will use first", folder.getPath());
        }
        return publications.isEmpty() ? null : publications.get(0);
    }

    List<HippoBean> getAllPublicaitons(HippoBean folder) {
        List<HippoBean> publications = new ArrayList<>();
        publications.addAll(folder.getChildBeans("govscot:Publication"));
        publications.addAll(folder.getChildBeans("govscot:ComplexDocument2"));
        publications.addAll(folder.getChildBeans("govscot:Consultation"));
        return publications;
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

