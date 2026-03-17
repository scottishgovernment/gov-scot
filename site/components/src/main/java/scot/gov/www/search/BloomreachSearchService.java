package scot.gov.www.search;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.component.*;

import scot.gov.publishing.hippo.search.PaginationBuilder;
import scot.gov.publishing.hippo.search.SearchService;
import scot.gov.publishing.hippo.search.SearchSettings;
import scot.gov.publishing.hippo.search.model.*;
import scot.gov.www.beans.*;
import scot.gov.www.components.ConstraintUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Collection;

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

    public static final String [] DATE_FIELDS = { "govscot:displayDate", "govscot:publicationDate", "hippostdpubwf:lastModificationDate" };

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");

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

    public SearchResponse response(HstQueryResult result, Search search) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(search.getQuery());
        searchResponse.setQuestion(question);

        int offset = (search.getPage() - 1) * PAGE_SIZE;
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        searchResponse.setResultsSummary(resultsSummary);

        Pagination pagination = new PaginationBuilder().getPagination(resultsSummary, search);
        searchResponse.setPagination(pagination);
        searchResponse.setResults(results(result.getHippoBeans(), search.getRequest().getRequestContext()));
        searchResponse.setHasResults(!searchResponse.getResults().isEmpty());
        return searchResponse;
    }

     Question getQuestion(String query) {
        Question question = new Question();
        question.setOriginalQuery(query);
        question.setQuery(query);
        return question;
    }

    public ResultsSummary buildResultsSummary(HstQueryResult result, int offset) {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setCurrStart(offset + 1);
        resultsSummary.setCurrEnd(Math.min(resultsSummary.getCurrStart() + PAGE_SIZE - 1, result.getTotalSize()));
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        return resultsSummary;
    }

    List<Result> results(HippoBeanIterator it, HstRequestContext requestContext) {
        List<Result> results = new ArrayList<>();
        while (it.hasNext()) {
            HippoBean bean = it.nextHippoBean();
            results.add(result(bean, requestContext));
        }
        return results;
    }

    Result result(HippoBean bean, HstRequestContext requestContext) {
        Result res = new Result();
        res.setLink(link(bean, requestContext));
        res.setSummary(bean.getSingleProperty("govscot:summary"));
        res.setLabel(label(bean));
        HippoBean partOf = partOfLink(bean);
        if (partOf != null) {
            res.getPartOf().add(link(partOf, requestContext));
        }
        Calendar date = bean.getSingleProperty("govscot:publicationDate");
        if (date == null || bean instanceof Publication) {
            date = bean.getSingleProperty("govscot:displayDate");
        }
        if (date != null) {
            DateTimeFormatter dateTimeFormatter = "News".equals(res.getLabel()) ? DATE_TIME_FORMATTER : DATE_FORMATTER;
            res.setDisplayDate(dateTimeFormatter.format(date.toInstant().atZone(java.time.ZoneId.systemDefault())));
        }
        setLabel(res, bean, requestContext);
        return res;
    }

    void setLabel(Result res, HippoBean bean, HstRequestContext requestContext) {
        if (bean instanceof Role role) {
            People peopleImage = role.getImage();
            res.setSubtitle(role.getIncumbent().getTitle());
            if (peopleImage != null) {
                res.setImage(image(peopleImage, requestContext));
                res.setLabel("Role");
            }
            res.setLabel("Role");
        }
        if (bean instanceof FeaturedRole role) {
            ColumnImage peopleImage = role.getImage();
            res.setSubtitle(role.getIncumbent().getTitle());
            if (peopleImage != null) {
                res.setImage(image(peopleImage, requestContext));
                res.setLabel("Role");
            }
            res.setLabel("Role");
        }
        if (bean instanceof Person person) {
            People peopleImage = person.getImage();
            if (peopleImage != null) {
                res.setImage(image(peopleImage, requestContext));
                res.setLabel("Role");
            }
        }
    }

    Image image(HippoBean imageBean, HstRequestContext requestContext) {
        HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
        String link = linkCreator.create(imageBean, requestContext).toUrlForm(requestContext, true);
        link = link + imageBean.getName();
        return Image.createImage(link, "govscot");
    }

    Link link(HippoBean bean, HstRequestContext requestContext) {
        HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
        HstLink hstlink = linkCreator.create(bean, requestContext);
        String url = hstlink.toUrlForm(requestContext, false);
        Link link = new Link();
        link.setUrl(url);
        link.setLabel(bean.getSingleProperty(TITLE));
        return link;
    }

    HippoBean partOfLink(HippoBean bean) {
        Method method = MethodUtils.getMatchingAccessibleMethod(bean.getClass(), "getPartOfBean", new Class[0]);
        if (method == null) {
            return null;
        }
        try {
            return (HippoBean) MethodUtils.invokeMethod(bean, "getPartOfBean", new String [] {});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOG.error("unable to invoke getPartOfBean", e);
            return null;
        }
    }

    String label(HippoBean bean) {
        Method method = MethodUtils.getMatchingAccessibleMethod(bean.getClass(), "getLabel", new Class[0]);
        if (method == null) {
            return null;
        }
        try {
            String label = (String) MethodUtils.invokeMethod(bean, "getLabel", new String [] {});
            return StringUtils.capitalize(label);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOG.error("unable to invoke getLabel", e);
            return null;
        }
    }
}
