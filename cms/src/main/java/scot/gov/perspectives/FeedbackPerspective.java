package scot.gov.perspectives;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnEventHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;
import org.hippoecm.frontend.service.IBrowseService;
import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class FeedbackPerspective extends Perspective {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackPerspective.class);

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

        final AjaxLink feedbackLink = new AjaxLink("feedbackLink") {
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.getDynamicExtraParameters()
                        .add("return {'uuid' : jQuery('#' + attrs.c).attr('data-uuid'), 'path' : jQuery('#' + attrs.c).attr('data-path') };");

            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onFeedbackLinkClick(context, config);
            }
        };

        add(feedbackLink);
    }

    public void onFeedbackLinkClick(IPluginContext context, IPluginConfig config) {
        final String browserId = config.getString("browser.id");
        final String location = config.getString("option.location");

        final IBrowseService browseService = context.getService(browserId, IBrowseService.class);

        RequestCycle requestCycle = RequestCycle.get();
        String uuid = requestCycle.getRequest()
                .getRequestParameters()
                .getParameterValue("uuid")
                .toString();

        String path = requestCycle.getRequest()
                .getRequestParameters()
                .getParameterValue("path")
                .toString();

        String nodePath;

        if (!"".equals(uuid)) {
            nodePath = getNodePathFromId(uuid);
        } else {
            nodePath = location + path;
        }

        if (browseService != null) {
            browseService.browse(new JcrNodeModel(nodePath));
        } else {
            LOG.warn("no browse service found with id '{}', cannot browse to '{}'", browserId, nodePath);
        }
    }


    @Override
    public void renderHead(final IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(PERSPECTIVE_CSS));
        response.render(JavaScriptHeaderItem.forReference(PERSPECTIVE_JS));
        response.render(OnEventHeaderItem.forScript("'" + componentMarkupId + "'", "readystatechange", "FeedbackPerspective.showIFrame(\"" + iframe.getMarkupId() + "\");"));
    }

    private String getNodePathFromId(String uuid) {
        try {
            Node thisNode = UserSession.get().getJcrSession().getNodeByIdentifier(uuid);

            return thisNode.getPath();
        } catch (RepositoryException e) {
            LOG.error("Failed to get node for ID {}", uuid, e);
            return null;
        }

    }
}
