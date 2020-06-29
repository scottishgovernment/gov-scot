<nav class="ds_side-navigation" data-module="ds-side-navigation">
    <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
    <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation">Choose section <span class="ds_side-navigation__expand-indicator"></span></label>

    <ul class="ds_side-navigation__list  ds_side-navigation__list--root  inner-shadow-right  inner-shadow-right--no-mobile" id="side-navigation-root">
        <#list pages as page>
            <li class="ds_side-navigation__item">
                <#if page == currentPage>
                    <a class="page-group__link  page-group__link--selected" href="<@hst.link hippobean=page/>" data-page-index="${page?index}">
                        <span class="page-group__text">${page.title}</span>
                    </a>
                <#else>
                    <a class="page-group__link" href="<@hst.link hippobean=page/>" data-page-index="${page?index}">
                        <span class="page-group__text">${page.title}</span>
                    </a>
                </#if>
            </li>
        </#list>
    </ul>
</nav>
