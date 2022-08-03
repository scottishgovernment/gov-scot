package scot.gov.www.channels;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface WebsiteInfo extends ChannelInfo {

    @Parameter(name = "color1", displayName = "Additional colour 1")
    @DropDownList(valueListProvider = ChannelColourValueListProvider.class)
    String getColor1();

    @Parameter(name = "color2", displayName = "Additional colour 2")
    @DropDownList(valueListProvider = ChannelColourValueListProvider.class)
    String getColor2();

    @Parameter(name = "defaultCardImage", required = false)
    @JcrPath(
            pickerConfiguration = "cms-pickers/images",
            isRelative = true,
            pickerSelectableNodeTypes = {"govscot:ImageCard"},
            pickerInitialPath = ""
    )
    String getDefaultCardImage();
}
