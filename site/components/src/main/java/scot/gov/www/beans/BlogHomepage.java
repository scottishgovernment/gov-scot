package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.List;

@HippoEssentialsGenerated(internalName = "govscot:BlogHomepage")
@Node(jcrType = "govscot:BlogHomepage")
public class BlogHomepage extends AttributableContent {

    @HippoEssentialsGenerated(internalName = "govscot:featuredPosts")
    public List<BlogPost> getFeaturedPosts() {
        return getLinkedBeans("govscot:featuredPosts", BlogPost.class);
    }

    public String getLabel() {
        return "bloghomepage";
    }
}
