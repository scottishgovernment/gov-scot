package scot.gov.www.channel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hippoecm.hst.core.parameters.ValueListProvider;

public class ChannelColourValueListProvider implements ValueListProvider {

    public static final Map<String, String> COLOURS = new LinkedHashMap<>();

    static {
        COLOURS.put("teal","Teal");
        COLOURS.put("darkteal","Dark teal");
        COLOURS.put("green","Green");
        COLOURS.put("darkgreen","Dark green");
        COLOURS.put("orange","Orange");
        COLOURS.put("red","Red");
        COLOURS.put("pink","Pink");
        COLOURS.put("purple","Purple");
        COLOURS.put("brown","Brown");
        COLOURS.put("black","Black");
    }

    private List getValueListForLocale(Locale locale) {
        return Collections.unmodifiableList(new LinkedList<>(COLOURS.keySet()));
    }

    @Override
    public List<String> getValues() {
        return getValueListForLocale(null);
    }

    @Override
    public String getDisplayValue(String value) {
        return getDisplayValue(value, null);
    }

    @Override
    public String getDisplayValue(String value, Locale locale) {
        String displayValue = COLOURS.get(value);
        return (displayValue != null) ? displayValue : value;
    }
}
