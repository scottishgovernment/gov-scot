<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--role">
        <@hst.manageContent hippobean=document/>

        <div class="grid"><!--
            --><div class="grid__item  medium--eight-twelfths  large--seven-twelfths">
                <header class="article-header">
                    <h1 class="article-header__title">${document.title}</h1>
                </header>

                <@hst.html hippohtml=document.content/>

                <div class="page-nav">
                    <a title="Previous page" href="../" class="page-nav__button  page-nav__button--left">
                        <span data-label="previous" class="page-nav__text">First Minister</span>
                    </a>
                </div>
            </div><!--
            --><div class="grid__item  medium--four-twelfths  push--large--one-twelfth">
                <aside class="gov_sidebar-feature">
                    <#if document.relatedarticle.image??>
                        <img class="gov_sidebar-feature__image" alt="" aria-hidden="true"
                            src="<@hst.link hippobean=document.relatedarticle.image.largefourcolumns/>"
                            srcset="<@hst.link hippobean=document.relatedarticle.image.smallcolumns/> 360w,
                                <@hst.link hippobean=document.relatedarticle.image.smalldoubled/> 720w,
                                <@hst.link hippobean=document.relatedarticle.image.mediumfourcolumns/> 220w,
                                <@hst.link hippobean=document.relatedarticle.image.mediumfourcolumnsdoubled/> 440w,
                                <@hst.link hippobean=document.relatedarticle.image.largefourcolumns/> 294w,
                                <@hst.link hippobean=document.relatedarticle.image.largefourcolumnsdoubled/> 588w,
                                <@hst.link hippobean=document.relatedarticle.image.xlargefourcolumns/> 360w,
                                <@hst.link hippobean=document.relatedarticle.image.xlargefourcolumnsdoubled/> 720w"
                            sizes="(min-width:1200px) 360px, (min-width:920px) 294px, (min-width:768px) 220px, 360px" />
                    </#if>

                    <h2 class="emphasis  contact-information__title">${document.relatedarticle.title}</h2>

                    <@hst.html hippohtml=document.relatedarticle.content/>
                </aside>
            </div><!--
        --></div>

    </article>

    <div class="grid"><!--
        --><div class="grid__item  medium--eight-twelfths  large--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
    --></div>

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
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
