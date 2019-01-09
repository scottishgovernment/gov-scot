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
  <@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
  </@hst.headContribution>
  <@hst.headContribution>
    <meta name="description" content="${document.metaDescription?html}"/>
  </@hst.headContribution>

  <@hst.link var="canonicalitem" hippobean=document canonical=true />
  <#include "../common/canonical.ftl" />
</#if>
