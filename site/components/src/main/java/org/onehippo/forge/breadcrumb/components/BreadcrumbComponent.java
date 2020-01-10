package org.onehippo.forge.breadcrumb.components;

import javax.servlet.ServletContext;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.ComponentConfiguration;

/**
 * Standard HST Breadcrumb component.
 */
public class BreadcrumbComponent extends BaseHstComponent {

    private BreadcrumbProvider breadcrumbProvider;

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);
        request.setAttribute(BreadcrumbProvider.ATTRIBUTE_NAME, breadcrumbProvider.getBreadcrumb(request));
    }

    @Override
    public void init(final ServletContext servletContext, final ComponentConfiguration componentConfig) throws HstComponentException {
        super.init(servletContext, componentConfig);
        breadcrumbProvider = new BreadcrumbProvider(this);
    }


}