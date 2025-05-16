package scot.gov.www.components;

import com.google.common.collect.Lists;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.utils.SiteUtils;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.contentbean.ValueListItem;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scot.gov.www.beans.DynamicIssue;
import scot.gov.www.beans.Issue;
import scot.gov.www.beans.Topic;
import scot.gov.www.components.info.FilteredResultsSideComponentInfo;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

import static scot.gov.www.components.FilteredResultsComponent.PUBLICATION_TYPES;

/**
 * Created by z441571 on 09/04/2018.
 */
@ParametersInfo(type = FilteredResultsSideComponentInfo.class)
public class
FilteredResultsSideComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsSideComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        FilteredResultsSideComponentInfo info = getComponentParametersInfo(request);
        request.setAttribute("term", true);
        request.setAttribute("searchType", info.getSearchType());
        if (info.getIncludeDateFilter()) {
            request.setAttribute("dates", true);
            request.setAttribute("fromDate", info.getFromDate());
        }
        if (info.getLocaleRequired()) {
            request.setAttribute("locale", request.getLocale());
        }
        populatePublicationTypes(request, info.getUseExtendedPublicationType());
        populateTopics(request);

        Map<String, Set<String>> params =
                sanitiseParameterMap(request, request.getRequestContext().getServletRequest().getParameterMap());
        request.setAttribute("parameters", params);
    }

    private void populatePublicationTypes(HstRequest request, boolean useExtendedPublicationType) {
        FilteredResultsSideComponentInfo info = getComponentParametersInfo(request);
        ValueList publicationValueList = SelectionUtil.getValueListByIdentifier(PUBLICATION_TYPES, request.getRequestContext());
        if (info.getIncludePublicationTypesFilter()) {
            List<ValueListItem> items = publicationValueList.getItems();
            if (useExtendedPublicationType) {
                items.add(new ExtendedValueListItem("news", "News"));
                items.add(new ExtendedValueListItem("policy", "Policy"));
                items.sort(Comparator.comparing(ValueListItem::getLabel));
            }
            request.setAttribute("publicationTypes", items);
        } else if (!info.getPublicationTypes().isEmpty()) {
            String[] types = SiteUtils.parseCommaSeparatedValue(info.getPublicationTypes());
            ArrayList<HashMap> pubTypes = new ArrayList<>();

            for (String type : types){
                String[] keyLabel = type.split(":");
                HashMap<String, String> typeMap = new HashMap<>();
                typeMap.put("key", keyLabel[0]);
                typeMap.put("label", keyLabel[1]);
                pubTypes.add(typeMap);
            }
            request.setAttribute("publicationTypes", pubTypes);
        }
    }

    class ExtendedValueListItem extends ValueListItem {
        String key;

        String label;

        ExtendedValueListItem(String key, String label) {
            this.key = key;
            this.label = label;
        }
        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }
    }

    private void populateTopics(HstRequest request) {
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoFolderBean topicsFolder = baseBean.getBean("topics", HippoFolderBean.class);
        HstQuery query = HstQueryBuilder.create(topicsFolder).ofTypes(Issue.class, Topic.class, DynamicIssue.class).orderByAscending("govscot:title").build();
        try {
            HstQueryResult result = query.execute();
            List<HippoBean> topicsList = Lists.newArrayList(result.getHippoBeans());
            request.setAttribute("topics", topicsList);
        } catch (QueryException e) {
            LOG.error("Failed to get topics", e);
        }
    }

    private Map<String, Set<String>> sanitiseParameterMap(HstRequest request, Map<String, String[]> parameterMap) {
        if (parameterMap == null) {
            return Collections.emptyMap();
        }
        Map<String, Set<String>> sanitisedMap = new HashMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            sanitisedMap.put(entry.getKey(), splitParameters(request, entry.getKey()));
        }
        return sanitisedMap;
    }

    private Set<String> splitParameters(HstRequest request, String parameter) {
        String parameters = param(request, parameter);
        if (parameters == null) {
            return Collections.emptySet();
        }
        String [] topicTitleArray = parameters.split("\\;");
        return new HashSet<>(Arrays.asList(topicTitleArray));
    }

    private String param(HstRequest request, String param) {
        HstRequestContext requestContext = request.getRequestContext();
        HttpServletRequest servletRequest = requestContext.getServletRequest();
        return servletRequest.getParameter(param);
    }
}
