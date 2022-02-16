<#include "../../include/imports.ftl">

<div class="layout--filtered-list">

<#-- @ftlvariable name="index" type="scot.gov.www.beans.SimpleContent" -->
<#if index??>
    <div class="grid" id="page-content"><!--
        --><div class="grid__item medium--eight-twelfths">
            <h1 class="article-header">${index.title?html}</h1>

            <#if index.content.content?has_content>
                <@hst.html hippohtml=index.content/>
            </#if>

        </div><!--
    --></div>
</#if>

<div class="grid"><!--
    --><div class="grid__item medium--four-twelfths large--three-twelfths">
        <@hst.include ref="side-filter"/>
    </div><!--
    --><div class="grid__item medium--eight-twelfths large--seven-twelfths">
        <@hst.include ref="results"/>
    </div><!--
--></div>

</div>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/filtered-list-page.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/filtered-list-page.es5.js"/>"></script>
</@hst.headContribution>

<#if index??>
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
    <@hst.headContribution category="pageTitle">
        <title>${index.title} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${index.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=index canonical=true/>

    <#assign canonicalitem = canonicalitem?remove_ending("index/")/>

    <#include "../common/canonical.ftl" />
</#if>
