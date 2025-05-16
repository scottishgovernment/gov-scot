package scot.gov.www.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.onehippo.forge.breadcrumb.components.BreadcrumbComponent;
import org.onehippo.forge.breadcrumb.components.BreadcrumbParametersInfo;
import org.onehippo.forge.breadcrumb.components.BreadcrumbProvider;

import jakarta.servlet.ServletContext;

@ParametersInfo(type = BreadcrumbParametersInfo.class)
public class BreadcrumbRenderingComponent extends BreadcrumbComponent {

    private BreadcrumbProvider breadcrumbProvider;

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute(BreadcrumbProvider.ATTRIBUTE_NAME, breadcrumbProvider.getBreadcrumb(request));
    }

    @Override
    public void init(final ServletContext servletContext, final ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        breadcrumbProvider = new BreadcrumbProviderComponent(this);
    }

}
