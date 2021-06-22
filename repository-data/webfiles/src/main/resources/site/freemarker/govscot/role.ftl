<#include "../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>

    <#if document.incumbent??>
        <#if document.incumbent.contactInformation??>
            <#assign contactInformation = document.incumbent.contactInformation/>
        </#if>
        <#if document.incumbent.postalAddress??>
            <#assign postalAddress = document.incumbent.postalAddress/>
        </#if>
    </#if>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--role">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.incumbent??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Current role holder</dt>
                                <dd class="ds_metadata__value">${document.incumbent.title}</dd>
                            </div>
                        </#if>
                    </dl>
                    <#include "common/metadata.ftl"/>
                </header>
            </div>

            <div class="ds_layout__sidebar">
                <#if document.incumbent??>
                    <div class="person person--bordered-mobile">
                        <div class="person__image-container person__image-container--centred-mobile">
                            <#if document.incumbent.image??>
                            <img alt="${document.incumbent.title}" class="person__image"
                                src="<@hst.link hippobean=document.incumbent.image.xlarge/>"
                                srcset="<@hst.link hippobean=document.incumbent.image.small/> 130w,
                                    <@hst.link hippobean=document.incumbent.image.smalldoubled/> 260w,
                                    <@hst.link hippobean=document.incumbent.image.medium/> 220w,
                                    <@hst.link hippobean=document.incumbent.image.mediumdoubled/> 440w,
                                    <@hst.link hippobean=document.incumbent.image.large/> 213w,
                                    <@hst.link hippobean=document.incumbent.image.largedoubled/> 426w,
                                    <@hst.link hippobean=document.incumbent.image.xlarge/> 263w,
                                    <@hst.link hippobean=document.incumbent.image.xlargedoubled/> 526w"
                                sizes="(min-width:1200px) 263px, (min-width:920px) 213px, (min-width:768px) 220px, 130px" />
                            <#else>
                            <img class="person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${document.incumbent.title}">
                            </#if>
                        </div>
                    </div>

                    <div class="hidden-xsmall">
                        <#include 'common/contact-information.ftl' />
                    </div>
                </#if>
            </div>

            <div class="ds_layout__content">
                <#if document.incumbent??>
                    <#if document.incumbent.content?has_content>
                        <div>
                            <@hst.html hippohtml=document.incumbent.content var="biography"/>
                            ${biography?trim?keep_before("\n")}
                        </div>
                    </#if>
                </#if>

                <h2>Responsibilities</h2>

                <@hst.html hippohtml=document.content/>

                <#if document.incumbent??>
                    <#if document.incumbent.content?has_content>
                        <h2>Biography</h2>

                        ${biography?trim?keep_after("\n")}
                    </#if>
                </#if>


                <div class="visible-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>

                <#if document.updateHistory?has_content>
                    <div class="update-history">
                        <#include 'common/update-history.ftl'/>
                    </div>
                </#if>
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
