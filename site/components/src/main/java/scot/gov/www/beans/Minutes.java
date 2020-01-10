package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "govscot:Minutes")
@Node(jcrType = "govscot:Minutes")
public class Minutes extends Publication {
    @HippoEssentialsGenerated(internalName = "govscot:location")
    public String getLocation() {
        return getProperty("govscot:location");
    }

    @HippoEssentialsGenerated(internalName = "govscot:nextMeetingDate")
    public Calendar getNextMeetingDate() {
        return getProperty("govscot:nextMeetingDate");
    }

    @HippoEssentialsGenerated(internalName = "govscot:attendees")
    public HippoHtml getAttendees() {
        return getHippoHtml("govscot:attendees");
    }

    @HippoEssentialsGenerated(internalName = "govscot:actions")
    public HippoHtml getActions() {
        return getHippoHtml("govscot:actions");
    }
}
