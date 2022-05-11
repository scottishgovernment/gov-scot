package scot.gov.www.rest.sitemap;

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

    Node getMostRecentModifiedNode() throws RepositoryException {
        HippoBeanIterator it = getPublishedNodesForRequest(1);
        return it.nextHippoBean().getNode();
    }

    Urlset generateSitemap(Node node) throws RepositoryException {
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
        // if the type is simpel content and the name is index then this is one of the landing pages
        Node node = bean.getNode();
        return "index".equals(node.getName())
                && "govscot:SimpleContent".equals(node.getPrimaryNodeType().getName());
    }

    boolean isQueryBackedType(HippoBean bean) throws RepositoryException {
        return QUERY_BACKED_TYPES.contains(bean.getNode().getPrimaryNodeType().getName());
    }

    HippoBeanIterator getPublishedNodesForRequest(int limit) throws RepositoryException {
        LOG.info("getPublishedNodesForRequest, {}", limit);
        HstRequestContext context = RequestContextProvider.get();
        HippoBean baseBean = context.getSiteContentBaseBean();
        LOG.info("baseBean is {}", baseBean.getPath());
        HstQueryBuilder builder = HstQueryBuilder.create(baseBean);
        HstQuery query = builder
                .ofTypes(types())
                .where(constraints())
                .limit(limit)
                .offset(0)
                .orderByDescending("hippostdpubwf:lastModificationDate")
                .build();
        //*[(@hippo:paths='b4f9e4d3-3499-4787-92d7-a76615baa2f9') and (@hippo:availability='live') and not(@jcr:primaryType='nt:frozenNode') and ((not(@govscot:contentsPage)) or (@govscot:contentsPage != 'true')) and ((@jcr:primaryType='govscot:PublicationPage' or @jcr:primaryType='govscot:ComplexDocumentSection'))] order by @hippostdpubwf:lastModificationDate descending
        try {
            LOG.info("query: {}", query.toString());
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
