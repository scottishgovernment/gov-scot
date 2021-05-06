<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.News" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Publication" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Policy" -->
<#-- @ftlvariable name="searchTermSingular" type="java.lang.String" -->
<#-- @ftlvariable name="searchTermPlural" type="java.lang.String" -->

<#-- Set number format to exclude comma separators -->
<#setting number_format="0.##">

<#-- determine whether we have active parameters -->
<#assign hasActiveParameters = false/>
<#if parameters['term']?has_content || parameters['begin']?has_content || parameters['end']?has_content || parameters['topics']?has_content || parameters['publicationTypes']?has_content>
    <#assign hasActiveParameters = true/>
</#if>

<#if pageable??>
<#-- this div is here to make use of 'pageable' -->
<div class="filter-buttons--sticky">
    <button class="button  button--secondary  button--no-margin  button--left  button--xsmall  js-show-filters">Filter</button>

    <span class="search-results__count js-search-results-count">Showing <#if hasActiveParameters == false>all</#if> <b>${pageable.total}</b> items</span>

    <a href="?" class="<#if hasActiveParameters == false>hidden  </#if>js-clear-filters  button button--xsmall button--cancel button--right">Clear</a>
</div>

<section id="search-results" class="search-results">
    <header class="search-results__header">
        <h2 class="hidden">Search results</h2>

        <p class="search-results__count  search-results-header__left">
            <#if hasActiveParameters == true>
                HERE
                Showing <b>${pageable.total}</b> <#if pageable.total == 1>${searchTermSingular}<#else>${searchTermPlural}</#if>

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

                <#if parameters['publicationTypes']??>
                    of type
                    <#list parameters['publicationTypes'] as nested>
                        <b>${publicationTypes[nested]}</b>
                        <#sep>or</#sep>
                    </#list>
                </#if>

            <#else>
                Showing all <b>${pageable.total}</b> ${searchTermPlural}
            </#if>
        </p>
        <button type="button" name="filters-clear" class="hidden visible-xsmall button button--small button--secondary js-clear-filters search-results-header__right clear-button">Clear</button>
    </header>

    <ol id="search-results-list" class="search-results__list">
        <#list pageable.items as item>
            <@hst.link var="link" hippobean=item/>
            <li class="search-results__item  listed-content-item">
                <article class="listed-content-item__article <#if item?is_first>listed-content-item__article--top-border</#if>">
                    <header class="listed-content-item__header">
                        <div class="listed-content-item__meta">
                            <span class="listed-content-item__label">${item.label}</span>

                            <#if item.publicationDate??>
                                <#assign dateFormat = "dd MMM yyyy">
                                <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                    <#assign dateFormat = "dd MMM yyyy HH:mm">
                                </#if>
                                <span class="listed-content-item__date">| <@fmt.formatDate value=item.publicationDate.time type="both" pattern=dateFormat /></span>
                            </#if>
                        </div>

                        <h2 class="gamma listed-content-item__title">
                            <a class="listed-content-item__link" href="${link}" data-gtm="search-pos-${item?index + 1}">${item.title}</a>
                        </h2>
                    </header>
                    <#if item.summary??>
                        <p class="listed-content-item__summary">
                            ${item.summary}
                        </p>
                    </#if>
                    <#if item.collections?has_content>
                        <#if item.collections?size == 1>
                            <#assign description = 'a collection'/>
                        <#else>
                            <#assign description = '${item.collections?size} collections'/>
                        </#if>
                        <p class="listed-content-item__collections">This publication is part of ${description}:&nbsp;
                            <a href="<@hst.link hippobean=item.collections[0]/>">${item.collections[0].title}</a><!--

                         --><#if item.collections?size gt 1><!--
                             -->,&nbsp;<!--
                             --><a href="#content-item-${item?index}-collections" class="content-data__expand js-display-toggle">
                                    &#43;${item.collections?size - 1}&nbsp;more&nbsp;&hellip;</a>

                                <span id="content-item-${item?index}-collections" class="content-data__additional">
                                    <#list item.collections as collection>
                                        <#if collection?index != 0>
                                            <@hst.link var="link" hippobean=collection/>
                                            <a href="${link}">${collection.title}</a><#sep>,&nbsp;</#sep>
                                        </#if>
                                    </#list>
                                </span>
                            </#if>
                        </p>
                    </#if>
                </article>
            </li>
        </#list>
    </ol>

    <div id="pagination" class="search-results__pagination pagination">
        <#assign gtmslug = relativeContentPath />
        <#if cparam.showPagination>
            <#include "../../include/pagination.ftl">
        </#if>

        <#assign pageNumber = 1/>
        <#if parameters['page']??>
            <#list parameters['page'] as nested>
                <#assign pageNumber = nested?number/>
            </#list>
        </#if>

        <#if pageable.currentPage < pageable.totalPages>
            <div class="search-results__pagination  search-results__pagination--small  pagination">
                <button data-page-start="${pageNumber + 1}" id="load-more" class="js-load-more-results  button  button--primary">Load more</button>
            </div>
        </#if>
    </div>
</section>
</#if>
