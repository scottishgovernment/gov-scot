package scot.gov.www.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.parameters.ValueListProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.www.channel.ChannelColourValueListProvider;
import scot.gov.www.channel.WebsiteInfo;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ComponentBackgroundColourValueListProvider implements ValueListProvider {

    private List getValueListForLocale(Locale locale) {
        Map<String, String> colors = getColors();
        return Collections.unmodifiableList(new LinkedList<>(colors.keySet()));
    }

    @Override
    public List<String> getValues() {
        List list = getValueListForLocale(null);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public String getDisplayValue(String value) {
        return getDisplayValue(value, null);
    }

    @Override
    public String getDisplayValue(String value, Locale locale) {
        return getColors().getOrDefault(value, value);
    }

    Map<String, String> getColors() {
        Map<String, String> colors = siteColours();
        WebsiteInfo websiteInfo = getWebsiteInfo();
        addColor(colors, websiteInfo.getColor1());
        addColor(colors, websiteInfo.getColor2());
        return colors;
    }

    WebsiteInfo getWebsiteInfo() {
        HstRequestContext hstRequestContext = RequestContextProvider.get();
        Mount editingMount = MountUtils.getEditingMount(hstRequestContext);
        return editingMount.getChannelInfo();
    }

    Map<String, String> siteColours() {
        Map<String, String> colors = new LinkedHashMap<>();
        colors.put("", "White");
        colors.put("blue", "Blue");
        colors.put("darkblue", "Dark blue");
        colors.put("grey", "Light grey");
        return colors;
    }

    void addColor(Map<String, String> map, String color) {
        if (isBlank(color)) {
            return;
        }

        if (!allowedColours().contains(color)) {
            return;
        }

        String label = ChannelColourValueListProvider.COLOURS.get(color);
        map.put(color, label);
    }

    Set<String> allowedColours() {
        return ChannelColourValueListProvider.COLOURS.keySet();
    }

}
