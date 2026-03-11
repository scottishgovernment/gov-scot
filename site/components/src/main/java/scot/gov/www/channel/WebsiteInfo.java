package scot.gov.www.channel;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface WebsiteInfo extends ChannelInfo {

    @Parameter(name = "searchEnabled", required = true, defaultValue = "false", displayName = "Search enabled?")
    Boolean isSearchEnabled();

    @Parameter(name = "siteTitle", required = true, displayName = "Site title")
    String getSiteTitle();

    @Parameter(name = "defaultCardImage", required = false)
    @JcrPath(
            pickerConfiguration = "cms-pickers/images",
            isRelative = true,
            pickerSelectableNodeTypes = {"govscot:ImageCard"},
            pickerInitialPath = ""
    )
    String getDefaultCardImage();
}
