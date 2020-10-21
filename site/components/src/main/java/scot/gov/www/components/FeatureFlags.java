package scot.gov.www.components;

import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;

import java.util.Map;

/**
 * Look up a flag in the featureFlags value list.
 */
public class FeatureFlags {

    private FeatureFlags() {
        // private constructor for utility class
    }

    /**
     * Is this feature enable?  If the flag is missing then the feature will default to being disabled.
     */
    public static boolean isEnabled(String name, HstRequestContext context) {
        return isEnabled(name, context, false);
    }


    /**
     * Allow the default to be determined.  this is usfull for cases such as menus where the flag is not usually present.
     */
    public static boolean isEnabled(String name, HstRequestContext context, boolean defaultValue) {
        ValueList featureFlags = SelectionUtil.getValueListByIdentifier("featureFlags", context);
        Map<String, String> flags = SelectionUtil.valueListAsMap(featureFlags);

        if (!flags.containsKey(name)) {
            return defaultValue;
        }

        return "true".equals(flags.get(name));
    }
}
