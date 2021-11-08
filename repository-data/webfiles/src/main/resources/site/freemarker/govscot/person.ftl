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
                <div class="gov_person  gov_person--flex">
                    <div class="gov_person__image-container">
                        <#if document.image??>
                        <img alt="${document.title}" class="gov_person__image"
                            src="<@hst.link hippobean=document.image.xlarge/>"
                            srcset="<@hst.link hippobean=document.image.small/> 148w,
                                <@hst.link hippobean=document.image.smalldoubled/> 296w,
                                <@hst.link hippobean=document.image.medium/> 224w,
                                <@hst.link hippobean=document.image.mediumdoubled/> 448w,
                                <@hst.link hippobean=document.image.large/> 208w,
                                <@hst.link hippobean=document.image.largedoubled/> 416w,
                                <@hst.link hippobean=document.image.xlarge/> 256w,
                                <@hst.link hippobean=document.image.xlargedoubled/> 512w"
                            sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width:768px) 224px, 148px" />
                        <#else>
                        <img class="gov_person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="<#if document.incumbent??>${document.incumbent.title}<#else>${document.roleTitle}</#if>">
                        </#if>
                    </div>

                    <div class="gov_person__text-container">
                        <#assign contactInformationHeadingModifier = 'gamma' />
                        <#assign contactInformation = document.contactInformation />
                        <#include 'common/contact-information.ftl' />
                    </div>
                </div>
            </div>

            <div class="ds_layout__content">
                <div class="ds_leader-first-paragraph">
                    <@hst.html hippohtml=document.content/>
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
