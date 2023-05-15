package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface DynamicIssueInfo {
    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "topics",
            pickerSelectableNodeTypes = "govscot:DynamicIssue"
    )
    String getDocument();

    @Parameter(name = "showPolicies", displayName = "Show Policies?", defaultValue = "false")
    Boolean getShowPolicies();

    @Parameter(name = "showNews", displayName = "Show News?", defaultValue = "false")
    Boolean getShowNews();

    @Parameter(name = "showPublications", displayName = "Show Publications?", defaultValue = "false")
    Boolean getShowPublications();
}