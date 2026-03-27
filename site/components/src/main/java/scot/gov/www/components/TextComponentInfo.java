package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = { "position", "backgroundcolor", "removebottompadding" }),
    @FieldGroup(titleKey = "Content", value = { "document" })
})

public interface TextComponentInfo {
    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "text",
            pickerSelectableNodeTypes = "govscot:text"
    )
    String getDocument();

    @Parameter(name = "position", displayName = "Position", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getPosition();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList({"Secondary", "Tertiary", "Theme"})
    String getBackgroundColor();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
