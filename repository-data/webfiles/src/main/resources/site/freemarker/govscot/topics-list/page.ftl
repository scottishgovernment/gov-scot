<#include "../../include/imports.ftl">

<#if document??>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <div id="topics-intro">
                    <@hst.html hippohtml=document.content />
                </div>

                <@hst.include ref="list"/>
            </div>
        </main>
    </div>
</#if>

<#if document??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <#if document.tags??>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.subject" content="<#list document.tags as tag>${tag}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Topic"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />
</#if>
