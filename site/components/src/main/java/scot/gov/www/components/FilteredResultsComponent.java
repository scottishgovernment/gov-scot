package scot.gov.www.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.hippoecm.repository.util.DateTools;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;
import org.onehippo.cms7.essentials.components.paging.Pageable;
import org.onehippo.cms7.essentials.components.utils.SiteUtils;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.AttributableContent;
import scot.gov.www.components.info.FilteredResultsComponentInfo;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

@ParametersInfo(type = FilteredResultsComponentInfo.class)
public class FilteredResultsComponent extends EssentialsListComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsComponent.class);

    public static final String GOVSCOT_TITLE = "govscot:title";

    public static final String PUBLICATION_TYPES = "publicationTypes";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Collection<String> fieldNames = new ArrayList<>();

    private FilteredResultsComponentInfo paramInfo;

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        Collections.addAll(fieldNames, GOVSCOT_TITLE, "govscot:summary", "govscot:content/hippostd:content",
                "hippostd:tags", "govscot:incumbentTitle", "govscot:policyTags", "govscot:newsTags");
    }

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

       paramInfo = getComponentParametersInfo(request);

        super.doBeforeRender(request, response);

        Map<String, Set<String>> params = sanitiseParameterMap(request,
                request.getRequestContext().getServletRequest().getParameterMap());

        ValueList publicationValueList =
                SelectionUtil.getValueListByIdentifier(PUBLICATION_TYPES, request.getRequestContext());

        if(params != null && params.containsKey(PUBLICATION_TYPES)){
            Set<String> pubTypesParams = params.get(PUBLICATION_TYPES);
            Set<String> validPubTypesParams = removeInvalidPublicationTypeParams(pubTypesParams, publicationValueList);

            if(!validPubTypesParams.isEmpty()){
                params.put(PUBLICATION_TYPES, validPubTypesParams);
            } else {
                params.remove(PUBLICATION_TYPES);
            }
        }

        String relativeContentPath = request.getRequestContext().getResolvedSiteMapItem().getRelativeContentPath();
        request.setAttribute("relativeContentPath", relativeContentPath);

        request.setAttribute("parameters", params);
        request.setAttribute(PUBLICATION_TYPES, SelectionUtil.valueListAsMap(publicationValueList));
        request.setAttribute("searchTermPlural", paramInfo.getSearchTermPlural());
        request.setAttribute("searchTermSingular", paramInfo.getSearchTermSingular());

    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {
        final String documentTypes = paramInfo.getDocumentTypes();
        final String[] types = SiteUtils.parseCommaSeparatedValue(documentTypes);

        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);
        final int offset = (page - 1) * pageSize;

        HippoBean scopeFolder = scope.isHippoFolderBean() ? scope : scope.getParentBean();

        HstQueryBuilder builder = HstQueryBuilder.create(scopeFolder);

        // order by needs to be multi valued
        String [] sortFields = paramInfo.getSortField().split(",");
        String [] sortOrders = paramInfo.getSortOrder().split(",");

        // use the first field (date field) to determine the constraint
        HstQueryBuilder queryBuilder = builder.ofTypes(types)
                .where(constraints(request, sortFields[0]))
                .limit(pageSize)
                .offset(offset);

        for (int i = 0; i < sortFields.length; i++) {
           queryBuilder.orderBy(HstQueryBuilder.Order.fromString(sortOrders[i]), sortFields[i]);
        }
        return queryBuilder.build();
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    Pageable<HippoBean> executeQuery(final HstRequest request, final T paramInfo, final HstQuery query) throws QueryException {
        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);

        final HstQueryResult execute = query.execute();

        // populate Collections for Publication type items
        HippoBeanIterator it = execute.getHippoBeans();
        while (it.hasNext()) {
            HippoBean item = it.nextHippoBean();
            if (item instanceof AttributableContent){
                populateCollectionAttribution(request, (AttributableContent) item);
            }
        }

        return getPageableFactory().createPageable(
                execute.getHippoBeans(),
                execute.getTotalSize(),
                pageSize,
                page);
    }

    public void populateCollectionAttribution(HstRequest request, AttributableContent item) {
        try {
            // find any Collection documents that link to the content bean in this request
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    item,
                    request.getRequestContext().getSiteContentBaseBean(),
                    "*/*/@hippo:docbase",
                    scot.gov.www.beans.Collection.class,
                    false);
            HstQueryResult result = query.execute();
            item.setCollections(collectionsBeans(result));
        } catch (QueryException e) {
            LOG.warn("Unable to get collections for content item {}", request.getRequestURI(), e);
        }
    }

    private List<HippoBean> collectionsBeans(HstQueryResult result) {
        // convert the iterator to a list of hippo beans - otherwise size method fails
        List<HippoBean> collectionsBeans = new ArrayList<>();
        HippoBeanIterator it = result.getHippoBeans();
        while (it.hasNext()) {
            HippoBean collection = it.nextHippoBean();
            collectionsBeans.add(collection);
        }
        return collectionsBeans;
    }

    private Set<String> removeInvalidPublicationTypeParams(Set<String> pubTypesParams, ValueList publicationValueList){
        Set<String> validPubTypesParams = new HashSet<>();
        Map<String, String> publicationTypesMap = SelectionUtil.valueListAsMap(publicationValueList);

        for (String item : pubTypesParams){
            if(publicationTypesMap.containsKey(item)){
                validPubTypesParams.add(item);
            }
        }

        return validPubTypesParams;
    }


    private Map<String, Set<String>> sanitiseParameterMap(HstRequest request, Map<String, String[]> parameterMap) {
        if (parameterMap == null) {
            return Collections.emptyMap();
        }

        Map<String, Set<String>> sanitisedMap = new HashMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {

            if (equalsIgnoreCase("page", entry.getKey())) {
                continue;
            }

            Set<String> splitParamaters = splitParameters(request, entry.getKey());

            sanitisedMap.put(entry.getKey(), splitParamaters);
        }
        return sanitisedMap;
    }

    private Constraint constraints(HstRequest request, String searchField) {
        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, request);
        addTopicsConstraint(constraints, request);
        addPublicationTypeConstraint(constraints, request);
        addDateConstraint(constraints, request, searchField);
        return and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, HstRequest request) {
        String term = param(request, "term");
        String parsedTerm = SearchInputParsingUtils.parse(term, false);

        if (StringUtils.isBlank(parsedTerm)) {
            return;
        }
        constraints.add(or(fieldConstraints(parsedTerm)));
    }

    private Constraint [] fieldConstraints(String term) {

        List<Constraint> constraints = fieldNames
                .stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    private void addTopicsConstraint(List<Constraint> constraints, HstRequest request) {
        Set<String> topics = splitParameters(request, "topics");
        if (!topics.isEmpty()) {
            Constraint constraint = ConstraintUtils.topicsConstraint(topics);
            constraints.add(constraint);
        }
    }

    void addPublicationTypeConstraint(List<Constraint> constraints, HstRequest request) {
        // check for publication type constraints in both the request query parameters and the component parameters
        Set<String> publicationTypeQueryParams = splitParameters(request, PUBLICATION_TYPES);
        Set<String> publicationTypeComponentParams = SiteUtils.parseCommaSeparatedValueAsSet(paramInfo.getPublicationTypes());
        Collection<String> publicationTypes = !publicationTypeQueryParams.isEmpty()
                ? publicationTypeQueryParams
                :  publicationTypeComponentParams;
        if (!publicationTypes.isEmpty()) {
            constraints.add(ConstraintUtils.publicationTypeConstraint(publicationTypes));
        }
    }

    private String param(HstRequest request, String param) {
        HstRequestContext requestContext = request.getRequestContext();
        HttpServletRequest servletRequest = requestContext.getServletRequest();
        return servletRequest.getParameter(param);
    }

    private Set<String> splitParameters(HstRequest request, String parameter) {
        String parameters = param(request, parameter);
        if (parameters == null) {
            return Collections.emptySet();
        }
        String [] topicTitleArray = parameters.split("\\;");
        return new HashSet<>(asList(topicTitleArray));
    }

    private void addDateConstraint(List<Constraint> constraints, HstRequest request, String searchField) {
        if (searchField == null) {
            return;
        }
        String begin = param(request, "begin");
        String end = param(request, "end");
        if (isNoneBlank(begin, end)) {
            Calendar beginCal = getCalendar(begin);
            Calendar endCal = getCalendar(end);
            constraints.add(and(constraint(searchField).between(beginCal, endCal, DateTools.Resolution.DAY)));
            return;
        }

        addBeginFilter(constraints, searchField, begin);
        addEndFilter(constraints, searchField, end);

    }

    private void addBeginFilter(List<Constraint> constraints, String searchField, String begin) {
        if (isAnyBlank(searchField, begin)) {
            return;
        }

        Calendar calendar = getCalendar(begin);
        constraints.add(and(constraint(searchField).greaterOrEqualThan(calendar, DateTools.Resolution.DAY)));
    }

    private void addEndFilter(List<Constraint> constraints, String searchField, String end) {
        if (isAnyBlank(searchField, end)) {
            return;
        }

        Calendar calendar = getCalendar(end);
        constraints.add(and(constraint(searchField).lessOrEqualThan(calendar, DateTools.Resolution.DAY)));
    }

    private Calendar getCalendar(String param) {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(dateFormat.parse(param));
            return cal;
        } catch (ParseException e) {
            LOG.warn("Invalid date {}", param, e);
            return null;
        }
    }
}