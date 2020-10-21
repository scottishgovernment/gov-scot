package scot.gov.www.sitemap;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.sitemap.components.model.Url;
import org.onehippo.forge.sitemap.components.model.Urlset;

import javax.jcr.RepositoryException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Specialise for just the most recent publication pages. We do this since we do not include individual pages in the
 * other sitemaps.
 */
public class SitemapLatestComponent extends SitemapComponent {

    private static Set<String> MANUAl_URLS = new HashSet<>();

    static {
        // these urls are query backed
        Collections.addAll(MANUAl_URLS, "/", "/search/", "/about/how-government-is-run/directorates/", "/groups/");
    }

    Urlset generateSitemap(HstRequest request) throws QueryException, RepositoryException {
        HippoBeanIterator it = getPublishedNodesForRequest(request);
        Urlset urlset = getUrlSetForResults(it, request);
        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();
        for (String path : MANUAl_URLS) {
            HstLink link = linkCreator.create(path, request.getRequestContext().getResolvedMount().getMount());
            String url = link.toUrlForm(request.getRequestContext(), true);
            Url urlEntry = url(url, Calendar.getInstance());
            urlset.getUrls().add(urlEntry);
        }
        return urlset;
    }

    HippoBeanIterator getPublishedNodesForRequest(HstRequest request) throws QueryException {
        HstRequestContext context = request.getRequestContext();
        HippoBean baseBean = context.getSiteContentBaseBean();
        HstQueryBuilder builder = HstQueryBuilder.create(baseBean);
        HstQuery query = builder
                .ofTypes(types())
                .where(constraints())
                .limit(500)
                .offset(0)
                .orderByDescending("hippostdpubwf:lastModificationDate")
                .build();
        return query.execute().getHippoBeans();
    }

    String [] types() {
        return new String[] { "govscot:PublicationPage", "govscot:ComplexDocumentSection" };
    }

    Constraint constraints() {
        // exclude those marked with exclude flag
        return propertyFalseIfPresent("govscot:contentsPage");
    }
}
