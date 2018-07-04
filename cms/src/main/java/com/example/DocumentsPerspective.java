package com.example;

import javax.jcr.Session;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;
import org.hippoecm.frontend.session.UserSession;

public class DocumentsPerspective extends Perspective {

    private static final long serialVersionUID = 1L;

    private static final ResourceReference PERSPECTIVE_CSS =
            new CssResourceReference(DocumentsPerspective.class, "DocumentsPerspective.css");

    public DocumentsPerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setOutputMarkupId(true);
        Session session = UserSession.get().getJcrSession();
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(PERSPECTIVE_CSS));
    }

}
