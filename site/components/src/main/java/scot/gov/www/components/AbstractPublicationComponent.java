package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.PolicyLatest;
import scot.gov.www.beans.Collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for PublicationComponent and ComplexDocumentComponenet which share some behaviour.
 */
public abstract class AbstractPublicationComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPublicationComponent.class);

    protected static final String DOCUMENTS = "documents";

    private static final String POLICIES = "policies";

    private static final String COLLECTIONS = "collections";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();
        if (document == null) {
            send404(request, response);
            return;
        }
        HippoBean publication = getPublication(document);
        if (publication == null) {
            send404(request, response);
            return;
        }
        request.setAttribute("document", publication);
        populateRequest(publication, document, request, response);
        populatePolicyAttribution(request, publication);
        populateCollectionAttribution(request, publication);
    }

    /**
     * Get the publication document
     */
    protected abstract HippoBean getPublication(HippoBean document);

    /**
     * Populate request with values needed for the concrete class - this is different for Publication and its subclasses and ComplexDocument
     */
    protected abstract void populateRequest(HippoBean publication, HippoBean document, HstRequest request, HstResponse response);

    public void populatePolicyAttribution(HstRequest request, HippoBean publication) {
        try {
            // find any Policy documents that link to the content bean in this request
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    (HippoDocumentBean) publication,
                    request.getRequestContext().getSiteContentBaseBean(),
                    "govscot:relatedItems/@hippo:docbase",
                    PolicyLatest.class,
                    false);
            HstQueryResult result = query.execute();
            request.setAttribute(POLICIES, policyNames(result));
        } catch (QueryException e) {
            LOG.warn("Unable to get policies for content item {}", request.getRequestURI(), e);
            request.setAttribute(POLICIES, Collections.emptyList());
        }
    }

    public void populateCollectionAttribution(HstRequest request, HippoBean publication) {
        try {
            // find any Collection documents that link to the content bean in this request
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    (HippoDocumentBean) publication,
                    request.getRequestContext().getSiteContentBaseBean(),
                    "*/*/@hippo:docbase",
                    Collection.class,
                    false);
            HstQueryResult result = query.execute();
            request.setAttribute(COLLECTIONS, collectionsBeans(result));
        } catch (QueryException e) {
            LOG.warn("Unable to get collections for content item {}", request.getRequestURI(), e);
            request.setAttribute(COLLECTIONS, Collections.emptyList());
        }
    }

    private java.util.Collection<String> policyNames(HstQueryResult result) {
        // convert the resulting beans to a list of policy names
        List<String> policyNodes = new ArrayList<>();
        HippoBeanIterator it = result.getHippoBeans();
        while (it.hasNext()) {
            HippoBean policyLatest = it.nextHippoBean();
            policyNodes.add(policyLatest.getParentBean().getName());
        }
        return policyNodes;
    }

    private List<HippoBean> collectionsBeans(HstQueryResult result) {
        // convert the iterator to a list of hippo beans - otherwise size method fails
        List<HippoBean> collectionsBeans = new ArrayList<>();
        HippoBeanIterator it = result.getHippoBeans();
        while (it.hasNext()) {
            HippoBean collection = it.nextHippoBean();
            collectionsBeans.add(collection);
        }
        return collectionsBeans;
    }

    protected void send404(HstRequest request, HstResponse response){
        try {
            LOG.info("404 for {}", request.getRequestURL());
            response.setStatus(404);
            response.forward("/pagenotfound");
        }  catch (IOException e) {
            throw new HstComponentException("Forward failed", e);
        }
    }

    protected boolean hasDocuments(HippoBean publicationParentFolder) {
        return hasChildBeans(publicationParentFolder.getChildBeansByName(DOCUMENTS));
    }

    protected boolean hasChildBeans(List<HippoFolderBean> folders) {
        for (HippoFolderBean docFolder : folders) {
            if (docFolder.getDocumentSize() > 0 || !docFolder.getFolders().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    protected HippoBean prevBean(HippoBean currentPage, List<HippoDocumentBean> pages) {
        int index = pages.indexOf(currentPage);

        // if this is the first page, then there is no previous bean
        if (index == 0) {
            return null;
        }

        return pages.get(index - 1);
    }

    protected HippoBean nextBean(HippoBean currentPage, List<HippoDocumentBean> pages) {
        int index = pages.indexOf(currentPage);

        // if this is the last page, then there is no next bean
        if (index == pages.size() - 1) {
            return null;
        }

        return pages.get(index + 1);
    }

}
