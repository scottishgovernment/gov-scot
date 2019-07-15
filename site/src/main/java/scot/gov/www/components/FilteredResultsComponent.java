package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.hippoecm.hst.core.request.HstRequestContext;
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
import scot.gov.www.components.info.FilteredResultsComponentInfo;

import javax.jcr.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

@ParametersInfo(type = FilteredResultsComponentInfo.class)
public class FilteredResultsComponent extends EssentialsListComponent {

    private static final String PUBLICATION_DATE = "govscot:publicationDate";
    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsComponent.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String PUBLICATION_TYPES = "publicationTypes";
    private static Collection<String> FIELD_NAMES = new ArrayList<>();

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        Collections.addAll(FIELD_NAMES, "govscot:title", "govscot:summary", "govscot:content/hippostd:content",
                "hippostd:tags", "govscot:incumbentTitle", "govscot:policyTags", "govscot:newsTags");
    }

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        final FilteredResultsComponentInfo paramInfo = getComponentParametersInfo(request);

        super.doBeforeRender(request, response);

        Map<String, Set<String>> params = sanitiseParameterMap(request,
                request.getRequestContext().getServletRequest().getParameterMap());

        ValueList publicationValueList =
                SelectionUtil.getValueListByIdentifier(PUBLICATION_TYPES, request.getRequestContext());

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

        HstQueryBuilder builder = HstQueryBuilder.create(scope);
        return builder.ofTypes(types)
                .where(constraints(request, PUBLICATION_DATE))
                .orderBy(HstQueryBuilder.Order.fromString(paramInfo.getSortOrder()), paramInfo.getSortField())
                .limit(pageSize)
                .offset(offset)
                .build();
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    Pageable<HippoBean> executeQuery(final HstRequest request, final T paramInfo, final HstQuery query) throws QueryException {
        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);

        final HstQueryResult execute = query.execute();
        return getPageableFactory().createPageable(
                execute.getHippoBeans(),
                execute.getTotalSize(),
                pageSize,
                page);
    }

    private Map<String, Set<String>> sanitiseParameterMap(HstRequest request, Map<String, String[]> parameterMap) {
        if (parameterMap == null) {
            return null;
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

        if (StringUtils.isBlank(term)) {
            return;
        }
        constraints.add(or(fieldConstraints(parsedTerm)));
    }

    private Constraint [] fieldConstraints(String term) {

        List<Constraint> constraints = FIELD_NAMES
                .stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    private void addTopicsConstraint(List<Constraint> constraints, HstRequest request) {
        List<String> topicIds = topicIds(request);
        if (topicIds.isEmpty()) {
            return;
        }

        List<Constraint> constraintList = new ArrayList<>();
        for (String topicId : topicIds) {
            constraintList.add(or(constraint("govscot:topics/@hippo:docbase").equalTo(topicId)));
        }

        Constraint orConstraint = ConstraintBuilder.or(constraintList.toArray(new Constraint[constraintList.size()]));
        constraints.add(orConstraint);
    }

    private List<String> topicIds(HstRequest request) {
        List<String> topicIds = new ArrayList<>();
        Set<String> topics = splitParameters(request, "topics");
        try {
            Session session = request.getRequestContext().getSession();
            Node topicsNode = session.getNode("/content/documents/govscot/topics");
            if (topicsNode == null) {
                return Collections.emptyList();
            }

            NodeIterator nodeIt = topicsNode.getNodes();
            while (nodeIt.hasNext()) {
                Node topicNode = nodeIt.nextNode();

                if (isRequired(topicNode, topics)) {
                    topicIds.add(topicNode.getIdentifier());
                }
            }
            return topicIds;
        } catch (RepositoryException e) {
            throw new HstComponentException("Failed to get topics", e);
        }
    }

    private void addPublicationTypeConstraint(List<Constraint> constraints, HstRequest request) {

        Set<String> publicationTypeParams = splitParameters(request, PUBLICATION_TYPES);

        if (publicationTypeParams.isEmpty()) {
            return;
        }

        List<Constraint> constraintList = new ArrayList<>();
        for (String typeId : publicationTypeParams) {
            constraintList.add(or(constraint("govscot:publicationType").equalTo(typeId)));
        }

        Constraint orConstraint = ConstraintBuilder.or(constraintList.toArray(new Constraint[constraintList.size()]));
        constraints.add(orConstraint);

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

    private boolean isRequired(Node topicNode, Set<String> requiredTitles) throws RepositoryException {
        String title = nodeTitle(topicNode);
        return requiredTitles.contains(title);
    }

    private String nodeTitle(Node node) throws RepositoryException {
        Property titleProperty = node.getProperty("hippo:name");
        return titleProperty.getString();
    }

    private void addDateConstraint(List<Constraint> constraints, HstRequest request, String searchField) {
        if (searchField == null) {
            return;
        }
        String begin = param(request, "begin");
        String end = param(request, "end");
        if (begin != null && end != null) {
            Calendar beginCal = getCalendar(begin);
            Calendar endCal = getCalendar(end);
            constraints.add(and(constraint(searchField).between(beginCal, endCal, DateTools.Resolution.DAY)));
            return;
        }

        addBeginFilter(constraints, request, searchField, begin);
        addEndFilter(constraints, request, searchField, end);

    }

    private void addBeginFilter(List<Constraint> constraints, HstRequest request, String searchField, String begin) {
        if (searchField == null) {
            return;
        }

        if (begin == null) {
            return;
        }
        Calendar calendar = getCalendar(begin);
        constraints.add(and(constraint(searchField).greaterOrEqualThan(calendar, DateTools.Resolution.DAY)));

    }

    private void addEndFilter(List<Constraint> constraints, HstRequest request, String searchField, String end) {
        if (searchField == null) {
            return;
        }

        if (end == null) {
            return;
        }

        Calendar calendar = getCalendar(end);
        constraints.add(and(constraint(searchField).lessOrEqualThan(calendar, DateTools.Resolution.DAY)));

    }

    private Calendar getCalendar(String param) {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(DATE_FORMAT.parse(param));
            return cal;
        } catch (ParseException e) {
            LOG.warn("Invalid date {}", param, e);
            return null;
        }
    }
}
