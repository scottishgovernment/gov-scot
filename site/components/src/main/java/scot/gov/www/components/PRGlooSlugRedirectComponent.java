package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.HstResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.hst.AliasRedirectService;
import scot.gov.www.beans.News;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.io.IOException;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

/**
 * Redirect prgloo slugs
 *
 * This component will first try to match the slug against the prglooslug property of News articles in the repo.
 * If a match is found a redirect will be issues to that item.
 *
 * If not then the slug will then be matched against the list of all historical prgloo slugs contained in the
 * repo under /content/redirects/prgloo/ if found a redirect to the archive will be issued.
 *
 * Finally, if it not found then a 404 will be sent.
 */
public class PRGlooSlugRedirectComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PRGlooSlugRedirectComponent.class);

    AliasRedirectService aliasRedirectService = new AliasRedirectService();

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        try {
            Optional<Redirect> redirect = aliasRedirectService.lookup(request.getRequestContext().getSession(), request.getPathInfo());
            if (redirect.isPresent()) {
                LOG.info("Redirecting news slug {} -> {}", request.getRequestContext(), redirect.get().getTo());
                HstResponseUtils.sendPermanentRedirect(request, response, redirect.get().getTo());
                return;
            }
        } catch (RepositoryException e) {
            LOG.error("failed to get redirect for " + request.getPathInfo());
            throw new RuntimeException(e);
        }

        // we do not know this slug, send a 404
        try {
            LOG.info("404 for {}", request.getRequestURL());
            response.setStatus(404);
            response.forward("/pagenotfound");
        }  catch (IOException e) {
            throw new HstComponentException("forward failed", e);
        }
    }

}
