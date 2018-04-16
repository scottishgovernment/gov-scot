<#include "../../include/imports.ftl">

<section id="search-results" class="search-results">
    <header class="search-results__header">
        <h2 class="hidden">Search results</h2>
        <p class="search-results__count js-search-results-status search-results-header__left"></p>

        <button type="button" name="filters-clear" class="hidden visible-xsmall button button--small button--secondary js-clear-filters search-results-header__right clear-button">Clear</button>
    </header>

    <ol id="search-results-list" class="search-results__list">
    <#list result as item>
        <@hst.manageContent hippobean=item/>
        <@hst.link var="link" hippobean=item/>
        <article class="listed-content-item__article listed-content-item__article--top-border">
            <#if item.publicationDate??>
                <p class="listed-content-item__date">
                    <@fmt.formatDate value=item.publicationDate.time type="both" dateStyle="medium" timeStyle="short"/>
                </p>
            </#if>
            <h2 class="gamma listed-content-item__title"><a href="${link}">${item.title?html}</a></h2>
            <#if item.summary??>
                <p class="listed-content-item__summary">
                ${item.summary?html}
                </p>
            </#if>
        </article>
    </#list>

    </ol>

<div id="pagination" class="search-results__pagination pagination">
    <#if cparam.showPagination>
        <#include "../../include/pagination.ftl">
    </#if>
</div>
</section>
