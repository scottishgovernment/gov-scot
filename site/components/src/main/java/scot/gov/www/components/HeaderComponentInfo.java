package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.Parameter;

public interface HeaderComponentInfo {
    @Parameter(name = "text", displayName = "Content", required = true, defaultValue = "header")
    String getText();

    @Parameter(name = "weight", displayName = "Header level", required = true, defaultValue = "h2")
    @DropDownList({"h2", "h3"})
    String getWeight();

    @Parameter(name = "position", displayName = "Position", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getPosition();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentBackgroundColourValueListProvider.class)
    String getBackgroundColor();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
