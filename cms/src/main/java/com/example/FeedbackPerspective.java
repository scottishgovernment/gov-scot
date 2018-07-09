package com.example;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnEventHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;

//import org.apache.wicket.ajax.AjaxRequestTarget;
//import org.apache.wicket.ajax.markup.html.AjaxLink;
//import org.hippoecm.frontend.model.JcrNodeModel;
//import org.hippoecm.frontend.service.IBrowseService;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class FeedbackPerspective extends Perspective {

//    private static final Logger log = LoggerFactory.getLogger(FeedbackPerspective.class);

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PERSPECTIVE_CSS =
        new CssResourceReference(FeedbackPerspective.class, "FeedbackPerspective.css");

    private static final ResourceReference PERSPECTIVE_JS =
        new JavaScriptResourceReference(FeedbackPerspective.class, "FeedbackPerspective.js");

    private final WebMarkupContainer iframe;
    private final String componentMarkupId;

    public FeedbackPerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setOutputMarkupId(true);
        context.registerService(this, "feedback-perspective");

        componentMarkupId = this.getMarkupId();

        iframe = new WebMarkupContainer("feedback-perspective-iframe");
        iframe.setOutputMarkupId(true);

        add(iframe);

        RedirectPage page = new RedirectPage("https://lgv.publishing.gov.scot/#/feedback");

        add(new InlineFrame("rubricFrame", page));

//        AjaxLink link = new AjaxLink("feedbackLink") {
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                final String browserId = config.getString("browser.id");
//
//                final IBrowseService browseService = context.getService(browserId, IBrowseService.class);
//
//                final String location = config.getString("option.location", "");
//
//                if (browseService != null) {
//                    browseService.browse(new JcrNodeModel(location));
//                } else {
//                    log.warn("no browse service found with id '{}', cannot browse to '{}'", browserId, location);
//                }
//                log.info("target: '{}', link: '{}'", target, this);
//            }
//        };
//
//        add(link);
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(PERSPECTIVE_CSS));
        response.render(JavaScriptHeaderItem.forReference(PERSPECTIVE_JS));
        response.render(OnEventHeaderItem.forScript("'" + componentMarkupId + "'", "onreadystatechange", "FeedbackPerspective.showIFrame(\"" + iframe.getMarkupId() + "\");"));
    }

}
