<#include "../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>

    <#assign contactInformation = document.incumbent.contactInformation/>
    <#assign postalAddress = document.incumbent.postalAddress/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--about">
            <div class="ds_layout__sidebar">
                <@hst.include ref="side-menu"/>

                <div class="hidden-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>
            </div>

            <div class="ds_layout__banner">
                <img class="full-width-image"
                    src="<@hst.link path='/assets/images/people/first_minister_desktop.jpg'/>"
                    srcset="<@hst.link path='/assets/images/people/first_minister_mob.jpg'/> 767w,
                        <@hst.link path='/assets/images/people/first_minister_desktop.jpg'/> 848w,
                        <@hst.link path='/assets/images/people/first_minister_desktop_@2x.jpg'/> 1696w"
                    alt="First Minister">
            </div>

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
                    <#include "../common/metadata.ftl"/>
                </header>
            </div>

            <div class="ds_layout__content">
                <h2>Responsibilities</h2>
                <@hst.html hippohtml=document.content />

                <#if document.incumbent??>
                    <h2>${document.incumbent.title}</h2>
                    <@hst.html var="content" hippohtml=document.incumbent.content />
                    ${content?keep_after("</p>")}

                    <div class="visible-xsmall">
                        <#include 'common/contact-information.ftl' />
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

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
