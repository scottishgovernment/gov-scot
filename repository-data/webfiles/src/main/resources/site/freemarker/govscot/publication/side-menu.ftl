<#ftl output_format="HTML">
<nav class="ds_side-navigation  ds_no-margin--top" data-module="ds-side-navigation">
    <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
    <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation">Choose section <span class="ds_side-navigation__expand-indicator"></span></label>

    <ul class="ds_side-navigation__list" id="side-navigation-root">
        <#list pages as page>
            <li class="ds_side-navigation__item">
                <a <@langcompare page document /> class="ds_side-navigation__link  <#if page == currentPage>ds_current</#if>  js-publication-navigation" href="<@hst.link hippobean=page/>" data-page-index="${page?index}">
                    ${page.title}
                </a>
            </li>
        </#list>
    </ul>
</nav>
