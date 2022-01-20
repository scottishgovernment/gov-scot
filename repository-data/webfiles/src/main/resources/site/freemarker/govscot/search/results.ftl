<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="publicationTypes" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Publication" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Person" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Role" -->

<#if pageable??>
<section id="search-results" class="search-results">
    <h2 class="visually-hidden">Search results</h2>

    <p class="ds_search-results__count  js-search-results-count">Showing <b>${pageable.total}</b> <#if pageable.total == 1>result<#else>results</#if></p>

    <ol id="search-results-list" class="ds_search-results__list" data-total="${pageable.total}">
        <#list pageable.items as item>
            <@hst.manageContent hippobean=item/>
            <@hst.link var="link" hippobean=item/>
            <#if ((hst.isBeanType(item, "scot.gov.www.beans.Role") && item.incumbent??) || hst.isBeanType(item, "scot.gov.www.beans.Person")) && item.image??>
                <li class="gov_search-result  gov_search-result--role  gov_search-result--has-image">
            <#else>
                <li class="gov_search-result">
            </#if>
            <#assign position = item_index + ((pageable.currentPage-1) * pageable.pageSize) />
            <#if ((hst.isBeanType(item, "scot.gov.www.beans.Role") && item.incumbent??) || hst.isBeanType(item, "scot.gov.www.beans.Person")) && item.image??>
                <div class="gov_search-result__main">
                    <img alt=""
                        class="gov_search-result__image"
                        src="<@hst.link hippobean=item.image.large />"
                        srcset="<@hst.link hippobean=item.image.small/> 84w,
                        <@hst.link hippobean=item.image.smalldoubled/> 168w,
                        <@hst.link hippobean=item.image.large/> 144w,
                        <@hst.link hippobean=item.image.largedoubled/> 288w"
                        sizes="(min-width:768px) 144px, 84px">

                    <header class="gov_search-result__header">
                        <#if item.roleTitle??>
                            <h2 class="gamma  gov_search-result__title">
                                <a class="gov_search-result__link" href="${link}" data-gtm="search-pos-${item?index + 1}">${item.roleTitle}</a>
                            </h2>
                        </#if>

                        <dl class="gov_search-result__metadata  ds_metadata  ds_metadata--inline">
                            <span class="ds_metadata__item">
                                <dt class="ds_metadata__key  visually-hidden">Role</dt>
                                <dd class="ds_metadata__value  ds_content-label">${item.name}</dd>
                            </span>
                        </dl>
                    </header>

                    <p class="gov_search-result__summary">${item.summary}</p>
                </div>
                <#else>
                <div class="gov_search-result__main">
                    <header class="gov_search-result__header">
                        <#if item.publicationDate?? || item.label?has_content>
                            <dl class="gov_search-result__metadata  ds_metadata  ds_metadata--inline">
                                <#if item.label?has_content>
                                    <span class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                        <dd class="ds_metadata__value  ds_content-label">${item.label}</dd>
                                    </span>
                                </#if>

                                <#if item.publicationDate??>
                                    <#assign dateFormat = "dd MMM yyyy">
                                    <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
                                    <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                        <#assign dateFormat = "dd MMM yyyy HH:mm">
                                    </#if>
                                    <span class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">Publication date</dt>
                                        <dd class="ds_metadata__value  ds_content-label"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
                                    </span>
                                </#if>
                            </dl>
                        </#if>
                        <h2 class="gamma  gov_search-result__title">
                            <a class="gov_search-result__link" href="${link}" data-gtm="search-pos-${item?index + 1}">${item.title?html}</a>
                        </h2>
                    </header>

                    <#if item.summary??>
                        <p class="gov_search-result__summary" title="${item.summary?html}">
                            ${item.summary?html}
                        </p>
                    </#if>
                </div>

                <#if item.collections?has_content>
                    <#if item.collections?size == 1>
                        <#assign description = 'a collection'/>
                    <#else>
                        <#assign description = '${item.collections?size} collections'/>
                    </#if>
                    <div class="gov_search-result__supplemental">This publication is part of ${description}:&nbsp;
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
                    </div>
                </#if>
                <#if item.parent??>
                    <div class="gov_search-result__supplemental">This page is part of a publication:&nbsp;
                        <a href="<@hst.link hippobean=item.parent/>">${item.parent.title}</a>
                    </div>
                </#if>
            </#if>
            </li>
        </#list>
    </ol>

    <div id="pagination">
        <#if cparam.showPagination??>
            <#include "../../include/pagination.ftl">
        </#if>
    </div>
</section>
<#else>
<p class="search-results__count js-search-results-count">
<b>0</b> results, please fill in the search field
</#if>
