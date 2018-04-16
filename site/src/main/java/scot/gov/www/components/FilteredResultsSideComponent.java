package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Topic;

import javax.jcr.RepositoryException;

/**
 * Created by z441571 on 09/04/2018.
 */
public class FilteredResultsSideComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsSideComponent.class);

    private static final String TOPICS = "topics";

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean bean = request.getRequestContext().getContentBean();
        try {

            HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();

            HstQuery query = HstQueryBuilder.create(baseBean)
                    .ofTypes(Topic.class).orderByAscending("govscot:title").build();

            final ValueList publicationTypesValueList =
                    SelectionUtil.getValueListByIdentifier("publicationTypes", RequestContextProvider.get());

            String path = bean.getNode().getPath();
            if (path.contains("news")) {
                executeQueryLoggingException(query, request, TOPICS);

            } else if (path.contains("policies")) {
                executeQueryLoggingException(query, request, TOPICS);

            } else if (path.contains("publications")) {
                executeQueryLoggingException(query, request, TOPICS);
                request.setAttribute("publicationTypes", publicationTypesValueList);
            }

        } catch (RepositoryException e) {
            LOG.error("Failed to get path from bean {}", bean, e);
        }

    }

    private void executeQueryLoggingException(HstQuery query, HstRequest request, String name) {
        try {
            HstQueryResult result = query.execute();
            request.setAttribute(name, result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Failed to get {}", name, e);
        }
    }
}
