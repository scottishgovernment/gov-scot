<#include "../../include/imports.ftl">

<div class="content-data__expandable">
    <button class="js-expand  expand  expand--mobile-only  content-data__toggle" data-target-selector="#expandable-content-data" title="Show details">
        <span class="hit-target">
            <span class="expand__icon"></span>
        </span>
    </button>
    <dl class="content-data__list" id="expandable-content-data">
    <#if index.responsibleRole?has_content>
        <dt class="content-data__label">From:</dt>

        <dd class="content-data__value">
            <@hst.link var="link" hippobean=index.responsibleRole/>
            <a href="${link}">${index.responsibleRole.title}</a><!--

         --><#if index.secondaryResponsibleRole?first??><!--
         -->, <!--
             --><a href="#secondary-responsible-roles" class="content-data__expand js-display-toggle">
                &#43;${index.secondaryResponsibleRole?size}&nbsp;more&nbsp;&hellip;</a>

                <span id="secondary-responsible-roles" class="content-data__additional">
                    <#list index.secondaryResponsibleRole as secondaryRole>

                        <@hst.link var="link" hippobean=secondaryRole/>
                        <a href="${link}">${secondaryRole.title}</a><#sep>, </#sep>
                    </#list>
                </span>

        </#if>
        </dd>
    </#if>
    <#if index.responsibleDirectorate?has_content>
        <dt class="content-data__label">Directorate:</dt>

        <dd class="content-data__value">
            <@hst.link var="link" hippobean=index.responsibleDirectorate/>
            <a href="${link}">${index.responsibleDirectorate.title}</a><!--
             --><#if index.secondaryResponsibleDirectorate?has_content><!--
             -->, <!--
             --><a href="#secondary-responsible-directorates" class="content-data__expand js-display-toggle">
            &#43;${index.secondaryResponsibleDirectorate?size}&nbsp;more&nbsp;&hellip;</a>

                <span id="secondary-responsible-directorates" class="content-data__additional">
                    <#list index.secondaryResponsibleDirectorate as secondaryDirectorate>

                        <@hst.link var="link" hippobean=secondaryDirectorate/>
                        <a href="${link}">${secondaryDirectorate.title}</a><#sep>, </#sep>
                    </#list>
                </span>
            </#if>
        </dd>
    </#if>
    <#if index.topics?first??>
        <dt class="content-data__label">Part of:</dt>

        <dd class="content-data__value">
            <#list index.topics?sort_by("title") as topic>
                <@hst.link var="link" hippobean=topic/>
                <a href="${link}">${topic.title}</a><#sep>, </sep>
            </#list>
        </dd>
    </#if>
    <#if index.isbn?has_content>
        <dt class="content-data__label">ISBN:</dt>
        <dd class="content-data__value">${index.isbn}</dd>
    </#if>
    </dl>
</div>
