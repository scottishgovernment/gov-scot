package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;

import static org.apache.commons.lang3.StringUtils.equalsAny;

public class SearchBarComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setSearchVisibility(request);
        populateAutoCompleteFlag(request);
    }

    void setSearchVisibility(HstRequest request) {
        HstComponentConfiguration componentConfig = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration();
        String formatName = componentConfig.getName();
        request.setAttribute("hideSearch", equalsAny(formatName, "home") || formatName.startsWith("search"));
        request.setAttribute("searchcategory", "sitesearch");
    }

    /**
     * determine if auto complete should be used for search bars
     */
    static void populateAutoCompleteFlag(HstRequest request) {
        SearchSettings searchSettings = ResilientSearchComponent.searchSettings();
        request.setAttribute("ds_autocomplete", autoCompleteEnabled(searchSettings));
    }

    static boolean autoCompleteEnabled(SearchSettings searchSettings) {
        boolean autoCompleteEnabled = true;
        if (!searchSettings.isEnabled()) {
            autoCompleteEnabled = false;
        }
        if ("bloomreach".equals(searchSettings.getSearchType())) {
            autoCompleteEnabled = false;
        }
        return autoCompleteEnabled;
    }
}
