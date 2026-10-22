package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.BlogPost;

import java.io.IOException;

public class BlogPostComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BlogPostComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HstRequestContext context = request.getRequestContext();
        BlogPost document = context.getContentBean(BlogPost.class);
        if (document == null) {
            forwardTo404Page(request, response);
            return;
        }
        request.setAttribute("document", document);
        request.setAttribute("blog", document.getBlog());
        request.setAttribute("parent", document.getBlog());
    }

    void forwardTo404Page(HstRequest request, HstResponse response) {
        try {
            LOG.info("404 for {}", request.getRequestURL());
            response.setStatus(404);
            response.forward("/pagenotfound");
        } catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }
    }

}
