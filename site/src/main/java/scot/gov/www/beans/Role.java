package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoBean;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:Role")
@Node(jcrType = "govscot:Role")
public class Role extends SimpleContent {

    private List<HippoBean> directorates;

    public List<HippoBean> getDirectorates() {
        return directorates;
    }

    public void setDirectorates(List<HippoBean> directorates) {
        this.directorates = directorates;
    }

    @HippoEssentialsGenerated(internalName = "govscot:responsibilities")
    public HippoHtml getResponsibilities() {
        return getHippoHtml("govscot:responsibilities");
    }

    @HippoEssentialsGenerated(internalName = "govscot:incumbent")
    public Person getIncumbent() {
        return getLinkedBean("govscot:incumbent", Person.class);
    }

    public String getLabel() { return "role"; }

    public People getImage() { return getIncumbent().getImage(); }

    public String getName() { return getIncumbent() != null ? getIncumbent().getTitle() : ""; }

    public String getRoleTitle() { return getTitle(); }
}
