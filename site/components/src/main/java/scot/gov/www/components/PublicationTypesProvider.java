package scot.gov.www.components;

import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import scot.gov.publishing.hippo.funnelback.component.MapProvider;

import java.util.Collections;
import java.util.Map;

public class PublicationTypesProvider implements MapProvider {

    @Override
    public Map<String, String> get(HstRequestContext context) {
        ValueList valueList = SelectionUtil.getValueListByIdentifier("publicationTypes", context);
        if (valueList == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = SelectionUtil.valueListAsMap(valueList);
        map.put("news", "News");
        map.put("policy", "Policy");
        return map;
    }

}
