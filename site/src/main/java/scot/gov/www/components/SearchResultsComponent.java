package scot.gov.www.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.container.RequestContextProvider;
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
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;
import org.onehippo.cms7.essentials.components.paging.Pageable;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;
import static scot.gov.www.components.FilteredResultsComponent.PUBLICATION_TYPES;

@ParametersInfo(type = EssentialsListComponentInfo.class)
public class SearchResultsComponent extends EssentialsListComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResultsComponent.class);

    private static String PRIMARY_TYPE = "jcr:primaryType";

    private static Collection<String> FIELD_NAMES = new ArrayList<>();

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        Collections.addAll(FIELD_NAMES,
                "govscot:title",
                "govscot:summary",
                "govscot:content/hippostd:content",
                "hippostd:tags",
                "govscot:incumbentInformation",
                "govscot:policyTags",
                "govscot:newsTags",
                "govscot:roleTitle",
                "govscot:request/hippostd:content",
                "govscot:response/hippostd:content",
                "govscot:twitter",
                "govscot:email",
                "govscot:website",
                "govscot:flickr",
                "govscot:facebook",
                "govscot:blog"
        );
    }

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        super.doBeforeRender(request, response);

        Map<String, Set<String>> params = sanitiseParameterMap(request,
                request.getRequestContext().getServletRequest().getParameterMap());

        String relativeContentPath = request.getRequestContext().getResolvedSiteMapItem().getRelativeContentPath();
        request.setAttribute("relativeContentPath", relativeContentPath);

        request.setAttribute("parameters", params);

        ValueList publicationValueList =
                SelectionUtil.getValueListByIdentifier(PUBLICATION_TYPES, RequestContextProvider.get());

        request.setAttribute("publicationTypes", SelectionUtil.valueListAsMap(publicationValueList));
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {

        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);
        final int offset = (page - 1) * pageSize;

//      Sorts by jcr:score by default.
        HstQueryBuilder builder = HstQueryBuilder.create(scope);
        return builder.where(constraints(request, null))
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

        HippoBeanIterator it = execute.getHippoBeans();
        while (it.hasNext()) {
            HippoBean item = it.nextHippoBean();

            // populate Collections for Publication type items
            if (item.getClass().getSuperclass() == AttributableContent.class || item.getClass().getSuperclass() == Publication.class){
                populateCollectionAttribution(request, (AttributableContent) item);
            }

            // populate parent publication for Complex Document sections
            if (item.getClass() == ComplexDocumentSection.class){
                populateComplexDocParent((ComplexDocumentSection) item);
            }

            // populate parent publication for Publication pages
            if (item.getClass() == PublicationPage.class){
                populatePublicationParent((PublicationPage) item);
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

    public void populateComplexDocParent(ComplexDocumentSection item) {
        // find this page's parent publication
        HippoBean sectionFolder = item.getParentBean();
        HippoBean chaptersFolder = sectionFolder.getParentBean();
        HippoBean parentFolder = chaptersFolder.getParentBean();
        ComplexDocument2 parent = (ComplexDocument2) parentFolder.getChildBeans("govscot:ComplexDocument2").get(0);
        item.setParent(parent);
    }

    public void populatePublicationParent(PublicationPage item){
        HippoBean pagesFolder = item.getParentBean();
        HippoBean parentFolder = pagesFolder.getParentBean();
        HippoBean parent = (HippoBean) parentFolder.getChildBeansByName("index").get(0);
        item.setParent(parent);
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

            Set<String> splitParameters = splitParameters(request, entry.getKey());
            sanitisedMap.put(entry.getKey(), splitParameters);
        }
        return sanitisedMap;
    }

    private Constraint constraints(HstRequest request, String searchField) {

        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, request);
        addContentTypeConstraints(constraints);
        constraints.add(
                or(
                    constraint("govscot:excludeFromSearchIndex").equalTo(false),
                    constraint("govscot:excludeFromSearchIndex").notExists()
                )
        );

        return and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, HstRequest request) {
        String term = param(request, "q");
        String parsedTerm = SearchInputParsingUtils.parse(term, false);

        if (StringUtils.isBlank(term)) {
            return;
        }
        constraints.add(or(fieldConstraints(parsedTerm)));
    }

    private void addContentTypeConstraints(List<Constraint> constraints) {
        constraints.add(
            and(
                constraint(PRIMARY_TYPE).notEqualTo("govscot:HomeFeaturedItem"),
                constraint(PRIMARY_TYPE).notEqualTo("govscot:DocumentInformation"),
                constraint(PRIMARY_TYPE).notEqualTo("govscot:SiteItem")
            )
        );
    }

    private Constraint [] fieldConstraints(String term) {

        List<Constraint> constraints = FIELD_NAMES
                .stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
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
}
