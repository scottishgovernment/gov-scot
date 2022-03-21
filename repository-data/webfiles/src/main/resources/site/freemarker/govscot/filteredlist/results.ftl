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

<#if hstRequestContext.servletRequest.getParameter("page")??>
    <#assign start = (hstRequestContext.servletRequest.getParameter("page")?number - 1) * 10 + 1/>
</#if>

<#if pageable??>

<section id="search-results" class="ds_search-results">
    <header>
        <h2 class="visually-hidden">Search results</h2>

        <div class="ds_search-results__count">
            <#if hasActiveParameters == true>
                <p class="js-search-results-count">
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
                </p>

                <button class="js-clear-filters  ds_button  ds_button--small  ds_button--cancel  ds_button--has-icon  gov_filters__clear">
                    Clear all filters
                    <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#close"></use></svg>
                </button>
            <#else>
                <p>Showing all <b>${pageable.total}</b> ${searchTermPlural}</p>
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
                    <span class="ds_metadata__item">
                        <dt class="ds_metadata__key  visually-hidden">Type</dt>
                        <dd class="ds_metadata__value">${item.label?cap_first}</dd>
                    </span>

                    <#if item.publicationDate??>
                        <span class="ds_metadata__item">
                            <dt class="ds_metadata__key  visually-hidden">Date</dt>
                            <#assign dateFormat = "dd MMMM yyyy">
                            <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                <#assign dateFormat = "dd MMMM yyyy HH:mm">
                            </#if>
                            <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
                            <dd class="ds_metadata__value"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
                        </span>
                    </#if>
                </dl>

                <#if item.collections?has_content>
                    <#if item.collections?size == 1>
                        <#assign description = 'a collection'/>
                    <#else>
                        <#assign description = '${item.collections?size} collections'/>
                    </#if>
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
