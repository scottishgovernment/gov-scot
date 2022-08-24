package scot.gov.www.search;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.component.Search;
import scot.gov.publishing.hippo.funnelback.component.SearchResponse;
import scot.gov.publishing.hippo.funnelback.component.SearchService;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;
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

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

@Service
@Component("scot.gov.publishing.hippo.funnelback.component.BloomreachSearchService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BloomreachSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(scot.gov.www.search.BloomreachSearchService.class);

    private static final String PRIMARY_TYPE = "jcr:primaryType";

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

        String query = defaultIfBlank(search.getQuery(), "");
        int offset = (search.getPage() - 1) * PAGE_SIZE;

        HstQuery hstQuery = query(query, offset, search.getRequest());
        try {
            HstQueryResult result = hstQuery.execute();
            postProcessResults(result.getHippoBeans());
            return response(result, query, offset, search.getRequestUrl());
        } catch (QueryException e) {
            LOG.error("Query exceptions in fallback", e);
            return null;
        }
    }

    public void postProcessResults(HippoBeanIterator it) {
        while (it.hasNext()) {
            HippoBean item = it.nextHippoBean();
            postProcessResult(item);
        }
    }

    void postProcessResult(HippoBean item) {
        // populate Collections for Publication type items
        if (item instanceof AttributableContent) {
            populateCollectionAttribution((AttributableContent) item);
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

    public void populateCollectionAttribution(AttributableContent item) {
        try {
            // find any Collection documents that link to the content bean in this request
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    item,
                    RequestContextProvider.get().getSiteContentBaseBean(),
                    "*/*/@hippo:docbase",
                    scot.gov.www.beans.Collection.class,
                    false);
            HstQueryResult result = query.execute();
            item.setCollections(collectionsBeans(result));
        } catch (QueryException e) {
            LOG.warn("Unable to get collections for content item {}", item.getTitle(), e);
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

    public void populatePublicationParent(PublicationPage item) {
        HippoBean pagesFolder = item.getParentBean();
        HippoBean parentFolder = pagesFolder.getParentBean();
        HippoBean parent = (HippoBean) parentFolder.getChildBeansByName("index").get(0);
        item.setParent(parent);
    }

    public HstQuery query(String queryStr, int offset, HstRequest request) {
        String parsedQueryStr = SearchInputParsingUtils.parse(queryStr, false);
        HstRequestContext context = request.getRequestContext();
        return HstQueryBuilder.create(context.getSiteContentBaseBean())
                .where(constraints(parsedQueryStr))
                .limit(PAGE_SIZE)
                .offset(offset)
                .build();
    }

    private Constraint constraints(String searchField) {

        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, searchField);
        addContentTypeConstraints(constraints);
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

    SearchResponse response(HstQueryResult result, String query, int offset, String url) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(query);
        searchResponse.setQuestion(question);

        Response response = new Response();
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        response.getResultPacket().setResultsSummary(resultsSummary);
        searchResponse.setResponse(response);

        Pagination pagination = new PaginationBuilder(url).getPagination(resultsSummary, query);
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
        resultsSummary.setCurrEnd(resultsSummary.getCurrStart() + PAGE_SIZE);
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
        return resultsSummary;
    }
}
