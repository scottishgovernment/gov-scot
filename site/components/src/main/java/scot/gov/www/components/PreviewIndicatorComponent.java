package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import static org.apache.commons.lang3.StringUtils.endsWith;

public class PreviewIndicatorComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute("isStaging", isStaging(request));
    }

    static boolean isStaging(HstRequest request) {
        Mount resolvedMount = request.getRequestContext().getResolvedMount().getMount();
        return "preview".equals(resolvedMount.getType()) && endsWith(resolvedMount.getAlias(), "-staging");
    }

}
