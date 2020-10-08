package scot.gov.www.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "govscot:Migration")
@Node(jcrType = "govscot:Migration")
public class Migration extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "govscot:migrationId")
    public String getMigrationId() {
        return getSingleProperty("govscot:migrationId");
    }
}
