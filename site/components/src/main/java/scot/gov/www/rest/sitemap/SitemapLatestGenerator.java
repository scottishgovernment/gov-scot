package scot.gov.www.rest.sitemap;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.sitemap.components.model.Url;
import org.onehippo.forge.sitemap.components.model.Urlset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

public class SitemapLatestGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapLatestGenerator.class);

    private static Set<String> MANUAl_URLS = new HashSet<>();

    private static final Set<String> QUERY_BACKED_TYPES = new HashSet<>();

    static {
        // these urls are query backed
        Collections.addAll(QUERY_BACKED_TYPES, "govscot:Home", "govscot:Issue", "govscot:Topic");
        Collections.addAll(MANUAl_URLS,
                "/", "/search/", "/about/how-government-is-run/directorates/", "/groups/", "/collections");
    }

    CacheLoader<Calendar, Urlset> cacheLoader = new CacheLoader<Calendar, Urlset>() {
        @Override
        public Urlset load(Calendar cal) throws RepositoryException {
            return doGenerateSitemap();
        }
    };

    LoadingCache<Calendar, Urlset> cache = CacheBuilder.<Calendar, Urlset>newBuilder().maximumSize(1).build(cacheLoader);

    Urlset generateSitemap(Node node) throws RepositoryException {

        // govscot:latestLastMod is maintained as the most recent publish / unpublish date.  If the cache contains the
        // Urlset for that data then we can reuse it to avoid the expensive query
        Calendar lastModified = node.getProperty("govscot:latestLastMod").getDate();
        try {
            return cache.get(lastModified);
        } catch (ExecutionException e) {
            LOG.error("Failed to generate sitemap");
            throw new RepositoryException(e);
        }
    }

    Urlset doGenerateSitemap() throws RepositoryException {
        HstRequestContext requestContext = RequestContextProvider.get();
        HippoBeanIterator it = getPublishedNodesForRequest(500);
        HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
        Mount mount = requestContext.getResolvedMount().getMount();
        Urlset urlset = new Urlset();
        while (it.hasNext()) {
            HippoBean bean = it.nextHippoBean();
            HstLink link = linkCreator.create(bean, requestContext);
            String url = link.toUrlForm(requestContext, true);
            Url urlEntry = urlEntry(url, getLastModifiedDate(bean));
            urlset.getUrls().add(urlEntry);
        }

        for (String path : MANUAl_URLS) {
            HstLink link = linkCreator.create(path, mount);
            String url = link.toUrlForm(requestContext, true);
            Url urlEntry = urlEntry(url, Calendar.getInstance());
            urlset.getUrls().add(urlEntry);
        }

        return urlset;
    }

    Url urlEntry(String url, Calendar lastModified) {
        Url urlEntry = new Url();
        urlEntry.setLoc(url);
        urlEntry.setLastmod(lastModified);
        return urlEntry;
    }

    Calendar getLastModifiedDate(HippoBean bean) throws RepositoryException {
        return frequentlyChanging(bean)
                ? Calendar.getInstance()
                : bean.getSingleProperty("hippostdpubwf:lastModificationDate");
    }

    boolean frequentlyChanging(HippoBean bean) throws RepositoryException {
        return isQueryBackedType(bean) || isIndexFile(bean);
    }

    boolean isIndexFile(HippoBean bean) throws RepositoryException {
        // if the type is simple content and the name is index then this is one of the landing pages
        Node node = bean.getNode();
        return "index".equals(node.getName())
                && "govscot:SimpleContent".equals(node.getPrimaryNodeType().getName());
    }

    boolean isQueryBackedType(HippoBean bean) throws RepositoryException {
        return QUERY_BACKED_TYPES.contains(bean.getNode().getPrimaryNodeType().getName());
    }

    HippoBeanIterator getPublishedNodesForRequest(int limit) throws RepositoryException {
        HstRequestContext context = RequestContextProvider.get();
        HippoBean baseBean = context.getSiteContentBaseBean();
        HstQueryBuilder builder = HstQueryBuilder.create(baseBean);
        HstQuery query = builder
                .ofTypes(types())
                .where(constraints())
                .limit(limit)
                .offset(0)
                .orderByDescending("hippostdpubwf:lastModificationDate")
                .build();
        try {
            return query.execute().getHippoBeans();
        } catch (QueryException e) {
            throw new RepositoryException(e);
        }
    }

    String [] types() {
        return new String[] { "govscot:PublicationPage", "govscot:ComplexDocumentSection" };
    }

    Constraint constraints() {
        // exclude those marked with exclude flag
        return propertyFalseIfPresent("govscot:contentsPage");
    }

    Constraint propertyFalseIfPresent(String property) {
        return or(constraint(property).notExists(), constraint(property).notEqualTo(true));
    }
}
