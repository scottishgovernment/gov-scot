package scot.gov.www.sitemap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.sitemap.components.model.Url;
import org.onehippo.forge.sitemap.components.model.Urlset;
import org.onehippo.forge.sitemap.generator.SitemapGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.endsWith;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.substringAfter;

/**
 * Component that produces a sitemap for a part of the content tree.  Uses code from the bloomreach forge plugin to
 * produce the XML.
 */
public class SitemapComponent extends BaseSitemapComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapComponent.class);

    private static final Set<String> QUERY_BACKED_TYPES = new HashSet<>();

    static {
        Collections.addAll(QUERY_BACKED_TYPES, "govscot:Home", "govscot:Issue", "govscot:Topic");
    }

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Urlset urlset = generateSitemap(request);
            request.setAttribute("sitemap", SitemapGenerator.toString(urlset));
            LOG.debug("sitemap for {}, {} pages, took {} millis",
                    request.getPathInfo(), urlset.getUrls().size(), stopWatch.getTime());
        } catch (QueryException | RepositoryException e) {
            LOG.error("Failed to generate sitemap", e);
            throw new HstComponentException("Unable to generate sitemap for url: " + request.getPathInfo(), e);
        } catch (NumberFormatException e) {
            LOG.error("Failed to generate sitemap - no valid index in url:" + request.getPathInfo(), e);
            send404(request, response);
        }
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

    Urlset generateSitemap(HstRequest request) throws QueryException, RepositoryException {
        HippoBeanIterator it = getPublishedNodesForRequest(request);
        return getUrlSetForResults(it, request);
    }

    HippoBeanIterator getPublishedNodesForRequest(HstRequest request) throws QueryException {
        int offset = getOffsetFromRequestPath(request.getPathInfo());
        HstQuery query = allPagesQuery(request, offset, MAX_SITEMAP_SIZE);
        return query.execute().getHippoBeans();
    }

    private int getOffsetFromRequestPath(String path) {
        String stripped = StringUtils.substringBefore(substringAfter(path, "/sitemap_"), ".xml");
        int index = Integer.valueOf(stripped);
        return index * MAX_SITEMAP_SIZE;
    }

    Urlset getUrlSetForResults(HippoBeanIterator it, HstRequest request) throws RepositoryException {
        Urlset urlset = new Urlset();
        HstRequestContext context = request.getRequestContext();
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        while (it.hasNext()) {
            HippoBean child = it.nextHippoBean();
            String path = linkCreator.create(child, request.getRequestContext()).toUrlForm(context, true);
            if (!StringUtils.endsWith(path, "/pagenotfound")) {
                Url url = url(path, child);
                urlset.getUrls().add(url);
            }
        }
        return urlset;
    }

    Url url(String path, HippoBean bean) throws RepositoryException {
        return url(path, getLastModifiedDate(bean));
    }

    Url url(String path, Calendar lastMod) {
        Url url = new Url();
        url.setLastmod(lastMod);
        url.setLoc(correctIndexUrls(path));
        return url;
    }

    String correctIndexUrls(String url) {
        return endsWith(url, "/index/")
                ? removeEnd(url, "index/")
                : url;
    }

    Calendar getLastModifiedDate(HippoBean bean) throws RepositoryException {
        return frequentlyChanging(bean)
                ? Calendar.getInstance()
                : bean.getProperty("hippostdpubwf:lastModificationDate");
    }

    boolean frequentlyChanging(HippoBean bean) throws RepositoryException {
        return isQueryBackedType(bean) || isIndexFile(bean);
    }

    boolean isQueryBackedType(HippoBean bean) throws RepositoryException {
        return QUERY_BACKED_TYPES.contains(bean.getNode().getPrimaryNodeType());
    }

    boolean isIndexFile(HippoBean bean) throws RepositoryException {
        // if the type is simpel content and the name is index then this is one of the landing pages
        Node node = bean.getNode();
        return "index".equals(node.getName())
                && "govscot:SimpleContent".equals(node.getPrimaryNodeType().getName());
    }
}