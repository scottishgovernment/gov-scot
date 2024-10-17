package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = { "neutrallinks", "showimages", "small", "removebottompadding" }),
    @FieldGroup(titleKey = "Content", value = { "document1", "document2" })
})

public interface TextAndCardComponentInfo {
    @Parameter(name = "document1", displayName = "Text document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "text",
            pickerSelectableNodeTypes = "govscot:text"
    )
    String getDocument();

    @Parameter(name = "document2", displayName = "Card document")
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerSelectableNodeTypes = "govscot:navigationcardcontentblock"
    )
    String getImage();

    @Parameter(name = "showimages", displayName = "Show images", defaultValue = "true")
    Boolean getShowImages();

    @Parameter(name = "small", displayName = "Use small variant on mobile", defaultValue = "true")
    Boolean getSmallVariant();

    @Parameter(name = "neutrallinks", displayName = "Neutral link colour", defaultValue = "false")
    Boolean getNeutralLinks();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
