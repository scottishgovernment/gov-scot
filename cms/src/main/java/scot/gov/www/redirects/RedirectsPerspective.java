package scot.gov.www.redirects;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;
import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.JcrRedirectRepository;
import scot.gov.publishing.hippo.redirects.PublicationArchiver;
import scot.gov.publishing.hippo.redirects.Redirect;
import scot.gov.publishing.hippo.redirects.RedirectValidator;
import scot.gov.www.archive.ArchiveResultsDialog;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedirectsPerspective extends Perspective {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectsPerspective.class);

    private static final String ARCHIVERS_GROUP_PATH = "/hippo:configuration/hippo:groups/archivers";
    private static final String MEMBERS_PROPERTY = "hipposys:members";

    private String csvText;

    public RedirectsPerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setTitle("Redirects");

        Form<Void> form = new Form<>("form");
        add(form);

        TextArea<String> csvArea = new TextArea<>("csv", new PropertyModel<>(this, "csvText"));
        csvArea.setRequired(true);
        form.add(csvArea);

        form.add(new org.apache.wicket.ajax.markup.html.form.AjaxButton("upload", form) {
            @Override
            protected void onSubmit(org.apache.wicket.ajax.AjaxRequestTarget target) {
                processUpload();
            }

            @Override
            protected void onError(org.apache.wicket.ajax.AjaxRequestTarget target) {
                // validation error (empty textarea) — do nothing, form stays put
            }
        });
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(RedirectsPerspective.class, "RedirectsPerspective.css")));
    }

    public String getCsvText() {
        return csvText;
    }

    public void setCsvText(String csvText) {
        this.csvText = csvText;
    }

    @Override
    public boolean isVisible() {
        return isUserInArchivers();
    }

    private void processUpload() {
        IDialogService dialogService = getPluginContext().getService(
                IDialogService.class.getName(), IDialogService.class);
        if (dialogService == null) {
            LOG.error("IDialogService not available in RedirectsPerspective");
            return;
        }

        List<Redirect> parsed;
        try {
            parsed = new RedirectsCsvParser().parse(csvText);
        } catch (IOException e) {
            LOG.error("Failed to parse CSV", e);
            dialogService.show(new RedirectsErrorDialog(List.of("CSV parse error: " + e.getMessage())));
            return;
        }

        List<String> violations = new RedirectValidator(RedirectsCsvParser.ORIGINS).validateRedirects(parsed);
        if (!violations.isEmpty()) {
            dialogService.show(new RedirectsErrorDialog(violations));
            return;
        }

        try {
            Session session = UserSession.get().getJcrSession();
            JcrRedirectRepository repo = new JcrRedirectRepository(session);
            PublicationArchiver archiver = new PublicationArchiver(session, repo);
            List<Redirect> redirects = expandRedirects(archiver, parsed);
            repo.save(redirects);

            int count = redirects.size();
            String noun = count == 1 ? "redirect" : "redirects";
            dialogService.show(new ArchiveResultsDialog(
                    redirects,
                    count + " " + noun + " created",
                    (ArchiveResultsDialog.OnCloseCallback) () -> {}));
        } catch (RepositoryException e) {
            LOG.error("Failed to save redirects", e);
            dialogService.show(new RedirectsErrorDialog(List.of("Failed to save redirects: " + e.getMessage())));
        }
    }

    private List<Redirect> expandRedirects(PublicationArchiver archiver, List<Redirect> parsed) throws RepositoryException {
        Set<String> explicitPaths = parsed.stream().map(Redirect::getFrom).collect(Collectors.toSet());
        List<Redirect> redirects = new ArrayList<>();
        for (Redirect r : parsed) {
            for (Redirect expanded : archiver.expand(r)) {
                boolean overriddenByExplicitRow = !expanded.getFrom().equals(r.getFrom())
                        && explicitPaths.contains(expanded.getFrom());
                if (!overriddenByExplicitRow) {
                    redirects.add(expanded);
                }
            }
        }
        return redirects;
    }

    private boolean isUserInArchivers() {
        try {
            Session session = UserSession.get().getJcrSession();
            String userId = session.getUserID();
            if (!session.nodeExists(ARCHIVERS_GROUP_PATH)) {
                return false;
            }
            Node groupNode = session.getNode(ARCHIVERS_GROUP_PATH);
            if (!groupNode.hasProperty(MEMBERS_PROPERTY)) {
                return false;
            }
            for (Value member : groupNode.getProperty(MEMBERS_PROPERTY).getValues()) {
                if (userId.equals(member.getString())) {
                    return true;
                }
            }
            return false;
        } catch (RepositoryException e) {
            LOG.error("Error checking archivers group membership", e);
            return false;
        }
    }
}
