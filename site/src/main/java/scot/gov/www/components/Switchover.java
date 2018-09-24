package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;

public final class Switchover {

    private Switchover() {
        // Utility class - should not be instantiated
    }

    /**
     * Returns true if the request is for www.gov.scot (or *.www.gov.scot).
     */
    static boolean isLive(HstRequest request) {
        HstRequestContext requestContext = request.getRequestContext();
        String hostGroupName = requestContext
                .getResolvedMount()
                .getMount()
                .getVirtualHost()
                .getHostGroupName();
        return !hostGroupName.endsWith("beta");
    }

}
