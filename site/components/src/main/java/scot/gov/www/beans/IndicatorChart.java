package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "govscot:IndicatorChart")
@Node(jcrType = "govscot:IndicatorChart")
public class IndicatorChart extends HippoCompound {

    @HippoEssentialsGenerated(internalName = "govscot:title")
    public String getTitle() {
        return getSingleProperty("govscot:title");
    }

    @HippoEssentialsGenerated(internalName = "govscot:content")
    public HippoHtml getContent() {
        return getHippoHtml("govscot:content");
    }

    @HippoEssentialsGenerated(internalName = "govscot:chartData")
    public ChartData getChartData() {
        return getLinkedBean("govscot:chartData", ChartData.class);
    }

    @HippoEssentialsGenerated(internalName = "govscot:chartConfig")
    public ChartConfig getChartConfig() {
        return getLinkedBean("govscot:chartConfig", ChartConfig.class);
    }
}
