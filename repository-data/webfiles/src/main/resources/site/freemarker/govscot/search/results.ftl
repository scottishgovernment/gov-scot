<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="publicationTypes" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Publication" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Person" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Role" -->

<#if pageable??>

<#if hstRequestContext.servletRequest.getParameter("page")??>
    <#assign start = (hstRequestContext.servletRequest.getParameter("page")?number - 1) * 10 + 1/>
</#if>

<section id="search-results" class="ds_search-results">
    <h2 class="visually-hidden">Search results</h2>

    <p class="ds_search-results__count  js-search-results-count">Showing <b>${pageable.total}</b> <#if pageable.total == 1>result<#else>results</#if></p>

    <ol id="search-results-list" <#if start??>start="${start}"</#if> class="ds_search-results__list" data-total="${pageable.total}">
        <#list pageable.items as item>
            <@hst.manageContent hippobean=item/>
            <@hst.link var="link" hippobean=item/>
            <li class="ds_search-result">
                <#if ((hst.isBeanType(item, "scot.gov.www.beans.Role") && item.incumbent??) || hst.isBeanType(item, "scot.gov.www.beans.Person")) && item.image??>
                    <h3 class="ds_search-result__title">
                        <a class="ds_search-result__link" href="${link}">${item.roleTitle}</a>
                    </h3>
                    <div class="ds_search-result__has-media">
                        <div class="ds_search-result__media-wrapper">
                            <div class="ds_search-result__media  ds_aspect-box  ds_aspect-box--square">
                                <a class="ds_search-result__media-link" href="#" tabindex="-1">
                                    <img alt=""
                                        aria-hidden="true"
                                        class="ds_aspect-box__inner"
                                        src="<@hst.link hippobean=item.image.large />"
                                        srcset="<@hst.link hippobean=item.image.small/> 84w,
                                        <@hst.link hippobean=item.image.smalldoubled/> 168w,
                                        <@hst.link hippobean=item.image.large/> 144w,
                                        <@hst.link hippobean=item.image.largedoubled/> 288w"
                                        sizes="(min-width:768px) 144px, 84px">
                                </a>
                            </div>
                        </div>
                        <div>
                            <#if item.name??>
                                <h4 class="ds_search-result__sub-title">${item.name}</h4>
                            </#if>
                            <#if item.summary??>
                                <p class="ds_search-result__summary">${item.summary}</p>
                            </#if>
                        </div>
                    </div>
                <#else>
                    <h3 class="ds_search-result__title">
                        <a class="ds_search-result__link" href="${link}">${item.title}</a>
                    </h3>
                    <#if item.summary??>
                        <p class="ds_search-result__summary">
                            ${item.summary}
                        </p>
                    </#if>

                    <#if item.publicationDate?? || item.label?has_content>
                        <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
                            <#if item.label?has_content>
                                <span class="ds_metadata__item">
                                    <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                    <dd class="ds_metadata__value">${item.label?cap_first}</dd>
                                </span>
                            </#if>

                            <#if item.publicationDate??>
                                <#assign dateFormat = "dd MMMM yyyy">
                                <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
                                <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                    <#assign dateFormat = "dd MMMM yyyy HH:mm">
                                </#if>
                                <span class="ds_metadata__item">
                                    <dt class="ds_metadata__key  visually-hidden">Publication date</dt>
                                    <dd class="ds_metadata__value"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
                                </span>
                            </#if>
                        </dl>
                    </#if>

                    <#if item.collections?has_content || item.parent??>
                        <dl class="ds_search-result__context">
                            <dt class="ds_search-result__context-key">Part of:</dt>
                            <#if item.collections?has_content>
                                <#list item.collections as collection>
                                    <@hst.link var="link" hippobean=collection/>
                                    <dd class="ds_search-result__context-value">
                                        <a data-navigation="collections-${collection?index + 1}" href="${link}">${collection.title}</a>
                                    </dd>
                                </#list>
                            </#if>
                            <#if item.parent??>
                                <dd class="ds_search-result__context-value">
                                    <a href="<@hst.link hippobean=item.parent/>">${item.parent.title}</a>
                                </dd>
                            </#if>
                        </dl>
                    </#if>
                </#if>
            </li>
        </#list>
    </ol>

    <div>
        <#if cparam.showPagination??>
            <#include "../../include/pagination.ftl">
        </#if>
    </div>
</section>
<#else>
<p class="search-results__count js-search-results-count">
<b>0</b> results, please fill in the search field
</#if>
