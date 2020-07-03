<#include "../../include/imports.ftl">

<div class="content-data__expandable">
    <button class="js-expand  expand  expand--mobile-only  content-data__toggle" data-target-selector="#expandable-content-data" title="Show details">
        <span class="hit-target">
            <span class="expand__icon"></span>
        </span>
    </button>
    <dl class="content-data__list" id="expandable-content-data">
    <#if index.allResponsibleRoles?has_content>
        <dt class="content-data__label">From:</dt>

        <dd class="content-data__value">
            <@hst.link var="link" hippobean=index.allResponsibleRoles[0]/>
            <a href="${link}">${index.allResponsibleRoles[0].title}</a><!--

         --><#if index.allResponsibleRoles?size gt 1><!--
         -->, <!--
             --><a href="#secondary-responsible-roles" class="content-data__expand js-display-toggle">
                &#43;${index.allResponsibleRoles?size - 1}&nbsp;more&nbsp;&hellip;</a>

                <span id="secondary-responsible-roles" class="content-data__additional">
                    <#list index.allResponsibleRoles as role>
                        <#if role?index != 0>
                            <@hst.link var="link" hippobean=role/>
                            <a href="${link}">${role.title}</a><#sep>, </#sep>
                        </#if>
                    </#list>
                </span>
            </#if>
        </dd>
    </#if>
    <#if index.allDirectorates?has_content>
        <dt class="content-data__label">Directorate:</dt>

        <dd class="content-data__value">
            <@hst.link var="link" hippobean=index.allDirectorates[0]/>
            <a href="${link}">${index.allDirectorates[0].title}</a><!--
             --><#if index.allDirectorates?size gt 1><!--
             -->, <!--
             --><a href="#secondary-responsible-directorates" class="content-data__expand js-display-toggle">
            &#43;${index.allDirectorates?size - 1}&nbsp;more&nbsp;&hellip;</a>

                <span id="secondary-responsible-directorates" class="content-data__additional">
                    <#list index.allDirectorates as directorate>
                        <#if directorate?index != 0>
                            <@hst.link var="link" hippobean=directorate/>
                            <a href="${link}">${directorate.title}</a><#sep>, </#sep>
                        </#if>
                    </#list>
                </span>
            </#if>
        </dd>
    </#if>
    <#if index.topics?has_content>
        <dt class="content-data__label">Part of:</dt>

        <dd class="content-data__value">
            <#list index.topics?sort_by("title") as topic>
                <#if topic?index lte 2>
                    <@hst.link var="link" hippobean=topic/>
                    <a href="${link}">${topic.title}</a><#sep>, </#sep>
                </#if><!--
         --></#list><!--
         --><#if index.topics?size gt 3><!--
             --><a href="#secondary-topics" class="content-data__expand js-display-toggle">
                &#43;${index.topics?size - 3}&nbsp;more&nbsp;&hellip;</a>
                <span id="secondary-topics" class="content-data__additional">
                    <#list index.topics?sort_by("title") as topic>
                        <#if topic?index gt 2>
                            <@hst.link var="link" hippobean=topic/>
                            <a href="${link}">${topic.title}</a><#sep>, </#sep>
                        </#if>
                    </#list>
                </span>
            </#if>
        </dd>
    </#if>
    <#if index.isbn?has_content>
        <dt class="content-data__label">ISBN:</dt>
        <dd class="content-data__value">${index.isbn}</dd>
    </#if>
    </dl>
</div>
