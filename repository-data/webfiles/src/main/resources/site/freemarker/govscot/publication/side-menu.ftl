<nav class="page-group">

    <h3 class="page-group__title  visible-xsmall">Contents</h3>

    <!-- toggler -->
    <button type="button" class="page-group__toggle visible-xsmall js-show-page-group-list toc-mobile-trigger">Choose section</button>

    <!-- nav links -->
    <ul class="page-group__list js-contents  inner-shadow-right  inner-shadow-right--no-mobile">
    <#list pages as page>
        <li class="page-group__item">
            <#if page == currentPage>
                <a class="page-group__link page-group__link--selected" href="<@hst.link hippobean=page/>" data-page-index="${page?index}">
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