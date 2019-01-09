<#include "../include/imports.ftl">

<div class="layout--page-not-found">

    <#if document??>
        <div class="grid"><!--
        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
            <h1 class="article-header">${document.title}</h1>
            <@hst.html hippohtml=document.content/>
            </div><!--
        --></div>
    </#if>

</div>

<@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
</@hst.headContribution>
<@hst.headContribution>
    <meta name="description" content="${document.metaDescription?html}"/>
</@hst.headContribution>
