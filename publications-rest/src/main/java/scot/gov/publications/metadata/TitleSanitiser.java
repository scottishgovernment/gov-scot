package scot.gov.publications.metadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class TitleSanitiser {

    private static final Map<Character, Character> replacements;

    static {
        replacements = new HashMap<>();
        replacements.put('’', '\'');
        replacements.put('‘', '\'');
        replacements.put('“', '"');
        replacements.put('”', '"');
    }

    private TitleSanitiser() {
        // utility class
    }

    public static String sanitise(String str) {
        if (isBlank(str)) {
            return "";
        }
        return replace(str, new HashSet<>(replacements.keySet()));
    }

    private static String replace(String str, Set<Character> keys) {
        if (keys.isEmpty()) {
            return str;
        }
        Character ch = keys.iterator().next();
        String newStr = str.replace(ch, replacements.get(ch));
        keys.remove(ch);
        return replace(newStr, keys);
    }

}
