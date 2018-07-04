package com.example;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;

public class FeedbackPerspective extends Perspective {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PERSPECTIVE_CSS =
            new CssResourceReference(FeedbackPerspective.class, "FeedbackPerspective.css");

    public FeedbackPerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(PERSPECTIVE_CSS));
    }

}
