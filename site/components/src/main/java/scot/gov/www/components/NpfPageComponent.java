package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Outcome;

import java.util.ArrayList;
import java.util.List;

public class NpfPageComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(NpfPageComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        HippoBean document = context.getContentBean();
        if (document == null) {
            pageNotFound(response);
            return;
        }
        request.setAttribute("document", document);

        HippoBean npfFolder = document.getParentBean();
        try {
            HstQuery query = HstQueryBuilder.create(npfFolder)
                    .ofTypes(Outcome.class)
                    .build();
            HstQueryResult result = query.execute();
            List<Outcome> outcomes = new ArrayList<>();
            result.getHippoBeans().forEachRemaining(b -> outcomes.add((Outcome) b));
            request.setAttribute("outcomes", outcomes);
        } catch (QueryException e) {
            throw new HstComponentException("Failed to query NPF outcomes", e);
        }
    }
}
