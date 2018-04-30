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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.News;
import scot.gov.www.beans.Policy;
import scot.gov.www.beans.Publication;

import javax.jcr.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

@ParametersInfo(type = EssentialsListComponentInfo.class)
public class FilteredResultsComponent extends EssentialsListComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsComponent.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final String PUBLICATION_DATE = "govscot:publicationDate";

    private static Collection<String> FIELD_NAMES = new ArrayList<>();

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        Collections.addAll(FIELD_NAMES, "govscot:title", "govscot:summary", "govscot:content", "hippostd:tags", 
                "govscot:incumbentTitle", "govscot:policyTags");
    }

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        final EssentialsListComponentInfo paramInfo = getComponentParametersInfo(request);

        HippoBean bean = request.getRequestContext().getContentBean();
        int pageSize = paramInfo.getPageSize();
        String offsetParam = param(request, "page");
        int offset = 0;
        if (offsetParam != null) {
            offset = Integer.parseInt(offsetParam) - 1;
        }
        offset = pageSize * offset;

        try {
            HstQuery hstQuery;
            String path = bean.getNode().getPath();

            if (path.contains("news")) {
                hstQuery = HstQueryBuilder.create(bean)
                        .ofTypes(News.class)
                        .where(constraints(request, PUBLICATION_DATE))
                        .orderByDescending(PUBLICATION_DATE)
                        .limit(pageSize)
                        .offset(offset)
                        .build();
            } else if (path.contains("publications")) {
                hstQuery = HstQueryBuilder.create(bean)
                        .ofTypes(Publication.class)
                        .where(constraints(request, PUBLICATION_DATE))
                        .orderByDescending(PUBLICATION_DATE)
                        .limit(pageSize)
                        .offset(offset)
                        .build();
            } else {
                hstQuery = HstQueryBuilder.create(bean)
                        .ofTypes(Policy.class)
                        .where(constraints(request, null))
                        .orderByAscending("govscot:title")
                        .limit(pageSize)
                        .offset(offset)
                        .build();
            }

            Map<String, Set<String>> params = sanitiseParameterMap(request,
                    request.getRequestContext().getServletRequest().getParameterMap());

            HstQueryResult result = hstQuery.execute();
            request.setAttribute("result", result.getHippoBeans());
            request.setAttribute("parameters", params);
        } catch (RepositoryException e) {
            LOG.error("Failed to access repository", e);
        } catch (QueryException e) {
            LOG.error("Failed to execute query", e);
        }

        super.doBeforeRender(request, response);

    }

    private Map<String, Set<String>> sanitiseParameterMap(HstRequest request, Map<String, String[]> parameterMap) {
        if (parameterMap == null) {
            return null;
        }
        Map<String, Set<String>> sanitisedMap = new HashMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            sanitisedMap.put(entry.getKey(), splitParameters(request, entry.getKey()));
        }
        return sanitisedMap;
    }

    private Constraint constraints(HstRequest request, String searchField) {

        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, request);
        addTopicsConstraint(constraints, request);
        addPublicationTypeConstraint(constraints, request);
        addDateConstraint(constraints, request, searchField);

        Constraint[] constraints1 = new Constraint[constraints.size()];
        for (int i = 0; i < constraints.size(); i++) {
            constraints1[i] = constraints.get(i);
        }

        return and(constraints1);
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
        // TODO: once we know how the publication types are to be structured in the CMS
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
        return new HashSet<>(Arrays.asList(topicTitleArray));
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
