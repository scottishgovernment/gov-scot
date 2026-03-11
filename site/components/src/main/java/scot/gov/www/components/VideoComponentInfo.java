package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsDocumentComponentInfo;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = {"backgroundcolor", "verticalcenter", "removebottompadding" }),
    @FieldGroup(titleKey = "Content", value = { "document" })
})

public interface VideoComponentInfo extends EssentialsDocumentComponentInfo {

    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "videos",
            pickerSelectableNodeTypes = "govscot:video"

    )
    String getDocument();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList({"Secondary", "Tertiary", "Theme"})
    String getBackgroundColor();

    @Parameter(name = "verticalcenter", displayName = "Vertically center", defaultValue = "false")
    Boolean getVerticalCenter();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
