<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="publicationTypes" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Publication" -->

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
                    <article class="listed-content-item__article <#if item?is_first>listed-content-item__article--top-border</#if>">
                        <header class="listed-content-item__heading">
                            <div class="listed-content-item__meta">
                                <div class="listed-content-item__meta-right">
                                    <#if item.publicationDate??>
                                        <span class="listed-content-item__date">
                                            <#assign dateFormat = "dd MMM yyyy">
                                            <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                                <#assign dateFormat = "dd MMM yyyy KK:mm">
                                            </#if>
                                            <@fmt.formatDate value=item.publicationDate.time type="both" pattern=dateFormat />
                                        </span>
                                    </#if>
                                </div>

                                <div class="listed-content-item__meta-left">
                                    <#assign documentType = "${item.class.simpleName}">
                                    <#if hst.isBeanType(item, "scot.gov.www.beans.Publication")>
                                        <#assign documentType = "${publicationTypes[item.publicationType]}" >
                                    </#if>
                                    <p class="listed-content-item__label">${documentType}</p>
                                </div>
                            </div>
                        </header>

                        <h1 class="gamma listed-content-item__title">${item.title?html}</h1>
                        <#if item.summary??>
                            <p class="listed-content-item__summary js-truncate" title="${item.summary?html}" style="max-height: 56px; word-wrap: break-word;">
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
