<#include "../../include/imports.ftl">

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
