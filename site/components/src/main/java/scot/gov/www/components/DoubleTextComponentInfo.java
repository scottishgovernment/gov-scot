package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = { "backgroundcolor", "removebottompadding" }),
    @FieldGroup(titleKey = "Content", value = { "document1", "document2" })
})

public interface DoubleTextComponentInfo {
    @Parameter(name = "document1", required = true)
    @JcrPath(
        isRelative = true,
        pickerConfiguration = "cms-pickers/documents-only",
        pickerInitialPath = "text",
        pickerSelectableNodeTypes = "govscot:text"
    )
    String getDocument1();

    @Parameter(name = "document2", required = true)
    @JcrPath(
        isRelative = true,
        pickerConfiguration = "cms-pickers/documents-only",
        pickerInitialPath = "text",
        pickerSelectableNodeTypes = "govscot:text"
    )
    String getDocument2();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentBackgroundColourValueListProvider.class)
    String getBackgroundColor();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
