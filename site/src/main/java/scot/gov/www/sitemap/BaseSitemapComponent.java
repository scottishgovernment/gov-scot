package scot.gov.www.sitemap;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;

import static org.apache.commons.lang.StringUtils.endsWith;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;
/**
 * Common code used by sitemap components.
 */
public abstract class BaseSitemapComponent extends BaseHstComponent {

    static int MAX_SITEMAP_SIZE = 100;

    HstQuery allPagesQuery(HstRequest request) {
        return allPagesQuery(request, 0, 1);
    }

    HstQuery allPagesQuery(HstRequest request, int offset, int limit) {
        HstRequestContext context = request.getRequestContext();
        HippoBean baseBean = context.getSiteContentBaseBean();
        HstQueryBuilder builder = HstQueryBuilder.create(baseBean);
        return builder
                .ofTypes(types())
                .where(constraints())
                .limit(limit)
                .offset(offset)
                .orderByDescending("hippostdpubwf:lastModificationDate")
                .build();
    }

    String [] types() {
        return new String[] { "govscot:SimpleContent", "govscot:Collection" };
    }

    Constraint constraints() {
        // exclude those marked with exclude flag
        return propertyFalseIfPresent("govscot:excludeFromSearchIndex");
    }

    Constraint propertyFalseIfPresent(String property) {
        return or(constraint(property).notExists(), constraint(property).notEqualTo(true));
    }

    String createLink(HstRequest request, String path) {
        HstRequestContext context = request.getRequestContext();
        Mount mount = context.getResolvedMount().getMount();
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        String link = linkCreator.create(path, mount).toUrlForm(context, true);
        return correctIndexUrls(link);
    }

    String correctIndexUrls(String url) {
        return endsWith(url, "/index/")
                ? removeEnd(url, "/index/")
                : url;
    }

}
