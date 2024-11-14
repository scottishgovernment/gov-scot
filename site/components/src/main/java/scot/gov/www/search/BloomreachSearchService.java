package scot.gov.www.search;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
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
import scot.gov.www.components.ConstraintUtils;
import scot.gov.www.components.FilteredResultsComponent;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;
import static scot.gov.www.components.FilteredResultsComponent.*;

@Service
@Component("scot.gov.publishing.hippo.funnelback.component.BloomreachSearchService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BloomreachSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(scot.gov.www.search.BloomreachSearchService.class);

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

    private static final String TITLE = "govscot:title";

    public static final String [] DATE_FIELDS = { "govscot:publicationDate", "govscot:displayDate", "hippostdpubwf:lastModificationDate" };

    static {
        Collections.addAll(FIELD_NAMES,
                TITLE,
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
        try {
            HstQuery hstQuery = query(search);
            HstQueryResult result = hstQuery.execute();
            postProcessResults(result.getHippoBeans(), search.getRequest());
            return response(result, search);
        } catch (QueryException e) {
            LOG.error("Query exceptions in fallback", e);
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String s, String mount, SearchSettings searchSettings) {
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
            FilteredResultsComponent.populateCollectionAttribution(request, (AttributableContent) item);
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
        int offset = (search.getPage() - 1) * 10;
        HstRequestContext context = search.getRequest().getRequestContext();
        HstQueryBuilder queryBuilder = HstQueryBuilder.create(context.getSiteContentBaseBean())
                .where(constraints(search)).limit(PAGE_SIZE).offset(offset);
        addOrderBy(queryBuilder, search.getSort());
        return queryBuilder.build(context.getQueryManager());
    }

    void addOrderBy(HstQueryBuilder queryBuilder, Sort sort) {
        if (sort == null) {
            return;
        }
        switch (sort) {
            case ADATE:
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, DATE_FIELDS);
                break;
            case DATE:
                queryBuilder.orderBy(HstQueryBuilder.Order.DESC, DATE_FIELDS);
                break;
            case TITLE:
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, TITLE);
                break;
            default:
        }
    }

    private Constraint constraints(Search search) {
        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, search.getQuery());
        addTopicsConstraint(constraints, search);
        addDateConstraint(constraints, search, DATE_FIELDS);
        addPublicationTypeConstraint(constraints, search);
        constraints.add(or(
                constraint("govscot:excludeFromSearchIndex").equalTo(false),
                constraint("govscot:excludeFromSearchIndex").notExists()));
        return and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, String queryString) {
        String parsedQueryStr = SearchInputParsingUtils.parse(queryString, false);
        constraints.add(or(fieldConstraints(parsedQueryStr)));
    }

    public static void addTopicsConstraint(List<Constraint> constraints, Search search) {
        if (search.getTopics().isEmpty()) {
            return;
        }
        constraints.add(ConstraintUtils.topicsConstraint(search.getTopics().keySet()));
    }

    public static void addPublicationTypeConstraint(List<Constraint> constraints, Search search) {

        List<Constraint> typeConstraints = new ArrayList<>();

        // convert the publication type 'news' to query for documents with the type 'govscot:News'
        if (search.getPublicationTypes().containsKey("news")) {
            typeConstraints.add(typeConstraint("govscot:News"));
        }

        // convert the publication type 'policy' to query for documents with one of the types 'govscot:Policy' or
        // 'govscot:PolicyInDetail'
        if (search.getPublicationTypes().containsKey("policy")) {
            typeConstraints.add(or(typeConstraint("govscot:Policy"), typeConstraint("govscot:PolicyInDetail")));
        }

        Set<String> publicationTypes = new HashSet<>(search.getPublicationTypes().keySet());
        publicationTypes.remove("news");
        publicationTypes.remove("policy");
        Collections.addAll(typeConstraints, ConstraintUtils.publicationTypeConstraints(search.getPublicationTypes().keySet()));

        if (!typeConstraints.isEmpty()) {
            // we need to 'or' the three potential constraint together
            constraints.add(or(typeConstraints.toArray(new Constraint[typeConstraints.size()])));
        }
    }

    static Constraint typeConstraint(String type) {
        return constraint("@jcr:primaryType").equalTo(type);
    }

    private Constraint [] fieldConstraints(String term) {
        List<Constraint> constraints = FIELD_NAMES
                .stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    public static SearchResponse response(HstQueryResult result, Search search) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(search.getQuery());
        searchResponse.setQuestion(question);

        Response response = new Response();
        int offset = (search.getPage() - 1) * PAGE_SIZE;
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        response.getResultPacket().setResultsSummary(resultsSummary);
        searchResponse.setResponse(response);

        Pagination pagination = new PaginationBuilder().getPagination(resultsSummary, search);
        searchResponse.setPagination(pagination);
        searchResponse.setBloomreachResults(result.getHippoBeans());

        return searchResponse;
    }

    static Question getQuestion(String query) {
        Question question = new Question();
        question.setOriginalQuery(query);
        question.setQuery(query);
        return question;
    }

    public static ResultsSummary buildResultsSummary(HstQueryResult result, int offset) {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setCurrStart(offset + 1);
        resultsSummary.setCurrEnd(Math.min(resultsSummary.getCurrStart() + PAGE_SIZE, result.getTotalSize()));
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
        return resultsSummary;
    }

}
