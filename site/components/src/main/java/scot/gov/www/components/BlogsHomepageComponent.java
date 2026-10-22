package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.www.beans.BlogHomepage;

public class BlogsHomepageComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HstRequestContext context = request.getRequestContext();

        // the bare /blogs sitemap item resolves its content bean to the blogs folder itself, but the
        // /blogs/_index_ sitemap item (matched for the normal, trailing-slash form of the URL) resolves
        // it to the BlogHomepage index document instead. Handle both so the A-Z list always groups the
        // folder's children rather than the document's.
        HippoBean contentBean = context.getContentBean();
        HippoBean blogsFolder;
        BlogHomepage document;
        if (contentBean instanceof BlogHomepage) {
            document = (BlogHomepage) contentBean;
            blogsFolder = document.getParentBean();
        } else {
            blogsFolder = contentBean;
            document = blogsFolder == null ? null : blogsFolder.getBean("index", BlogHomepage.class);
        }

        request.setAttribute("document", document);
        request.setAttribute("featuredPosts", document == null ? null : document.getFeaturedPosts());
        request.setAttribute("beansByLetter", new AtoZComponent().computeLettersAndBeans(blogsFolder, document));
    }

}
