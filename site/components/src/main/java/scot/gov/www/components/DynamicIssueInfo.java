package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = { "showNews", "showPublications", "showPolicies", "neutrallinks", "removebottompadding"}),
    @FieldGroup(titleKey = "Content", value = { "document"})
})

public interface DynamicIssueInfo {
    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "topics",
            pickerSelectableNodeTypes = "govscot:DynamicIssue"
    )
    String getDocument();

    @Parameter(name = "showPolicies", displayName = "Show Policies", defaultValue = "false")
    Boolean getShowPolicies();

    @Parameter(name = "showNews", displayName = "Show News", defaultValue = "false")
    Boolean getShowNews();

    @Parameter(name = "showPublications", displayName = "Show Publications", defaultValue = "false")
    Boolean getShowPublications();

    @Parameter(name = "neutrallinks", displayName = "Neutral link colour", defaultValue = "false")
    Boolean getNeutralLinks();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}