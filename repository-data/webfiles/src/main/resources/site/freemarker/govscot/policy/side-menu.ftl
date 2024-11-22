<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<nav class="ds_side-navigation  ds_no-margin--top" data-module="ds-side-navigation">
    <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
    <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation">Choose section <span class="ds_side-navigation__expand-indicator"></span></label>

    <ul class="ds_side-navigation__list" id="side-navigation-root">

        <li class="ds_side-navigation__item">
            <@hst.link var="link" hippobean=index canonical=true/>
            <a class="ds_side-navigation__link<#if document == index && !latest??>  ds_current</#if>" href="${link}"<#if document == index && !latest??> aria-current="page"</#if>>
                Overview
            </a>
        </li>

        <li class="ds_side-navigation__item">
            <a class="ds_side-navigation__link<#if latest??>  ds_current</#if>" href="${link + 'latest/'}"<#if latest??> aria-current="page"</#if>>
                Latest
            </a>
        </li>

        <li class="ds_side-navigation__item">
            <span class="ds_side-navigation__link">
                <b>Policy actions:</b>
            </span>

            <ul class="ds_side-navigation__list">
                <#list policyDetails as policyDetail>
                    <li class="ds_side-navigation__item">
                        <@hst.link var="link" hippobean=policyDetail/>
                        <a <@langcompare policyDetail document/> class="ds_side-navigation__link<#if document == policyDetail>  ds_current</#if>" href="${link}"<#if document == policyDetail> aria-current="page"</#if>>
                            ${policyDetail.title}
                        </a>
                    </li>
                </#list>
            </ul>
        </li>
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
