package scot.gov.www.components;

import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

import static scot.gov.www.components.ThreeImageCardsComponent.CMS_PICKERS_DOCUMENTS_ONLY;
import static scot.gov.www.components.ThreeImageCardsComponent.INITIAL_PATH;
import static scot.gov.www.components.ThreeImageCardsComponent.TYPE;

public interface ThreeImageCardsComponentInfo {

    @Parameter(name = "document1", required = true)
    @JcrPath(
            isRelative = true,
            pickerInitialPath = INITIAL_PATH,
            pickerSelectableNodeTypes = {TYPE},
            pickerConfiguration = CMS_PICKERS_DOCUMENTS_ONLY)
    String getImage1();

    @Parameter(name = "document2")
    @JcrPath(
            isRelative = true,
            pickerInitialPath = INITIAL_PATH,
            pickerSelectableNodeTypes = {TYPE},
            pickerConfiguration = CMS_PICKERS_DOCUMENTS_ONLY)
    String getImage2();

    @Parameter(name = "document3")
    @JcrPath(
            isRelative = true,
            pickerInitialPath = INITIAL_PATH,
            pickerSelectableNodeTypes = {TYPE},
            pickerConfiguration = CMS_PICKERS_DOCUMENTS_ONLY)
    String getImage3();

    @Parameter(name = "fullwidth", displayName = "Full-width background", defaultValue = "true")
    Boolean getFullWidth();

    @Parameter(name = "showimages", displayName = "Show images", defaultValue = "true")
    Boolean getShowImages();

    @Parameter(name = "small", displayName = "Use small variant on mobile")
    Boolean getSmallVariant();
}
