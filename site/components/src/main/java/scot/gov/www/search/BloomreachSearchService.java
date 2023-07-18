package scot.gov.www.search;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.component.*;
import scot.gov.publishing.hippo.funnelback.component.postprocess.PaginationBuilder;
import scot.gov.publishing.hippo.funnelback.model.Pagination;
import scot.gov.publishing.hippo.funnelback.model.Question;
import scot.gov.publishing.hippo.funnelback.model.Response;
import scot.gov.publishing.hippo.funnelback.model.ResultsSummary;
import scot.gov.www.beans.AttributableContent;
import scot.gov.www.beans.ComplexDocument2;
import scot.gov.www.beans.ComplexDocumentSection;
import scot.gov.www.beans.PublicationPage;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

@Service
@Component("scot.gov.publishing.hippo.funnelback.component.BloomreachSearchService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BloomreachSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(scot.gov.www.search.BloomreachSearchService.class);

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

    static {
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
                "govscot:blog");
    }

    @Override
    public SearchResponse performSearch(Search search, SearchSettings searchsettings) {

        int offset = (search.getPage() - 1) * PAGE_SIZE;
        try {
            HstQuery hstQuery = query(search);
            HstQueryResult result = hstQuery.execute();
            postProcessResults(result.getHippoBeans(), search.getRequest());
            return response(result, search, offset, search.getRequestUrl());
        } catch (QueryException e) {
            LOG.error("Query exceptions in fallback", e);
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String s, SearchSettings searchSettings) {
        return emptyList();
    }

    public void postProcessResults(HippoBeanIterator it, HstRequest request) {
        while (it.hasNext()) {
            HippoBean item = it.nextHippoBean();
            postProcessResult(item, request);
        }
    }

    void postProcessResult(HippoBean item, HstRequest request) {

        // populate Collections for Publication type items
        if (item instanceof AttributableContent) {
            populateCollectionAttribution((AttributableContent) item, request);
        }

        // populate parent publication for Complex Document sections
        if (item instanceof ComplexDocumentSection) {
            populateComplexDocParent((ComplexDocumentSection) item);
        }

        // populate parent publication for Publication pages
        if (item instanceof PublicationPage) {
            populatePublicationParent((PublicationPage) item);
        }
    }

    public void populateCollectionAttribution(AttributableContent item, HstRequest request) {

        try {
            // find any Collection documents that link to the content bean in this request
            HstRequestContext context = request.getRequestContext();
            HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
            HstQuery query = createIncomingBeansQuery(
                    item,
                    baseBean,
                    "*/*/@hippo:docbase",
                    scot.gov.www.beans.Collection.class,
                    context.getQueryManager());
            HstQueryResult result = query.execute();
            item.setCollections(collectionsBeans(result));
        } catch (QueryException e) {
            LOG.warn("Unable to get collections for content item {}", item.getTitle(), e);
        }
    }

    private HstQuery createIncomingBeansQuery(HippoDocumentBean bean,
                                                    HippoBean scope,
                                                    String linkPath,
                                                    Class<? extends HippoBean> beanMappingClass,
                                                    HstQueryManager queryManager) throws QueryException {

        String canonicalHandleUUID = bean.getCanonicalHandleUUID();
        HstQuery query = queryManager.createQuery(scope, beanMappingClass, false);
        Filter filter = query.createFilter();
        filter.addEqualTo(linkPath, canonicalHandleUUID);
        query.setFilter(filter);
        return query;
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

    public void populatePublicationParent(PublicationPage item) {
        HippoBean pagesFolder = item.getParentBean();
        HippoBean parentFolder = pagesFolder.getParentBean();
        HippoBean parent = (HippoBean) parentFolder.getChildBeansByName("index").get(0);
        item.setParent(parent);
    }

    public HstQuery query(Search search) {
        String query = defaultIfBlank(search.getQuery(), "");
        String parsedQueryStr = SearchInputParsingUtils.parse(query, false);
        int offset = (search.getPage() - 1) * 10;
        HstRequestContext context = search.getRequest().getRequestContext();
        HstQueryBuilder queryBuilder = HstQueryBuilder.create(context.getSiteContentBaseBean());
        return queryBuilder
                .where(constraints(parsedQueryStr))
                .limit(PAGE_SIZE)
                .offset(offset)
                .build(context.getQueryManager());
    }

    private Constraint constraints(String searchField) {

        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, searchField);
        constraints.add(
                or(
                        constraint("govscot:excludeFromSearchIndex").equalTo(false),
                        constraint("govscot:excludeFromSearchIndex").notExists()
                )
        );

        return and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, String queryString) {
        constraints.add(or(fieldConstraints(queryString)));
    }

    private Constraint [] fieldConstraints(String term) {
        List<Constraint> constraints = FIELD_NAMES
                .stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    SearchResponse response(HstQueryResult result, Search search, int offset, String url) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(search.getQuery());
        searchResponse.setQuestion(question);

        Response response = new Response();
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        response.getResultPacket().setResultsSummary(resultsSummary);
        searchResponse.setResponse(response);

        Pagination pagination = new PaginationBuilder(search).getPagination(resultsSummary, search.getQuery());
        searchResponse.setPagination(pagination);
        searchResponse.setBloomreachResults(result.getHippoBeans());

        return searchResponse;
    }

    Question getQuestion(String query) {
        Question question = new Question();
        question.setOriginalQuery(query);
        question.setQuery(query);
        return question;
    }

    ResultsSummary buildResultsSummary(HstQueryResult result, int offset) {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setCurrStart(offset + 1);
        resultsSummary.setCurrEnd(Math.min(resultsSummary.getCurrStart() + PAGE_SIZE, result.getTotalSize()));
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
        return resultsSummary;
    }

}
