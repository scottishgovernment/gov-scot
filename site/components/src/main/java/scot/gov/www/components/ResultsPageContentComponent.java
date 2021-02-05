package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.CovidRestrictionsLookup;
import scot.gov.www.beans.SimpleContent;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * Created by z441571 on 09/04/2018.
 */
public class ResultsPageContentComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResultsComponent.class);

    private static final String INDEX = "index";

    // regular expression for postcodes.  Note this has no spaces and is uppercase.  Before matching the input
    // is normalised
    private static final String POSTCODE_REGEXP = "^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$";

    private static final Pattern postcodePattern = Pattern.compile(POSTCODE_REGEXP);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        super.doBeforeRender(request, response);

        Map<String, Set<String>> params = sanitiseParameterMap(request,
            request.getRequestContext().getServletRequest().getParameterMap());

        setIsPostcode(request);
        request.setAttribute("parameters", params);
        request.setAttribute("isSearchpage", true);

        HippoBean bean = request.getRequestContext().getContentBean();
        SimpleContent index = bean.getBean(INDEX, SimpleContent.class);

        if (index != null){
            request.setAttribute(INDEX, index);
        } else {
            request.setAttribute(INDEX, bean);
        }
    }

    private void setIsPostcode(HstRequest request) {
        String term = param(request, "q");

        if (term == null) {
            return;
        }

        String normalisedTerm = term.replaceAll("\\s","").toUpperCase();
        boolean isPostcode = postcodePattern.matcher(normalisedTerm).matches();

        if (isPostcode) {
            // set the covid search page as a n attrib
            HstQueryBuilder builder = HstQueryBuilder.create(request.getRequestContext().getSiteContentBaseBean());
            HstQuery query = builder.ofTypes(CovidRestrictionsLookup.class).build();
            try {
                HstQueryResult result = query.execute();
                if (result.getHippoBeans().hasNext()) {
                    HippoBean covidLookupPage = result.getHippoBeans().next();
                    request.setAttribute("isPostcode", isPostcode);
                    request.setAttribute("covidLookupPage", covidLookupPage);
                    request.setAttribute("normalisedPostcode", normalisedTerm);
                } else {
                    LOG.warn("No CovidRestrictionsLookup page is published");
                }
            } catch (QueryException e) {
                LOG.error("Failed to find CovidRestrictionsLookup page", e);
            }
        }
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
