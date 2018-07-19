<#include "../../include/imports.ftl">

<nav class="page-group">
    <!-- toggler -->
    <button type="button" class="page-group__toggle visible-xsmall js-show-page-group-list">Choose section &hellip;</button>
    <!-- nav links -->
    <ul class="page-group__list">
        <li class="page-group__item page-group__item--level-0">
            <@hst.link var="link" hippobean=index canonical=true/>
            <#if document == index && !latest??>
                <span class="page-group__link page-group__link--level-0 page-group__link--selected page-group__link--level-0--selected">
                    <span class="page-group__text">Overview</span>
                </span>
            <#else>
                <a class="page-group__link page-group__link--level-0" href="${link}">
                    <span class="page-group__text">Overview</span>
                </a>
            </#if>
        </li>

        <li class="page-group__item page-group__item--level-0">
            <#if latest??>
                <span class="page-group__link page-group__link--level-0 page-group__link--selected page-group__link--level-0--selected">
                    <span class="page-group__text">Latest</span>
                </span>
            <#else>
                <a class="page-group__link page-group__link--level-0" href="${link + 'latest/'}">
                    <span class="page-group__text">Latest</span>
                </a>
            </#if>
        </li>

        <li class="page-group__item page-group__item--level-0">
          <span class="page-group__link page-group__link--level-0">
              <span class="page-group__text">Policy actions</span>
          </span>
        </li>

        <#list policyDetails as policyDetail>
            <li class="page-group__item page-group__item--level-1">
                <#if document.title == policyDetail.title>
                    <span class="page-group__link page-group__link--level-1 page-group__link--selected page-group__link--level-1--selected">
                        <span class="page-group__text">${policyDetail.title}</span>
                    </span>
                <#else>
                    <@hst.link var="link" hippobean=policyDetail/>
                    <a class="page-group__link page-group__link--level-1" href="${link}">
                        <span class="page-group__text">${policyDetail.title}</span>
                    </a>
                </#if>
            </li>
        </#list>
    </ul>
</nav>

<div class="page-group__policy-action-count visible-xsmall">
    <@hst.link var="link" hippobean=index/>
    <#if latest??>
        <span class="page-group__policy-action-count__label">Latest</span>
    <#elseif document == index>
        <span class="page-group__policy-action-count__label">Overview</span>
    <#else>
        <span class="page-group__policy-action-count__label">Policy actions</span>
        &nbsp;${policyDetails?seq_index_of(document) + 1} of ${policyDetails?size}
    </#if>
</div>
