package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;
import org.onehippo.cms7.essentials.components.paging.Pageable;
import scot.gov.www.search.BloomreachSearchService;

import static org.apache.commons.lang.StringUtils.isBlank;

@ParametersInfo(type = EssentialsListComponentInfo.class)
public class SearchResultsComponent extends EssentialsListComponent {

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {

        String term = getAnyParameter(request, "q");
        if (isBlank(term)) {
            return null;
        }

        int pageSize = getPageSize(request, paramInfo);
        int page = getCurrentPage(request);
        int offset = (page - 1) * pageSize;
        return new BloomreachSearchService().query(term, offset, request);
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    Pageable<HippoBean> executeQuery(final HstRequest request, final T paramInfo, final HstQuery query) throws QueryException {
        int pageSize = getPageSize(request, paramInfo);
        int page = getCurrentPage(request);
        HstQueryResult execute = query.execute();
        HippoBeanIterator it = execute.getHippoBeans();
        new BloomreachSearchService().postProcessResults(it);
        return getPageableFactory().createPageable(
                execute.getHippoBeans(),
                execute.getTotalSize(),
                pageSize,
                page);
    }



}
