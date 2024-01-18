<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#-- @ftlvariable name="archiveUrl" type="java.lang.String" -->
<#if document??>
<#assign pagetitle><#if archiveUrl??>${document.archiveTitle}<#else>${document.title}</#if></#assign>

<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  ds_layout--article">
        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">${pagetitle}</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <#if archiveUrl??>
                <@hst.html hippohtml=document.archiveContent/>
                <p>
                    <a href="${archiveUrl}">${archiveUrl}</a>
                </p>
                <@hst.html hippohtml=document.archiveContentEpilogue/>
            <#else>
                <@hst.html hippohtml=document.content/>
            </#if>
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>

<@hst.headContribution category="pageTitle">
    <title>${pagetitle} - gov.scot</title>
</@hst.headContribution>
<@hst.headContribution>
    <meta name="description" content="${document.metaDescription}"/>
</@hst.headContribution>
    
    <#include "common/metadata.social.ftl"/>
</#if>