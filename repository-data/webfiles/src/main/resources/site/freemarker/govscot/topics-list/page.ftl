<#include "../../include/imports.ftl">

<div class="layout--topics-list">
<div class="grid" id="page-content"><!--
  --><div class="grid__item medium--eight-twelfths">
    <h1 class="article-header">${document.title}</h1>
  </div><!--
--></div>

<#if document??>
  <div id="topics-intro">
    <@hst.html hippohtml=document.content />
  </div>
</#if>

<@hst.include ref="list"/>
</div>

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
