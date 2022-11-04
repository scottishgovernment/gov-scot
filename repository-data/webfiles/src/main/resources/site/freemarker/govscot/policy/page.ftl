<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if document??>
<div class="ds_wrapper">
    <main <@lang index/> id="main-content" class="ds_layout  gov_layout--policy">
        <div class="ds_layout__header">
            <@hst.link var="link" hippobean=index />
            <header class="ds_page-header">
                <span <@revertlang index /> class="ds_page-header__label  ds_content-label">Policy<#if latest??> - Latest</#if></span>
                <h1 class="ds_page-header__title">${index.title}</h1>
                <#include '../common/content-metadata.ftl'/>
            </header>
        </div>

        <div class="ds_layout__sidebar">
            <!--noindex-->
            <#include 'side-menu.ftl'>
            <!--endnoindex-->
        </div>

        <div class="ds_layout__content">
            <#if latest??>
                <#include 'latest.ftl'/>
            <#else>
                <#include 'content.ftl'/>
            </#if>
        </div>

        <div <@revertlang index />class="ds_layout__feedback">
            <#include '../common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

<#if document??>

    <#if title??>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.title" content="${title}"/>
        </@hst.headContribution>
        <@hst.headContribution category="pageTitle">
            <title>${title} <#if parent??>- ${parent.title} </#if>- gov.scot</title>
        </@hst.headContribution>
    <#else>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.title" content="${document.title}"/>
        </@hst.headContribution>
        <@hst.headContribution category="pageTitle">
            <title>${document.title}  <#if parent??>- ${parent.title} </#if>- gov.scot</title>
        </@hst.headContribution>
    </#if>

    <#if parent??>
        <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series" content="${parent.title}"/>
        </@hst.headContribution>
        <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series.link" content="<@hst.link hippobean=parent/>"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <#if document.tags??>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.subject" content="<#list document.tags as tag>${tag}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Policy"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />

    <#assign uuid = document.getSingleProperty('jcr:uuid')/>
    <#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/>
    <#assign dateCreated = document.getSingleProperty('hippostdpubwf:creationDate')/>
    <#assign document = index/>
    <#include "../common/gtm-datalayer.ftl"/>
</#if>
