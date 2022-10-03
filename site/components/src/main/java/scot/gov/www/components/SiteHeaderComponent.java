package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;

public class SiteHeaderComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        populateAutoCompleteFlag(request);
    }

    static void populateAutoCompleteFlag(HstRequest request) {

        SearchSettings searchSettings = ResilientSearchComponent.searchSettings();
        boolean autoCompleteEnabled = true;
        if (!searchSettings.isEnabled()) {
            autoCompleteEnabled = false;
        }
        if ("bloomreach".equals(searchSettings.getSearchType())) {
            autoCompleteEnabled = false;
        }
        request.setAttribute("autoCompleteEnabled", autoCompleteEnabled);
    }
}

