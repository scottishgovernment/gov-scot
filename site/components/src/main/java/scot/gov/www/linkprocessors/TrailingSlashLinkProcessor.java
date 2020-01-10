package scot.gov.www.linkprocessors;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.sitemap.HstSiteMapItem;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;

public class TrailingSlashLinkProcessor extends HstLinkProcessorTemplate {

    protected HstLink doPostProcess(final HstLink link) {
        // if the path has an extension then leave it alone
        String extension = StringUtils.substringAfterLast(link.getPath(), ".");
        if (StringUtils.isNotEmpty(extension)) {
            return link;
        }

        // if the path ends in a slasg then leave it alone
        if (link.getPath().endsWith("/")) {
            return link;

        }

        // wrap the link in order to add a trailing slash
        return new GovScotLink(link);
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {
        return link;
    }

    /**
     * Wrap HstLink to provide trailing slash behaviour.
     */
    private class GovScotLink implements HstLink {

        private HstLink link;

        public GovScotLink(HstLink link){
            this.link = link;
        }

        @Override
        public String getPath() {
            String path = link.getPath();
            return path.endsWith("/") ? path: path + "/";
        }

        @Override
        public void setPath(String path) {
            link.setPath(path);
        }

        @Override
        public String getSubPath() {
            return link.getSubPath();
        }

        @Override
        public void setSubPath(String subpath) {
            link.setSubPath(subpath);
        }

        @Override
        public boolean isContainerResource() {
            return link.isContainerResource();
        }

        @Override
        public void setContainerResource(boolean b) {
            link.setContainerResource(b);
        }

        @Override
        public String toUrlForm(HstRequestContext requestContext, boolean fullyQualified) {
            String linkStr =  link.toUrlForm(requestContext, fullyQualified);
            return linkStr == null ? null : linkStr.endsWith("/") ? linkStr : addUrlToPath(linkStr);
        }

        String addUrlToPath(String linkStr) {
            int questionMarkIndex = linkStr.indexOf("?");
            if (questionMarkIndex == -1) {
                return linkStr + "/";
            }

            String pathPart = linkStr.substring(0, questionMarkIndex - 1);
            String params = linkStr.substring(questionMarkIndex, linkStr.length());
            return pathPart + '/' + params;
        }

        @Override
        public String[] getPathElements() {
            return link.getPathElements();
        }

        @Override
        public Mount getMount() {
            return link.getMount();
        }

        @Override
        public HstSiteMapItem getHstSiteMapItem() {
            return null;
        }

        @Override
        public boolean isNotFound() {
            return link.isNotFound();
        }

        @Override
        public void setNotFound(boolean b) {
            link.setNotFound(b);
        }
    }

}
