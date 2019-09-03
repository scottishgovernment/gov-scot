<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="publicationTypes" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Publication" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Person" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Role" -->

<#-- this div is here to make use of 'pageable' -->

<section id="search-results" class="search-results">
    <h2 class="hidden">Search results</h2>
    <p class="search-results__count js-search-results-count">Showing <b>${pageable.total}</b> <#if pageable.total == 1>result<#else>results</#if></p>

    <ol id="search-results-list" class="search-results__list">
        <#list pageable.items as item>
            <@hst.manageContent hippobean=item/>
            <@hst.link var="link" hippobean=item/>
            <li class="search-results__item  listed-content-item">
                <#if ((hst.isBeanType(item, "scot.gov.www.beans.Role") && item.incumbent??) || hst.isBeanType(item, "scot.gov.www.beans.Person")) && item.image??>
                    <article class="listed-content-item__article <#if item?is_first>listed-content-item__article--top-border</#if> listed-content-item__article--role listed-content-item__article--has-image ">
                        <img alt=""
                            class="listed-content-item__image"
                            src="<@hst.link hippobean=item.image.large />"
                            srcset="<@hst.link hippobean=item.image.small/> 84w,
                            <@hst.link hippobean=item.image.smalldoubled/> 168w,
                            <@hst.link hippobean=item.image.large/> 144w,
                            <@hst.link hippobean=item.image.largedoubled/> 288w"
                            sizes="(min-width:768px) 144px, 84px">

                    <div class="listed-content-item__wrapper">
                        <header class="listed-content-item__heading">
                            <#if item.roleTitle??><h2 class="gamma listed-content-item__title"><a class="listed-content-item__link" href="${link}" data-gtm="search-pos-${item?index}">${item.roleTitle}</a></h2></#if>
                            <p class="listed-content-item__role">${item.name}</p>
                        </header>

                        <p class="listed-content-item__summary js-truncate" title="Role and biography." style="max-height: 56px; word-wrap: break-word;">
                            ${item.summary}
                        </p>
                    </div>
                    </article>
                <#else>
                    <article class="listed-content-item__article <#if item?is_first>listed-content-item__article--top-border</#if> ">
                        <header class="listed-content-item__heading">
                            <#if item.publicationDate?? || item.label?has_content>
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
                            </#if>
                            <h2 class="gamma listed-content-item__title"><a class="listed-content-item__link" href="${link}" data-gtm="search-pos-${item?index}">${item.title?html}</a></h2>
                        </header>

                        <#if item.summary??>
                            <p class="listed-content-item__summary js-truncate" title="${item.summary?html}" style="max-height: 56px; word-wrap: break-word;">
                                ${item.summary?html}
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
                        <#if item.parent??>
                            <p class="listed-content-item__publication">This page is part of a publication:&nbsp;
                                <a href="<@hst.link hippobean=item.parent/>">${item.parent.title}</a>
                            </p>
                        </#if>
                    </article>
                </#if>
            </li>
        </#list>
    </ol>

    <div id="pagination" class="search-results__pagination pagination">
    <#if cparam.showPagination>
        <#assign gtmslug = relativeContentPath />
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


