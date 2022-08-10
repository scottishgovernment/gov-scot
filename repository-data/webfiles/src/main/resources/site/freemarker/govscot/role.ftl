<#ftl output_format="HTML">
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
                    <h1 class="ds_page-header__title" id="sg-meta__person-role">${document.title}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.incumbent??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Current role holder</dt>
                                <dd class="ds_metadata__value" id="sg-meta__person-name">${document.incumbent.title}</dd>
                            </div>
                        </#if>
                    </dl>
                    <#include "common/metadata.ftl"/>
                </header>
            </div>

            <div class="ds_layout__sidebar">
                <#if document.incumbent??>
                    <div class="gov_person">
                        <div class="gov_person__image-container  gov_person__image-container--centered-mobile">
                            <#if document.incumbent.image??>
                            <img alt="${document.incumbent.title}" class="gov_person__image"
                                width="256"
                                height="256"
                                loading="lazy"
                                src="<@hst.link hippobean=document.incumbent.image.xlargethreecolumnssquare/>"
                                srcset="<@hst.link hippobean=document.incumbent.image.mediumfourcolumnssquare/> 224w,
                                    <@hst.link hippobean=document.incumbent.image.mediumfourcolumnsdoubledsquare/> 448w,
                                    <@hst.link hippobean=document.incumbent.image.largethreecolumnssquare/> 208w,
                                    <@hst.link hippobean=document.incumbent.image.largethreecolumnsdoubledsquare/> 416w,
                                    <@hst.link hippobean=document.incumbent.image.xlargethreecolumnssquare/> 256w,
                                    <@hst.link hippobean=document.incumbent.image.xlargethreecolumnsdoubledsquare/> 512w"
                                sizes="(min-width:1200px) 256px, (min-width:992px) 208px, 224px" />
                            <#else>
                            <img width="256" height="256" loading="lazy" class="gov_person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${document.incumbent.title}">
                            </#if>
                        </div>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__sidebar-B">
                <#if document.incumbent??>
                    <div class="gov_person">
                        <div class="gov_person__text-container">
                            <#assign contactInformationHeadingModifier = 'gamma' />
                            <#include 'common/contact-information.ftl' />
                        </div>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__content">
                <#if document.incumbent??>
                    <#if document.incumbent.content?has_content>
                        <div class="ds_leader-first-paragraph">
                            <@hst.html hippohtml=document.incumbent.content var="biography"/>
                            ${biography?trim?keep_before("\n")?no_esc}
                        </div>
                    </#if>
                </#if>

                <h2>Responsibilities</h2>

                <@hst.html hippohtml=document.content/>

                <#if document.incumbent??>
                    <#if document.incumbent.content?has_content>
                        <h2>Biography</h2>

                        ${biography?trim?keep_after("\n")?no_esc}
                    </#if>
                </#if>

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

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Role"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#if document.incumbent?? && document.incumbent.image??>
        <@hst.link var="ogimage" path='/assets/images/logos/SGLogo1200x630.png' fullyQualified=true/>
        <@hst.headContribution category="facebookMeta">
            <meta property="og:image" content="<@hst.link hippobean=document.incumbent.image.xlargedoubled fullyQualified=true/>" />
        </@hst.headContribution>
    </#if>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
