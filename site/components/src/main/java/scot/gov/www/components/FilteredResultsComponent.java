package scot.gov.www.components;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
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
import scot.gov.publishing.hippo.funnelback.component.*;
import scot.gov.www.beans.AttributableContent;
import scot.gov.www.beans.DynamicIssue;
import scot.gov.www.beans.Issue;
import scot.gov.www.beans.Topic;
import scot.gov.www.components.info.FilteredResultsComponentInfo;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;
import static scot.gov.www.search.BloomreachSearchService.DATE_FIELDS;
import static scot.gov.www.search.BloomreachSearchService.response;

@ParametersInfo(type = FilteredResultsComponentInfo.class)
public class FilteredResultsComponent extends EssentialsListComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsComponent.class);

    public static final String GOVSCOT_TITLE = "govscot:title";

    public static final String PUBLICATION_TYPES = "publicationTypes";

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
        setContentBeanWith404(request, response);
        String relativeContentPath = request.getRequestContext().getResolvedSiteMapItem().getRelativeContentPath();
        Search search = search(request, false);
        request.setAttribute("search", search);
        request.setAttribute("filterButtons", FilterButtonGroups.filterButtonGroups(search));
        request.setAttribute("relativeContentPath", relativeContentPath);
        request.setAttribute("searchTermPlural", paramInfo.getSearchTermPlural());
        request.setAttribute("searchTermSingular", paramInfo.getSearchTermSingular());
        if (paramInfo.getShowSort()) {
            request.setAttribute("showSort", true);
        }
    }

    Search search(HstRequest request, boolean includeComponentParams) {
        String query = param(request, "q");

        int page = getCurrentPage(request);

        // we only want to use paramaters that are supported
        LocalDate begin = date(request, "begin");
        LocalDate end = date(request, "end");
        SearchBuilder searchBuilder = new SearchBuilder()
                .query(query)
                .page(page)
                .fromDate(begin)
                .toDate(end)
                .request(request);
        addPublicationTypes(request, searchBuilder, includeComponentParams);
        addTopics(request, searchBuilder);
        searchBuilder.sort(sort(request));
        return searchBuilder.build();
    }

    Sort sort(HstRequest request) {
        String sortParam = getAnyParameter(request, "sort");
        if (isBlank(sortParam)) {
            /// if there is no sort param then default to the one configured for this page
            paramInfo = getComponentParametersInfo(request);
            sortParam = paramInfo.getDefaultSort();
        }

        try {
            return Sort.valueOf(sortParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid sort value {}, defaulting to date", sortParam, e);
            return Sort.DATE;
        }
    }

    void addPublicationTypes(HstRequest request, SearchBuilder searchBuilder, boolean includeComponentParams) {
        // we support type publication types paramaters:
        //  - publicationsTypes: a ; separated list of publications types
        //  - type: multiple type params can be supplied and each one will be added
        Map<String, String> typesMap = publicationTypes();
        String publicationTypes = param(request, PUBLICATION_TYPES);
        searchBuilder.publicationTypes(publicationTypes, typesMap);

        String [] types = request.getParameterMap().get("type");
        if (types != null) {
            for (String type : types) {
                searchBuilder.publicationTypes(type, typesMap);
            }
        }

        if (!searchBuilder.hasPublicationTypes() && includeComponentParams) {
            // publication types from the param info, this is used by the stats page to make sure it filters by the specified publication types
            searchBuilder.publicationTypes(paramInfo.getPublicationTypes(), ",", typesMap);
        }
    }

    void addTopics(HstRequest request, SearchBuilder searchBuilder) {

        //  - topics: a ; separated list of topics
        //  - topic: multiple topic params can be supplied and each one will be added
        Map<String, String> topicsMap = topics(request);
        searchBuilder.topics(param(request, "topics"), topicsMap);

        String [] topics = request.getParameterMap().get("topic");
        if (topics != null) {
            for (String topic : topics) {
                searchBuilder.topics(topic, topicsMap);
            }
        }
    }

    Map<String, String> topics(HstRequest request) {

        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoFolderBean topicsFolder = baseBean.getBean("topics", HippoFolderBean.class);
        HstQuery query = HstQueryBuilder.create(topicsFolder).ofTypes(Issue.class, Topic.class, DynamicIssue.class).build();
        HashMap<String, String> topics = new HashMap<>();
        try {
            HstQueryResult result = query.execute();
            HippoBeanIterator it = result.getHippoBeans();
            while (it.hasNext()) {
                HippoBean bean = it.nextHippoBean();
                topics.put(bean.getName(), bean.getSingleProperty(GOVSCOT_TITLE));
            }
            return topics;
        } catch (QueryException e) {
            LOG.error("Failed to get topics", e);
            return Collections.emptyMap();
        }
    }

    Map<String, String> publicationTypes() {
        ValueList valueList = SelectionUtil.getValueListByIdentifier(PUBLICATION_TYPES, RequestContextProvider.get());
        return SelectionUtil.valueListAsMap(valueList);
    }

    LocalDate date(HstRequest request, String dateParam) {
        String dateValue = param(request, dateParam);
        if (isBlank(dateValue)) {
            return null;
        }
        return LocalDate.parse(dateValue, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
        String [] dateFields = paramInfo.getSortField().split(",");

        Search search = search(request, true);

        // use the first field (date field) to determine the constraint
        HstQueryBuilder queryBuilder = builder.ofTypes(types)
                .where(constraints(search, dateFields))
                .limit(pageSize)
                .offset(offset);
        addOrderBy(queryBuilder, search.getSort());
        return queryBuilder.build();
    }

    void addOrderBy(HstQueryBuilder queryBuilder, Sort sort) {
        switch (sort) {
            case ADATE:
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, DATE_FIELDS);
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, GOVSCOT_TITLE);
                break;
            case DATE:
                queryBuilder.orderBy(HstQueryBuilder.Order.DESC, DATE_FIELDS);
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, GOVSCOT_TITLE);
                break;
            case TITLE:
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, GOVSCOT_TITLE);
                break;
            default:
        }
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    Pageable<HippoBean> executeQuery(final HstRequest request, final T paramInfo, final HstQuery query) throws QueryException {
        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);
        final HstQueryResult queryResult = query.execute();

        // populate Collections for Publication type items
        HippoBeanIterator it = queryResult.getHippoBeans();
        while (it.hasNext()) {
            HippoBean item = it.nextHippoBean();
            if (item instanceof AttributableContent){
                populateCollectionAttribution(request, (AttributableContent) item);
            }
        }
        populateResponse(request, queryResult);

        return getPageableFactory().createPageable(
                queryResult.getHippoBeans(),
                queryResult.getTotalSize(),
                pageSize,
                page);
    }

    void populateResponse(HstRequest request, HstQueryResult queryResult) {
        Search search = search(request, true);
        SearchResponse searchResponse = response(queryResult, search);
        request.setAttribute("question", searchResponse.getQuestion());
        request.setAttribute("response", searchResponse.getResponse());
        request.setAttribute("pagination", searchResponse.getPagination());
        request.setAttribute("filterButtons", FilterButtonGroups.filterButtonGroups(search));
    }

    public static void populateCollectionAttribution(HstRequest request, AttributableContent item) {
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

    private static List<HippoBean> collectionsBeans(HstQueryResult result) {
        // convert the iterator to a list of hippo beans - otherwise size method fails
        List<HippoBean> collectionsBeans = new ArrayList<>();
        HippoBeanIterator it = result.getHippoBeans();
        while (it.hasNext()) {
            HippoBean collection = it.nextHippoBean();
            collectionsBeans.add(collection);
        }
        return collectionsBeans;
    }

    private Constraint constraints(Search search, String [] dateFields) {
        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, search);
        addTopicsConstraint(constraints, search);
        addPublicationTypeConstraint(constraints, search);
        addDateConstraint(constraints, search, dateFields);
        constraints = constraints.stream().filter(Objects::nonNull).collect(toList());
        return and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, Search search) {
        String parsedTerm = SearchInputParsingUtils.parse(search.getQuery(), false);
        if (isBlank(parsedTerm)) {
            return;
        }
        constraints.add(or(fieldConstraints(parsedTerm)));
    }

    Constraint [] fieldConstraints(String term) {

        List<Constraint> constraints = fieldNames
                .stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    public void addTopicsConstraint(List<Constraint> constraints, Search search) {
        Constraint constraint = ConstraintUtils.topicsConstraint(search.getTopics().keySet());
        if (constraint != null) {
            constraints.add(constraint);
        }
    }

    public void addPublicationTypeConstraint(List<Constraint> constraints, Search search) {
        Constraint constraint = publicationTypeConstraint(search);
        if (constraint != null) {
            constraints.add(constraint);
        }
    }

    public Constraint publicationTypeConstraint(Search search) {
        return ConstraintUtils.publicationTypeConstraint(search.getPublicationTypes().keySet());
    }

    private static String param(HstRequest request, String param) {
        HstRequestContext requestContext = request.getRequestContext();
        HttpServletRequest servletRequest = requestContext.getServletRequest();
        return servletRequest.getParameter(param);
    }

    public static void addDateConstraint(List<Constraint> constraints, Search search, String [] dateFields) {

        if (search.getToDate() != null && search.getFromDate() != null) {
            Calendar beginCal = getCalendar(search.getFromDate());
            Calendar endCal = getCalendar(search.getToDate());
            Constraint [] dateConstaints = Arrays.stream(dateFields)
                    .map(field -> constraint(field).between(beginCal, endCal, DateTools.Resolution.DAY))
                    .collect(toList())
                    .toArray(new Constraint[dateFields.length]);
            constraints.add(and(or(dateConstaints)));
            return;
        }

        addBeginFilter(constraints, search, dateFields);
        addEndFilter(constraints, search, dateFields);
    }

    static void addBeginFilter(List<Constraint> constraints, Search search, String [] dateFields) {
        if (search.getFromDate() == null ) {
            return;
        }

        Calendar calendar = getCalendar(search.getFromDate());
        Constraint [] dateConstraints = Arrays.stream(dateFields)
                .map(field -> constraint(field).greaterOrEqualThan(calendar, DateTools.Resolution.DAY))
                .collect(toList())
                .toArray(new Constraint[dateFields.length]);
        constraints.add(and(or(dateConstraints)));
    }

    static void addEndFilter(List<Constraint> constraints, Search search, String [] dateFields) {
        if (search.getToDate() == null ) {
            return;
        }

        Calendar calendar = getCalendar(search.getToDate());
        Constraint [] dateConstraints = Arrays.stream(dateFields)
                .map(field -> constraint(field).lessOrEqualThan(calendar, DateTools.Resolution.DAY))
                .collect(toList())
                .toArray(new Constraint[dateFields.length]);
        constraints.add(and(or(dateConstraints)));
    }

    static  Calendar getCalendar(LocalDate localDate) {
        return GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
    }
}