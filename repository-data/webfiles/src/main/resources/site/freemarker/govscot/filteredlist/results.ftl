<#include "../../include/imports.ftl">

<#-- determine whether we have active parameters -->
<#assign hasActiveParameters = false/>
<#if parameters['term']?has_content || parameters['begin']?has_content || parameters['end']?has_content || parameters['topics']?has_content || parameters['types']?has_content>
    <#assign hasActiveParameters = true/>
</#if>

<#-- this div is here to make use of `result` -->
<div class="filter-buttons--sticky">
    <button class="button  button--secondary  button--no-margin  button--left  button--xsmall  js-show-filters">Filter</button>

    <span class="search-results__count js-search-results-count">Showing <#if hasActiveParameters == false>all</#if> <b>${result.totalSize}</b> items</span>

    <a href="?" class="<#if hasActiveParameters == false>hidden  </#if>js-clear-filters  button button--xsmall button--cancel button--right">Clear</a>
</div>

<section id="search-results" class="search-results">
    <header class="search-results__header">
        <h2 class="hidden">Search results</h2>

        <p class="search-results__count  search-results-header__left">
            <#if hasActiveParameters == true>
                Showing <b>${result.totalSize}</b> <#if result.totalSize == 1>item<#else>items</#if>

                <#if parameters['term']??>
                    <#list parameters['term'] as nested>
                        <#assign term = nested/>
                    </#list>

                    <#if term?has_content>
                        containing <b>${term}</b>
                    </#if>
                </#if>

                <#if parameters['begin']??>
                    <#list parameters['begin'] as nested>
                        <#assign begin = nested/>
                    </#list>

                    <#if begin?has_content>
                        from <b>${begin}</b>
                    </#if>
                </#if>

                <#if parameters['end']??>
                    <#list parameters['end'] as nested>
                        <#assign end = nested/>
                    </#list>

                    <#if end?has_content>
                        to <b>${end}</b>
                    </#if>
                </#if>

                <#if parameters['topics']??>
                    about
                    <#list parameters['topics'] as nested>
                        <b>${nested}</b>
                        <#sep>or</#sep>
                    </#list>
                </#if>

            <#else>
                Showing all <b>${result.totalSize}</b> items
            </#if>
        </p>

        <button type="button" name="filters-clear" class="hidden visible-xsmall button button--small button--secondary js-clear-filters search-results-header__right clear-button">Clear</button>
    </header>

    <ol id="search-results-list" class="search-results__list">
        <#list result.hippoBeans as item>
            <@hst.manageContent hippobean=item/>
            <@hst.link var="link" hippobean=item/>
            <li class="search-results__item  listed-content-item">
                <a class="listed-content-item__link" href="${link}" data-gtm="search-pos-${item?index}">
                    <article class="listed-content-item__article <#if item?is_first>listed-content-item__article--top-border</#if>">
                        <#if item.publicationDate??>
                            <p class="listed-content-item__date">
                                <@fmt.formatDate value=item.publicationDate.time type="both" dateStyle="medium" timeStyle="short"/>
                            </p>
                        </#if>
                        <h2 class="gamma listed-content-item__title">${item.title?html}</h2>
                        <#if item.summary??>
                            <p class="listed-content-item__summary">
                                ${item.summary?html}
                            </p>
                        </#if>
                    </article>
                </a>
            </li>
        </#list>
    </ol>

    <div id="pagination" class="search-results__pagination pagination">
        <#if cparam.showPagination>
            <#include "../../include/pagination.ftl">
        </#if>

        <#assign pageNumber = 1/>
        <#if parameters['page']??>
            <#list parameters['page'] as nested>
                <#assign pageNumber = nested?number/>
            </#list>
        </#if>

        <div class="search-results__pagination  search-results__pagination--small  pagination">
            <button data-page-start="${pageNumber + 1}" id="load-more" class="js-load-more-results  button  button--primary">Load more</button>
        </div>
    </div>
</section>
