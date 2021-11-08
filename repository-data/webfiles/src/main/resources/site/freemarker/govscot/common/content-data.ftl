<#include "../../include/imports.ftl">

<#if !metadataChildrenOnly??>
<dl class="ds_page-header__metadata  ds_metadata">
</#if>
    <#if index.allResponsibleRoles?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">From</dt>
            <dd class="ds_metadata__value">
                <@hst.link var="link" hippobean=index.allResponsibleRoles[0]/>
                <a href="${link}">${index.allResponsibleRoles[0].title}</a><!--
                --><#if index.allResponsibleRoles?size gt 1><!--
                -->, <!--
                --><a href="#secondary-responsible-roles" class="content-data__expand  js-display-toggle">
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
        </div>
    </#if>
    <#if index.allDirectorates?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Directorate</dt>
            <dd class="ds_metadata__value">
                <@hst.link var="link" hippobean=index.allDirectorates[0]/>
                <a href="${link}">${index.allDirectorates[0].title}</a><!--
                --><#if index.allDirectorates?size gt 1><!--
                -->, <!--
                --><a href="#secondary-responsible-directorates" class="content-data__expand  js-display-toggle">
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
        </div>
    </#if>
    <#if index.topics?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Part of</dt>
            <dd class="ds_metadata__value">
                <#list index.topics?sort_by("title") as topic>
                    <#if topic?index lte 2>
                        <@hst.link var="link" hippobean=topic/>
                        <a href="${link}">${topic.title}</a><#sep>, </#sep>
                    </#if><!--
            --></#list><!--
            --><#if index.topics?size gt 3><!--
                --><a href="#secondary-topics" class="content-data__expand  js-display-toggle">
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
        </div>
    </#if>
    <#if index.isbn?has_content>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">ISBN</dt>
            <dd class="ds_metadata__value">${index.isbn}</dd>
        </div>
    </#if>
<#if !metadataChildrenOnly??>
</dl>
</#if>
