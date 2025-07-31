package demo.hippo.email;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public final class FreemarkerParser {

    public static final String ENCODING = "UTF-8";
    public static final String DEFAULT_PACKAGE = "org/onehippo/connect/email/templates/";
    public static final String DEFAULT_SUFFIX = ".ftl";

    private static Logger log = LoggerFactory.getLogger(FreemarkerParser.class);


    /**
     * Fetch <strong>HTML</strong> template from classpath and populate it with provided data.
     * Template path is constructed as follows: {@code templateName + Html + .ftl} so, <strong>requestPublish</strong> becomes:
     * <strong>requestPublishHtml.ftl</strong>
     *
     * @param templateName name of the template e.g. requestPublish
     * @param data         template data
     * @param escapeHtml   apply HTML escaping
     * @return string containing parsed & populated template
     * @see FreemarkerParser#DEFAULT_PACKAGE
     */
    public static String getHtmlBody(final String templateName, final Map<String, Object> data, final boolean escapeHtml) {
        return bodyFromClasspath(templateName, data, "Html", escapeHtml);
    }

    /**
     * Fetch <strong>TEXT</strong> template from classpath and populate it with provided data.
     * Template path is constructed as follows: {@code templateName + Html + .ftl} so, <strong>requestPublish</strong> becomes:
     * <strong>requestPublishHtml.ftl</strong>
     *
     * @param templateName name of the template e.g. requestPublish
     * @param data         template data
     * @param escapeHtml   apply HTML escaping
     * @return string containing parsed & populated template
     * @see FreemarkerParser#DEFAULT_PACKAGE
     */
    public static String getTextBody(final String templateName, final Map<String, Object> data, final boolean escapeHtml) {
        return bodyFromClasspath(templateName, data, "Text", escapeHtml);
    }


    /**
     * Populate given freemarker  it with provided data
     *
     * @param freemarkerSource freemarker source
     * @param data             template data
     * @param escapeHtml       apply HTML escaping
     * @return string containing parsed & populated template
     */
    public static String getBodyForTemplate(final String freemarkerSource, final Map<String, Object> data, final boolean escapeHtml) {
        final Template template = createEmailTemplate(freemarkerSource, escapeHtml);
        return populateData(template, data);
    }


    private static String bodyFromClasspath(final String templateName, final Map<String, Object> data, final String type, final boolean escapeHtml) {
        final String location = DEFAULT_PACKAGE + templateName + type + DEFAULT_SUFFIX;
        final Template template = createTemplate(location, escapeHtml);
        return populateData(template, data);
    }


    private static Template createTemplate(final String templatePath, boolean escapeHtml) {
        try {
            final ClassTemplateLoader ctl = escapeHtml ? new FreemarkerHtmlEscapedClassTemplateLoader(FreemarkerParser.class, "/") : new ClassTemplateLoader(FreemarkerParser.class, "/");
            final Configuration cfg = new Configuration();
            cfg.setTemplateLoader(ctl);

            return cfg.getTemplate(templatePath, ENCODING);

        } catch (IOException e) {
            log.error("Error loading freemarker template:" + templatePath, e);
        }
        return null;

    }

    /**
     * Create freemarker template for given String
     *
     * @param templateContent string containing ftl markup (freemarker template)  @return freemarker template or null
     *                        if creation fails
     * @param escapeHtml      Escape HTML?
     * @return template
     */
    private static Template createEmailTemplate(final String templateContent, boolean escapeHtml) {
        StringTemplateLoader loader = new StringTemplateLoader();
        String name = "email_template";
        loader.putTemplate(name, escapeHtml ? FreemarkerHtmlEscapedClassTemplateLoader.addHtmlEscapeDirectives(templateContent) : templateContent);
        Configuration config = new Configuration();
        config.setTemplateLoader(loader);
        try {
            return config.getTemplate(name, ENCODING);
        } catch (IOException e) {
            log.error("Error creating freemaker template", e);
        }
        return null;
    }


    /**
     * Creates body of email message which we are going to send
     *
     * @param template freemarker template
     * @param data     data which will be used to populate template variables
     * @return null if populating fails
     */
    private static String populateData(final Template template, final Map<String, Object> data) {
        try {
            // NOTE: no cleanup needed
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return StringUtils.trimToEmpty(writer.toString());
        } catch (TemplateException | IOException e) {
            log.error("Error populating template data", e);
        }
        return null;
    }


    private FreemarkerParser() {
    }

}


