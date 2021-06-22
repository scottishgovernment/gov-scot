<nav class="ds_side-navigation" data-module="ds-side-navigation">
    <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
    <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation">Choose section <span class="ds_side-navigation__expand-indicator"></span></label>

    <ul class="ds_side-navigation__list" id="side-navigation-root">
        <#list pages as page>
            <li class="ds_side-navigation__item">
                <#if page == currentPage>
                    <a class="ds_side-navigation__link  ds_current" href="<@hst.link hippobean=page/>" data-page-index="${page?index}">
                        ${page.title}
                    </a>
                <#else>
                    <a class="ds_side-navigation__link" href="<@hst.link hippobean=page/>" data-page-index="${page?index}">
                        ${page.title}
                    </a>
                </#if>
            </li>
        </#list>
    </ul>
</nav>
