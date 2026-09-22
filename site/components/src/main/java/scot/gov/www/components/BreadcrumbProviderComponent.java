package scot.gov.www.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
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

    private static final String NPF_FOLDER_NAME = "npf";

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

        if (currentBean != null && isNpfSection(request)) {

            addNpfTrailingDocuments(items, currentBean, request);

        } else if (currentBean != null && deepestExpandedMenuItem != null) {

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
        if (isAboutOrPolicies(deepestExpandedMenuItemBean)){
            BreadcrumbItem parent = getBreadcrumbItem(request, currentBean.getParentBean());
            if (parent != null && !"govscot".equals(parent.getTitle()) && !"People".equals(parent.getTitle()) && !menuItems.contains(parent)) {
                menuItems.add(parent);
            }

        }

    }

    /**
     * The NPF (National Performance Framework) folder tree sits at the root of the site and
     * isn't wired into the CMS-managed site menus, so its breadcrumb trail is built directly
     * from content instead of from the deepest expanded menu item.
     *
     * @param request HST request
     * @return true if the current request is for a page in the NPF section
     */
    boolean isNpfSection(final HstRequest request) {
        final String pathInfo = request.getRequestContext().getResolvedSiteMapItem().getPathInfo();
        return pathInfo != null && (NPF_FOLDER_NAME.equals(pathInfo) || pathInfo.startsWith(NPF_FOLDER_NAME + "/"));
    }

    /**
     * Add breadcrumb items for the current bean and each of its ancestors up to (but not
     * including) the NPF landing page, followed by a hard-coded crumb for the NPF landing
     * page itself.
     *
     * @param items       list of breadcrumb items
     * @param currentBean a bean described by URL that is in the NPF content tree
     * @param request     HST request
     */
    private void addNpfTrailingDocuments(final List<BreadcrumbItem> items, final HippoBean currentBean, final HstRequest request) {
        // the NPF landing page and outcome pages are folder-index pages, so the bean resolved
        // for them here is the enclosing folder itself (named "npf" or the outcome's folder
        // name), not a govscot:NPF/govscot:Outcome document instance - only indicator pages
        // resolve to an actual document bean. Detect "am I on the NPF page itself" by name
        // rather than type for this reason.
        if (NPF_FOLDER_NAME.equals(currentBean.getName())) {
            // like every other section, the current page's own title is not repeated in its
            // breadcrumb trail - so on the NPF landing page itself there is nothing to add here
            return;
        }

        // start from the current page's parent, not the current page itself
        HippoBean bean = currentBean.getParentBean();
        while (bean != null && !NPF_FOLDER_NAME.equals(bean.getName())) {
            final BreadcrumbItem item = getBreadcrumbItem(request, bean);
            if (item != null) {
                items.add(item);
            }
            bean = bean.getParentBean();
        }
        items.add(getNpfHomeBreadcrumbItem(request));
    }

    private BreadcrumbItem getNpfHomeBreadcrumbItem(final HstRequest request) {
        final HstRequestContext context = request.getRequestContext();
        final HstLink link = context.getHstLinkCreator().create("/npf/", context.getResolvedMount().getMount());
        return new BreadcrumbItem(link, "National Performance Framework");
    }

    boolean isAboutOrPolicies(HippoBean deepestExpandedMenuItemBean) {
        String name = deepestExpandedMenuItemBean.getName();
        String parentName = deepestExpandedMenuItemBean.getParentBean().getName();
        return StringUtils.equalsAny(name, "about", "directorates", "policies")
                || StringUtils.equals(parentName, "about");
    }
}
