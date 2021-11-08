<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="body-content">

    <@hst.html hippohtml=document.content/>

    <div class="ds_search-results">
        <ol id="search-results-list" class="ds_search-results__list   ds_no-margin--top">
            <#list latest as item>
                <li class="gov_search-result">

                    <@hst.link var="link" hippobean=item/>
                    <div class="gov_search-result__main">
                        <header class="gov_search-result__header">
                            <dl class="gov_search-result__metadata  ds_metadata  ds_metadata--inline">
                                <span class="ds_metadata__item">
                                    <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                    <dd class="ds_metadata__value  ds_content-label">
                                        <#if hst.isBeanType(item, "scot.gov.www.beans.News")><#else>
                                            <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#description"></use></svg>
                                        </#if>
                                        ${item.label}
                                    </dd>
                                </span>

                                <#if item.publicationDate??>
                                    <span class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">Date</dt>
                                        <#assign dateFormat = "dd MMM yyyy">
                                        <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                            <#assign dateFormat = "dd MMM yyyy HH:mm">
                                        <#else>
                                            <#assign dateFormat = "dd MMM yyyy">
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
                </li>
            </#list>
        </ol>
    </div>
</div>

<nav class="ds_sequential-nav" aria-label="Article navigation">
    <#if prev??>
        <@hst.link var="link" hippobean=prev/>
        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
            <a title="Previous section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                <span class="ds_sequential-nav__text" data-label="previous">
                    ${prev.title}
                </span>
            </a>
        </div>
    </#if>

    <#if next??>
        <@hst.link var="link" hippobean=next/>
        <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
            <a title="Next section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                <span class="ds_sequential-nav__text" data-label="next">
                    ${next.title}
                </span>
            </a>
        </div>
    </#if>
</nav>
