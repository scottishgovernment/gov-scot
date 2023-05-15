package scot.gov.www.components;

import java.util.*;

public class ComponentForegroundColourValueListProvider extends ComponentBackgroundColourValueListProvider {

    private static Set<String> COLOURS = new HashSet<>();

    static {
        COLOURS.add("default");
        COLOURS.add("blue");
        COLOURS.add("darkblue");
        COLOURS.add("darkteal");
        COLOURS.add("darkgreen");
        COLOURS.add("red");
        COLOURS.add("pink");
        COLOURS.add("purple");
        COLOURS.add("brown");
        COLOURS.add("black");
    }

    @Override
    Map<String, String> siteColours() {
        Map<String, String> colors = new LinkedHashMap<>();
        colors.put("", "Default");
        colors.put("blue", "Blue");
        colors.put("darkblue", "Dark blue");
        return colors;
    }

    @Override
    Set<String> allowedColours() {
        return COLOURS;
    }
}
