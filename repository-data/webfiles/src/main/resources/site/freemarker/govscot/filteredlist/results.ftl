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
<#assign filtersCount = filterButtons.types?size +
            filterButtons.topics?size + filterButtons.dates?size />

<#if hstRequestContext.servletRequest.getParameter("page")??>
    <#assign start = (hstRequestContext.servletRequest.getParameter("page")?number - 1) * 10 + 1/>
</#if>

<#if pageable??>

<section id="search-results" class="ds_search-results">
    <header>
        <h2 class="visually-hidden">Search results ${filtersCount}</h2>

        <div class="ds_search-results__count">
            <#if pageable.total = 0>
                <@hst.html hippohtml=document.noResultsMessage/>
                    <button class="gov-clear-filters-button  js-clear-filters  ds_button  ds_button--small  ds_button--cancel  ds_button--has-icon  gov_filters__clear">
                        Clear all filters
                        <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
                    </button>

                    <a href="." class="gov-clear-filters-link  js-clear-filters  ds_button  ds_button--small  ds_button--cancel  ds_button--has-icon  gov_filters__clear">
                        Clear all filters
                        <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
                    </a>
            <#else>
                <#if filtersCount gt 0 || search.query?has_content>
                    <p aria-live="polite" class="js-search-results-count">
                        Showing <b>${pageable.total}</b> <#if pageable.total == 1>${searchTermSingular}<#else>${searchTermPlural}</#if>

                        <#if search.query????>
                            <#if search.query?has_content>
                                containing <b>${search.query}</b>
                            </#if>
                        </#if>

                        <#if search.fromDate??>
                            from <b>${filterButtons.dates.begin.label}</b>
                        </#if>

                        <#if search.toDate??>
                            to <b>${filterButtons.dates.end.label}</b>
                        </#if>

                        <#if search.topics?size gt 0>
                            about

                            <#list search.topics?values?sort as topic>
                                <b>${topic}</b>
                                <#sep>or</#sep>
                            </#list>
                        </#if>

                        <#if search.publicationTypes?size gt 0>
                            of type
                            <#list search.publicationTypes?values?sort as publicationType>
                                <b>${publicationType}</b>
                                <#sep>or</#sep>
                            </#list>
                        </#if>
                    </p>

                    <button class="gov-clear-filters-button  js-clear-filters  ds_button  ds_button--small  ds_button--cancel  ds_button--has-icon  gov_filters__clear">
                        Clear all filters
                        <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
                    </button>

                    <a href="." class="gov-clear-filters-link  js-clear-filters  ds_button  ds_button--small  ds_button--cancel  ds_button--has-icon  gov_filters__clear">
                        Clear all filters
                        <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
                    </a>
                <#else>
                    <p>Showing all <b>${pageable.total}</b> ${searchTermPlural}</p>
                </#if>
            </#if>

        </div>
    </header>

    <ol id="search-results-list" <#if start??>start="${start}"</#if> class="ds_search-results__list" data-total="${pageable.total}">
        <#list pageable.items as item>
            <@hst.link var="link" hippobean=item/>
            <li class="ds_search-result">
                <h3 class="ds_search-result__title">
                    <a class="ds_search-result__link" href="${link}">${item.title}</a>
                </h3>

                <#if item.summary??>
                    <p class="ds_search-result__summary">
                        ${item.summary}
                    </p>
                </#if>

                <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
                    <div class="ds_metadata__item">
                        <dt class="ds_metadata__key">Type</dt>
                        <dd class="ds_metadata__value">${item.label?cap_first}</dd>
                    </div>

                    <#if item.publicationDate??>
                        <div class="ds_metadata__item">
                            <dt class="ds_metadata__key">Date</dt>
                            <#assign dateFormat = "dd MMMM yyyy">
                            <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                <#assign dateFormat = "dd MMMM yyyy HH:mm">
                            </#if>
                            <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
                            <dd class="ds_metadata__value"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
                        </div>
                    </#if>
                </dl>

                <#if item.collections?has_content>
                    <dl class="ds_search-result__context">
                        <dt class="ds_search-result__context-key">Part of:</dt>
                        <#list item.collections as collection>
                            <@hst.link var="link" hippobean=collection/>
                            <dd class="ds_search-result__context-value">
                                <a data-navigation="collections-${collection?index + 1}" href="${link}">${collection.title}</a>
                            </dd>
                        </#list>
                    </dl>
                </#if>
            </li>
        </#list>
    </ol>

    <div>
        <#assign gtmslug = relativeContentPath />
        <#if cparam.showPagination??>
            <#include "../../include/pagination.ftl">
        </#if>
    </div>
</section>
</#if>
