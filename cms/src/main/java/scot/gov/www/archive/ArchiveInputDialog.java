package scot.gov.www.archive;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.value.IValueMap;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.redirects.JcrRedirectRepository;
import scot.gov.publishing.hippo.redirects.PublicationArchiver;
import scot.gov.publishing.hippo.redirects.Redirect;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Collections;
import java.util.List;

public class ArchiveInputDialog extends AbstractDialog<String> {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveInputDialog.class);

    private static final String PAGE_NOT_FOUND_QUERY =
            "SELECT * FROM govscot:PageNotFound WHERE hippostd:state = 'published'";

    private final IPluginContext pluginContext;
    private final Node handleNode;
    private final ArchiveResultsDialog.OnCloseCallback onClose;
    private String archiveUrl;

    public ArchiveInputDialog(IPluginContext pluginContext, Node handleNode, ArchiveResultsDialog.OnCloseCallback onClose) {
        this.pluginContext = pluginContext;
        this.handleNode = handleNode;
        this.onClose = onClose;
        this.archiveUrl = loadArchiveBaseUrl();

        TextField<String> urlField = new TextField<>("archive-url", new PropertyModel<>(this, "archiveUrl"));
        urlField.setRequired(true);
        urlField.setOutputMarkupId(true);
        add(urlField);
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(String archiveUrl) {
        this.archiveUrl = archiveUrl;
    }

    @Override
    public IModel<String> getTitle() {
        return Model.of("Create archive redirects");
    }

    @Override
    public IValueMap getProperties() {
        return new org.apache.wicket.util.value.ValueMap("width=620,height=auto").makeImmutable();
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(ArchiveInputDialog.class, "ArchiveDialog.css")));
    }

    @Override
    protected void onOk() {
        try {
            Session session = UserSession.get().getJcrSession();
            JcrRedirectRepository repo = new JcrRedirectRepository(session);
            List<Redirect> redirects = buildRedirects(session, repo);
            repo.save(redirects);

            IDialogService dialogService = pluginContext.getService(
                    IDialogService.class.getName(), IDialogService.class);

            if (dialogService != null) {
                int count = redirects.size();
                String noun = count == 1 ? "redirect" : "redirects";
                dialogService.show(new ArchiveResultsDialog(redirects, count + " " + noun + " created", onClose));
            }
        } catch (RepositoryException e) {
            LOG.error("Failed to create archive redirects", e);
            error("Failed to create archive redirects: " + e.getMessage());
        }
    }

    private List<Redirect> buildRedirects(Session session, JcrRedirectRepository repo) throws RepositoryException {
        String documentPath = ArchiveDocumentUtils.getDocumentPath(handleNode);
        String archiveTarget = buildArchiveTarget(archiveUrl, documentPath);

        Redirect baseRedirect = new Redirect();
        baseRedirect.setFrom(documentPath);
        baseRedirect.setTo(archiveTarget);
        baseRedirect.setHistoricalUrl(true);
        baseRedirect.setDescription("Archived: " + documentPath);

        if (ArchiveDocumentUtils.isPublication(handleNode)) {
            PublicationArchiver archiver = new PublicationArchiver(session, repo);
            List<Redirect> expanded = archiver.expand(baseRedirect);
            if (!expanded.isEmpty()) {
                return expanded;
            }
            LOG.warn("PublicationArchiver.expand returned no redirects for {}, using single redirect", documentPath);
        }
        return Collections.singletonList(baseRedirect);
    }

    private String buildArchiveTarget(String baseUrl, String documentPath) {
        String base = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        String path = documentPath.startsWith("/") ? documentPath : "/" + documentPath;
        return base + path;
    }

    private String loadArchiveBaseUrl() {
        try {
            Session session = UserSession.get().getJcrSession();
            QueryResult result = session.getWorkspace().getQueryManager()
                    .createQuery(PAGE_NOT_FOUND_QUERY, Query.SQL)
                    .execute();
            NodeIterator nodes = result.getNodes();
            if (nodes.hasNext()) {
                Node pageNotFound = nodes.nextNode();
                return JcrUtils.getStringProperty(pageNotFound, "govscot:archiveUrl", "");
            }
            LOG.warn("No published PageNotFound document found; archive URL will be empty");
        } catch (RepositoryException e) {
            LOG.error("Failed to load archive base URL from 404 page", e);
        }
        return "";
    }
}
