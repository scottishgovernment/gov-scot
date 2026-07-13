package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Indicator")
@Node(jcrType = "govscot:Indicator")
public class Indicator extends SimpleContent {

    @HippoEssentialsGenerated(internalName = "govscot:indicatorChart")
    public List<IndicatorChart> getIndicatorCharts() {
        return getChildBeansByName("govscot:indicatorChart", IndicatorChart.class);
    }
}
