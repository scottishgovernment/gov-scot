<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

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
    <button class="ds_button  ds_button--secondary  ds_button--small  ds_no-margin  js-show-filters">Filter</button>

    <span class="search-results__count  js-search-results-count">Showing <#if hasActiveParameters == false>all</#if> <b>${pageable.total}</b> items</span>

    <a href="?" class="<#if hasActiveParameters == false>hidden  </#if>js-clear-filters  button button--xsmall button--cancel button--right">Clear</a>
</div>

<section id="search-results" class="ds_search-results">
    <header style="outline: 4px dashed red">
        <h2 class="visually-hidden">Search results</h2>

        <p class="ds_search-results__count  js-search-results-count">
            <#if hasActiveParameters == true>
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

        <button type="button" name="filters-clear" class="visually-hidden  ds_button  ds_button--cancel  ds_button--small  js-clear-filters">Clear</button>
    </header>

    <ol id="search-results-list" class="ds_search-results__list">
        <#list pageable.items as item>
            <@hst.link var="link" hippobean=item/>
            <li class="gov_search-result">
                <div class="gov_search-result__main">
                    <header class="gov_search-result__header">
                        <dl class="gov_search-result__metadata  ds_metadata  ds_metadata--inline">
                            <span class="ds_metadata__item">
                                <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                <dd class="ds_metadata__value  ds_content-label">${item.label}</dd>
                            </span>

                            <#if item.publicationDate??>
                                <span class="ds_metadata__item">
                                    <dt class="ds_metadata__key  visually-hidden">Date</dt>
                                    <#assign dateFormat = "dd MMM yyyy">
                                    <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                        <#assign dateFormat = "dd MMM yyyy HH:mm">
                                    </#if>
                                    <dd class="ds_metadata__value"><@fmt.formatDate value=item.publicationDate.time type="both" pattern=dateFormat /></dd>
                                </span>
                            </#if>
                        </dl>

                        <h2 class="gamma  gov_search-result__title">
                            <a class="gov_search-result__link" href="${link}">${item.title}</a>
                        </h2>
                    </header>

                    <#if item.summary??>
                        <p class="gov_search-result__summary">
                            ${item.summary}
                        </p>
                    </#if>
                </div>

                <#if item.collections?has_content>
                    <#if item.collections?size == 1>
                        <#assign description = 'a collection'/>
                    <#else>
                        <#assign description = '${item.collections?size} collections'/>
                    </#if>
                    <p class="gov_search-result__supplemental">
                        <svg class="ds_icon ds_!_margin-right--1">
                            <use xlink:href="${iconspath}#document"></use>
                        </svg>

                        This publication is part of ${description}:&nbsp;
                        <a href="<@hst.link hippobean=item.collections[0]/>">${item.collections[0].title}</a><!--

                        --><#if item.collections?size gt 1><!--
                            -->,&nbsp;<!--
                            --><a href="#content-item-${item?index}-collections">
                                &#43;${item.collections?size - 1}&nbsp;more&nbsp;&hellip;</a>

                            <span id="content-item-${item?index}-collections">
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
            </li>
        </#list>
    </ol>

    <div id="pagination">
        <#assign gtmslug = relativeContentPath />
        <#if cparam.showPagination??>
            <#include "../../include/pagination.ftl">
        </#if>
    </div>
</section>
</#if>
