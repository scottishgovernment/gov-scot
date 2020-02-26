<#include "../include/imports.ftl">

<#-- @ftlvariable name="archiveUrl" type="java.lang.String" -->

<div class="layout--page-not-found">

    <#if document??>
        <div class="grid"><!--
        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">

            <#if archiveUrl??>
                <h1 class="article-header">${document.archiveTitle}</h1>
                <@hst.html hippohtml=document.archiveContent/>
                <p>
                    <a href="${archiveUrl}">${archiveUrl}</a>
                </p>
                <@hst.html hippohtml=document.archiveContentEpilogue/>
            <#else>
                <h1 class="article-header">${document.title}</h1>
                <@hst.html hippohtml=document.content/>
            </#if>
        </div><!--
    --></div>
    </#if>

    <div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  xlarge--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</div>

<@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
</@hst.headContribution>
<@hst.headContribution>
    <meta name="description" content="${document.metaDescription?html}"/>
</@hst.headContribution>
