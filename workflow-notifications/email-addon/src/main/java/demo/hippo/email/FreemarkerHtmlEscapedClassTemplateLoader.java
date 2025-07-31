package demo.hippo.email;

import freemarker.cache.ClassTemplateLoader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Freemarker class template loader adding HTML escape directives (if not already present)
 * (see http://freemarker.sourceforge.net/docs/ref_directive_escape.html)
 */
public final class FreemarkerHtmlEscapedClassTemplateLoader extends ClassTemplateLoader {

    /**
     * HTML escape directive prefix
     */
    private static final String ESCAPE_HTML_DIRECTIVE_PREFIX = "<#escape x as x?html>\n";

    /**
     * HTML escape directive suffix
     */
    private static final String ESCAPE_HTML_DIRECTIVE_SUFFIX = "\n</#escape>";

    /**
     * Add HTML template directives to the template (if not already present)
     *
     * @param template Template
     * @return Template with HTML escape directives
     */
    public static String addHtmlEscapeDirectives(String template) {
        if (template == null) {
            return null;
        }
        return template.contains(ESCAPE_HTML_DIRECTIVE_PREFIX) ? template :
                ESCAPE_HTML_DIRECTIVE_PREFIX + template + ESCAPE_HTML_DIRECTIVE_SUFFIX;
    }

    /**
     * Constructor
     *
     * @param loaderClass Loader class
     * @param path        Path
     */
    public FreemarkerHtmlEscapedClassTemplateLoader(Class loaderClass, String path) {
        super(loaderClass, path);
    }


    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new StringReader(addHtmlEscapeDirectives(IOUtils.toString(super.getReader(templateSource, encoding))));
    }
}
