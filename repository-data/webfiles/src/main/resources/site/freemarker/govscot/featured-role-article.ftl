<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#if document??>
<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  gov_layout--featured-role-article">
        <@hst.manageContent hippobean=document/>

        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">${document.title}</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <@hst.html hippohtml=document.content/>

            <nav class="ds_sequential-nav" aria-label="Article navigation">
                <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                    <a title="Previous section" href="../" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                        <span class="ds_sequential-nav__text" data-label="previous">
                            First Minister
                        </span>
                    </a>
                </div>
            </nav>
        </div>

        <div class="ds_layout__sidebar">
            <aside class="gov_sidebar-feature">
                <#if document.relatedarticle.image??>
                    <img alt="" aria-hidden="true" class="gov_sidebar-feature__image"
                        width="${document.relatedarticle.image.xlargefourcolumnsdoubled.width?c}"
                        height="${document.relatedarticle.image.xlargefourcolumnsdoubled.height?c}"
                        loading="lazy"
                        src="<@hst.link hippobean=document.relatedarticle.image.largefourcolumns/>"
                        srcset="<@hst.link hippobean=document.relatedarticle.image.smallcolumns/> 360w,
                            <@hst.link hippobean=document.relatedarticle.image.smallcolumnsdoubled/> 720w,
                            <@hst.link hippobean=document.relatedarticle.image.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=document.relatedarticle.image.mediumfourcolumnsdoubled/> 488w,
                            <@hst.link hippobean=document.relatedarticle.image.largefourcolumns/> 288w,
                            <@hst.link hippobean=document.relatedarticle.image.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=document.relatedarticle.image.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=document.relatedarticle.image.xlargefourcolumnsdoubled/> 708w"
                        sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width:768px) 224px, 360px" />
                </#if>

                <h2 class="emphasis  contact-information__title">${document.relatedarticle.title}</h2>

                <@hst.html hippohtml=document.relatedarticle.content/>
            </aside>
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
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

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#include "common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
</#if>
