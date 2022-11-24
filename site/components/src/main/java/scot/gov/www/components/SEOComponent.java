package scot.gov.www.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.channels.WebsiteInfo;

/**
 * Provides seo information for the page.
 */
@ParametersInfo(type = SEOComponentInfo.class)
public class SEOComponent extends EssentialsDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SEOComponent.class);

    private static final String TITLE_TAG = "titletag";

    private static final String PAGE_TITLE = "pagetitle";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        // try to find an seo document first, and then fall back to the content document.
        // this is so that the component can be used in channel manager style pages as in ddat,
        // or baked into the page like mygov / trading nations
        HippoBean contentBean = (HippoBean) request.getAttribute(REQUEST_ATTR_DOCUMENT);
        if (contentBean == null) {
            contentBean = request.getRequestContext().getContentBean();
        }

        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo websiteInfo = mount.getChannelInfo();
        setPageTitle(request, contentBean);
//        setPageImage(request, websiteInfo, contentBean);
        request.setAttribute("contentBean", contentBean);
    }

    void setPageTitle(HstRequest request, HippoBean contentBean) {
        String pageTitle = contentBean.getSingleProperty("govscot:title");
        request.setAttribute(TITLE_TAG, String.format("%s - gov.scot", pageTitle));
        request.setAttribute(PAGE_TITLE, pageTitle);
    }

//    void setPageImage(HstRequest request, WebsiteInfo websiteInfo, HippoBean contentBean) {
//        HippoBean imageCard = null;
//
//        if (contentBean != null) {
//            imageCard = contentBean.getLinkedBean("publishing:cardImage", ImageCard.class);
//        }
//
//        if (imageCard == null) {
//            imageCard = getDefaultImageCardForSite(request.getRequestContext(), websiteInfo);
//        }
//
//        request.setAttribute("cardImage", imageCard);
//    }
//
//    public ImageCard getDefaultImageCardForSite(HstRequestContext requestContext, WebsiteInfo websiteInfo) {
//        try {
//            Object e = requestContext.getObjectConverter().getObject(
//                    requestContext.getSession(), websiteInfo.getDefaultCardImage());
//
//            if(e != null && ImageCard.class.isAssignableFrom(e.getClass())) {
//                return (ImageCard) e;
//            }
//
//            LOG.info("unable to find default card image for request {}", websiteInfo.getSiteTitle());
//            return null;
//        } catch (ObjectBeanManagerException | RepositoryException e) {
//            LOG.error("Exception trying to get default image card for site {}", websiteInfo.getSiteTitle(), e);
//            return null;
//        }
//    }
}