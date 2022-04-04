<#include "../../include/imports.ftl">

<#if !metadataChildrenOnly??>
<dl class="ds_page-header__metadata  ds_metadata">
</#if>
    <#if index.allResponsibleRoles?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">From</dt>
            <dd class="ds_metadata__value">
                <@hst.link var="link" hippobean=index.allResponsibleRoles[0]/>
                <a data-navigation="roles-1" href="${link}" class="sg-meta__role">${index.allResponsibleRoles[0].title}</a><!--
                --><#if index.allResponsibleRoles?size gt 1><!--
                -->, <!--
                --><a data-navigation="roles-all" href="#secondary-responsible-roles" data-module="gov-toggle-link" data-toggled-text="" aria-controls="secondary-responsible-roles" class="gov_toggle-link">
                    &#43;${index.allResponsibleRoles?size - 1}&nbsp;more&nbsp;&hellip;</a>

                    <span id="secondary-responsible-roles" class="gov_toggle-link__target">
                        <#list index.allResponsibleRoles as role>
                            <#if role?index != 0>
                                <@hst.link var="link" hippobean=role/>
                                <a data-navigation="roles-${role?index + 2}" href="${link}" class="sg-meta__role">${role.title}</a><#sep>, </#sep>
                            </#if>
                        </#list>
                    </span>
                </#if>
            </dd>
        </div>
    </#if>
    <#if index.allDirectorates?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Directorate</dt>
            <dd class="ds_metadata__value">
                <@hst.link var="link" hippobean=index.allDirectorates[0]/>
                <a data-navigation="directorates-1" href="${link}" class="sg-meta__directorate">${index.allDirectorates[0].title}</a><!--
                --><#if index.allDirectorates?size gt 1><!--
                -->, <!--
                --><a data-navigation="directorates-all" href="#secondary-responsible-directorates" data-module="gov-toggle-link" data-toggled-text="" aria-controls="secondary-responsible-directorates" class="gov_toggle-link">
                &#43;${index.allDirectorates?size - 1}&nbsp;more&nbsp;&hellip;</a>

                    <span id="secondary-responsible-directorates" class="gov_toggle-link__target">
                        <#list index.allDirectorates as directorate>
                            <#if directorate?index != 0>
                                <@hst.link var="link" hippobean=directorate/>
                                <a data-navigation="directorates-${directorate?index + 2}" href="${link}" class="sg-meta__directorate">${directorate.title}</a><#sep>, </#sep>
                            </#if>
                        </#list>
                    </span>
                </#if>
            </dd>
        </div>
    </#if>
    <#if index.topics?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Part of</dt>
            <dd class="ds_metadata__value">
                <#list index.topics?sort_by("title") as topic>
                    <#if topic?index lte 2>
                        <@hst.link var="link" hippobean=topic/>
                        <a data-navigation="topics-${topic?index + 1}"href="${link}" class="sg-meta__topic">${topic.title}</a><#sep>, </#sep>
                    </#if><!--
            --></#list><!--
            --><#if index.topics?size gt 3><!--
                --><a data-navigation="topics-all" href="#secondary-topics" data-module="gov-toggle-link" data-toggled-text="" aria-controls="secondary-topics" class="gov_toggle-link">
                    &#43;${index.topics?size - 3}&nbsp;more&nbsp;&hellip;</a>
                    <span id="secondary-topics" class="gov_toggle-link__target">
                        <#list index.topics?sort_by("title") as topic>
                            <#if topic?index gt 2>
                                <@hst.link var="link" hippobean=topic/>
                                <a data-navigation="topics-${topic?index + 1}" href="${link}" class="sg-meta__topic">${topic.title}</a><#sep>, </#sep>
                            </#if>
                        </#list>
                    </span>
                </#if>
            </dd>
        </div>
    </#if>
    <#if index.isbn?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">ISBN</dt>
            <dd class="ds_metadata__value" id="sg-meta__isbn">${index.isbn}</dd>
        </div>
    </#if>
<#if !metadataChildrenOnly??>
</dl>
</#if>
