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
                <a class="listed-content-item__link" href="${link}" data-gtm="search-pos-${item?index}">
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
                                <#if item.roleTitle??><h2 class="gamma listed-content-item__title">${item.roleTitle}</h2></#if>
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
                                        <div class="listed-content-item__meta-right">
                                            <#if item.publicationDate??>
                                                <p class="listed-content-item__date">
                                                    <#assign dateFormat = "dd MMM yyyy">
                                                    <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                                        <#assign dateFormat = "dd MMM yyyy KK:mm">
                                                    </#if>
                                                    <@fmt.formatDate value=item.publicationDate.time type="both" pattern=dateFormat />
                                                </p>
                                            </#if>
                                        </div>

                                        <div class="listed-content-item__meta-left">
                                            <p class="listed-content-item__label">${item.label}</p>
                                        </div>
                                    </div>
                                </#if>
                                <h1 class="gamma listed-content-item__title">${item.title?html}</h1>
                            </header>

                            <#if item.summary??>
                                <p class="listed-content-item__summary js-truncate" title="${item.summary?html}" style="max-height: 56px; word-wrap: break-word;">
                                    ${item.summary?html}
                                </p>
                            </#if>
                        </article>
                    </#if>
                </a>
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
