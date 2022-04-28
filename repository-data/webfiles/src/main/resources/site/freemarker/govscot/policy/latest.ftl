<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="body-content">

    <@hst.html hippohtml=document.content/>

    <div class="ds_search-results">
        <ol id="search-results-list" class="ds_search-results__list   ds_no-margin--top">

            <#list latest as item>
                <li class="ds_search-result">
                    <@hst.link var="link" hippobean=item/>

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
                            <dd class="ds_metadata__value  ds_content-label">
                                <#if hst.isBeanType(item, "scot.gov.www.beans.News")><#else>
                                    <svg class="ds_icon  ds_icon--20" aria-hidden="true" role="img"><use href="${iconspath}#description"></use></svg>
                                </#if>
                                ${item.label}
                            </dd>
                        </span>

                        <#if (hst.isBeanType(item, "scot.gov.www.beans.News") && item.publicationDate??)
                                || (!hst.isBeanType(item, "scot.gov.www.beans.News") && item.displayDate??)>
                            <span class="ds_metadata__item">
                                <dt class="ds_metadata__key  visually-hidden">Date</dt>

                                <dd class="ds_metadata__value">
                                    <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                        <@fmt.formatDate value=item.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/>
                                    <#elseif item.displayDate.time??>
                                        <@fmt.formatDate value=item.displayDate.time type="both" pattern="dd MMMM yyyy"/>
                                    </#if>
                                </dd>
                            </span>
                        </#if>
                    </dl>
                </li>
            </#list>
        </ol>
    </div>
</div>

<!--noindex-->
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
<!--endnoindex-->
