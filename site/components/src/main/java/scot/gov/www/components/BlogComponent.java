package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.paging.DefaultPagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Blog;
import scot.gov.www.beans.BlogPost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlogComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BlogComponent.class);

    private static final int PAGE_SIZE = 10;

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HstRequestContext context = request.getRequestContext();
        Blog document = context.getContentBean(Blog.class);
        if (document == null) {
            forwardTo404Page(request, response);
            return;
        }
        request.setAttribute("document", document);

        HippoBean blogFolder = document.getParentBean();
        try {
            HstQuery query = HstQueryBuilder.create(blogFolder)
                    .ofTypes(BlogPost.class)
                    .orderByDescending("govscot:publicationDate")
                    .build();
            HstQueryResult result = query.execute();
            List<BlogPost> posts = toBlogPosts(result.getHippoBeans());
            request.setAttribute("pageable", paginate(posts, currentPage(request)));
        } catch (QueryException e) {
            LOG.warn("Unable to get posts for blog {}", document.getPath(), e);
        }
    }

    private List<BlogPost> toBlogPosts(HippoBeanIterator iterator) {
        List<BlogPost> posts = new ArrayList<>();
        while (iterator.hasNext()) {
            posts.add((BlogPost) iterator.nextHippoBean());
        }
        return posts;
    }

    /**
     * DefaultPagination does not slice its items for you: getItems() returns exactly what you give
     * it, and the (List, pageSize) constructor passes pageSize as the total, not the page size. So
     * the slicing and the total/pageSize/pageNumber bookkeeping both have to be done by hand here.
     */
    private DefaultPagination<BlogPost> paginate(List<BlogPost> posts, int page) {
        int total = posts.size();
        int fromIndex = Math.min((page - 1) * PAGE_SIZE, total);
        int toIndex = Math.min(fromIndex + PAGE_SIZE, total);

        DefaultPagination<BlogPost> pageable = new DefaultPagination<>(total);
        pageable.setItems(posts.subList(fromIndex, toIndex));
        pageable.setPageSize(PAGE_SIZE);
        pageable.setPageNumber(page);
        return pageable;
    }

    private int currentPage(HstRequest request) {
        try {
            return Math.max(1, Integer.parseInt(request.getParameter("page")));
        } catch (NumberFormatException | NullPointerException e) {
            return 1;
        }
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
