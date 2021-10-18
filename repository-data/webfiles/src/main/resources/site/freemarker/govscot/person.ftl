<#include "../include/imports.ftl">

<#if document??>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--role">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.roleTitle??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Role</dt>
                                <dd class="ds_metadata__value">${document.roleTitle}</dd>
                            </div>
                        </#if>
                    </dl>
                </header>
            </div>

            <div class="ds_layout__sidebar">
                <div class="person person--bordered-mobile">
                    <div class="person__image-container person__image-container--centred-mobile">
                        <#if document.image??>
                        <img alt="${document.title}" class="person__image"
                            src="<@hst.link hippobean=document.image.xlarge/>"
                            srcset="<@hst.link hippobean=document.image.small/> 130w,
                                <@hst.link hippobean=document.image.smalldoubled/> 260w,
                                <@hst.link hippobean=document.image.medium/> 220w,
                                <@hst.link hippobean=document.image.mediumdoubled/> 440w,
                                <@hst.link hippobean=document.image.large/> 213w,
                                <@hst.link hippobean=document.image.largedoubled/> 426w,
                                <@hst.link hippobean=document.image.xlarge/> 263w,
                                <@hst.link hippobean=document.image.xlargedoubled/> 526w"
                            sizes="(min-width:1200px) 263px, (min-width:920px) 213px, (min-width:768px) 220px, 130px"/>
                        <#else>
                        <img class="person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="<#if document.incumbent??>${document.incumbent.title}<#else>${document.roleTitle}</#if>">
                        </#if>
                    </div>
                </div>

                <div class="hidden-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>
            </div>

            <div class="ds_layout__content">
                <div class="ds_leader-first-paragraph">
                    <@hst.html hippohtml=document.content/>
                </div>

                <div class="visible-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>
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
