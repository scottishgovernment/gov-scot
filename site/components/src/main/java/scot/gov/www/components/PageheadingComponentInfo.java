package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsDocumentComponentInfo;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = {"backgroundcolor", "fullwidth"}),
    @FieldGroup(titleKey = "Formatting", value = {"widetext", "verticalalign", "imagenomargin", "imagecover", "imagealignmobile" }),
    @FieldGroup(titleKey = "Content", value = {"document"})
})

public interface PageheadingComponentInfo extends EssentialsDocumentComponentInfo {

    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerSelectableNodeTypes = "govscot:pageheading"
    )
    String getDocument();

    @Parameter(name = "fullwidth", displayName = "Full-width background", defaultValue = "true")
    Boolean getFullWidth();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList({"Secondary", "Tertiary", "Theme", "Theme reversed"})
    String getBackgroundColor();

    @Parameter(name = "verticalalign", displayName = "Vertical align", required = true, defaultValue = "top")
    @DropDownList({"top", "middle"})
    String getVerticalAlign();

    @Parameter(name = "widetext", displayName = "Increase width of text", defaultValue = "false")
    Boolean getWideText();

    @Parameter(name = "imagenomargin", displayName = "Remove padding around image (only applicable if full width background)", defaultValue = "false")
    Boolean getImageNoMargin();

    @Parameter(name = "imagecover", displayName = "Set image to cover available space (desktop)", defaultValue = "false")
    Boolean getImageCover();

    @Parameter(name = "imagealignmobile", displayName = "Image horizontal alignment (mobile)", defaultValue = "left")
    @DropDownList({"left", "right", "center", "hidden"})
    String getImageAlignMobile();
}
