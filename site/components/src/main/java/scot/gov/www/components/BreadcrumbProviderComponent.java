package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.core.sitemenu.HstSiteMenu;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItem;
import org.onehippo.forge.breadcrumb.components.BreadcrumbProvider;
import org.onehippo.forge.breadcrumb.om.Breadcrumb;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreadcrumbProviderComponent extends BreadcrumbProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbProviderComponent.class);

    /**
     * Constructor
     *
     * @param component component that creates this provider
     */
    public BreadcrumbProviderComponent(final BaseHstComponent component) {
        super(component);
    }

    /**
     * Generate the breadcrumb.
     *
     * @param request HST request
     * @return the generated breadcrumb
     */
    @Override
    public Breadcrumb getBreadcrumb(final HstRequest request) {

        final List<String> siteMenuNames = getSitemenuNames();
        LOG.debug("{} creating breadcrumb based on site menu names {}", this.getClass().getName(), siteMenuNames);

        // match deepest menu item for multiple configured menus
        int i = 0;
        HstSiteMenuItem deepestMenuItem = null;
        while (i < siteMenuNames.size() && deepestMenuItem == null) {
            final HstSiteMenu menu = request.getRequestContext().getHstSiteMenus().getSiteMenu(siteMenuNames.get(i));
            if (menu != null) {
                deepestMenuItem = menu.getDeepestExpandedItem();
                LOG.debug("{} creating breadcrumb based on deepest menu item '{}' of menu '{}'", this.getClass().getName(),
                        (deepestMenuItem == null) ? "null" : deepestMenuItem.getName(), menu.getName());
            }
            i++;
        }

        // create items from a current menu item and upwards
        final List<BreadcrumbItem> breadcrumbItems = getMenuBreadcrumbItems(request, deepestMenuItem);
        LOG.debug("{} created {} menu based breadcrumb items: {}", this.getClass().getName(), breadcrumbItems.size(), breadcrumbItems);

        // create items from current content bean and upwards to a current menu item or to content base
        final List<BreadcrumbItem> contentBreadcrumbItems = getContentBreadcrumbItems(request, deepestMenuItem, breadcrumbItems);
        LOG.debug("{} created {} content based breadcrumb items: {}", this.getClass().getName(), contentBreadcrumbItems.size(), contentBreadcrumbItems);

        breadcrumbItems.addAll(contentBreadcrumbItems);

        LOG.debug("{} created {} breadcrumb items: {}", this.getClass().getName(), breadcrumbItems.size(),
                breadcrumbItems.stream().map(BreadcrumbItem::getTitle).toArray());

        return new Breadcrumb(breadcrumbItems, getSeparator(), null);
    }

    /**
     * Generate the trailing breadcrumb items. By default, the trailing items
     * are derived from the bean structure of the resolved sitemap item of the
     * current request.
     *
     * @param request                 HST request
     * @param deepestExpandedMenuItem HST menu item
     * @return list of trailing breadcrumb items
     */
    protected List<BreadcrumbItem> getContentBreadcrumbItems(final HstRequest request, final HstSiteMenuItem deepestExpandedMenuItem, List<BreadcrumbItem> menuItems) {

        final List<BreadcrumbItem> items = new ArrayList<>();

        final ResolvedSiteMapItem currentSmi = request.getRequestContext().getResolvedSiteMapItem();
        final HippoBean currentBean = getBeanForResolvedSiteMapItem(request, currentSmi);

        if (currentBean != null && deepestExpandedMenuItem != null) {

            final ResolvedSiteMapItem deepestExpandedmenuItemSmi = deepestExpandedMenuItem.resolveToSiteMapItem();
            final HippoBean deepestExpandedMenuItemBean = getBeanForResolvedSiteMapItem(request, deepestExpandedmenuItemSmi);

            addTrailingDocument(menuItems, items, currentBean, deepestExpandedMenuItemBean, request);
        }

        Collections.reverse(items);

        return items;
    }

    /**
     * Add one trailing document since the addTrailingDocumentOnly flag is up.
     *
     * @param items                       list of breadcrumb items
     * @param currentBean                 a bean described by URL that is in the child tree of the ancestor bean
     * @param deepestExpandedMenuItemBean bean corresponding to the deepest expanded site menu item
     * @param request                     HST request
     */
    protected void addTrailingDocument(List<BreadcrumbItem> menuItems, final List<BreadcrumbItem> items, final HippoBean currentBean,
                                       final HippoBean deepestExpandedMenuItemBean, final HstRequest request) {

        // if the current page is in the breadcrumbs already, remove it
        BreadcrumbItem itemToRemove = null;

        for (BreadcrumbItem item: menuItems) {
            if (item.getTitle().equals(currentBean.getDisplayName())){
                itemToRemove = item;
            }
        }

        menuItems.remove(itemToRemove);

        // if we are in About or Policies, add the parent folder to the breadcrumbs if it's not already there
        if ("about".equals(deepestExpandedMenuItemBean.getName())
                || "about".equals(deepestExpandedMenuItemBean.getParentBean().getName())
                || "directorates".equals(deepestExpandedMenuItemBean.getName())
                || "policies".equals(deepestExpandedMenuItemBean.getName()) ){

            BreadcrumbItem parent = getBreadcrumbItem(request, currentBean.getParentBean());

            if (parent != null && !"govscot".equals(parent.getTitle()) && !menuItems.contains(parent)) {
                menuItems.add(parent);
            }

        }

    }

}
