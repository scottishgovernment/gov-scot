<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--role">
        <@hst.manageContent hippobean=document/>
        <#if document.incumbent??>
            <#if document.incumbent.contactInformation??>
                <#assign contactInformation = document.incumbent.contactInformation/>
            </#if>
            <#if document.incumbent.postalAddress??>
                <#assign postalAddress = document.incumbent.postalAddress/>
            </#if>
        </#if>

        <div class="grid"><!--
            --><div class="grid__item medium--eight-twelfths">
                <header class="article-header">
                    <h1 class="article-header__title" id="sg-meta__person-role">${document.title}</h1>
                    <#if document.incumbent??>
                        <p class="article-header__subtitle">Current role holder:
                            <b class="article-header__subtitle__b" id="sg-meta__person-name">${document.incumbent.title}</b>
                        </p>
                    </#if>
                </header>

                <#include "common/metadata.ftl"/>
            </div><!--
        --></div>

        <div class="grid"><!--
            --><div class="grid__item medium--four-twelfths large--three-twelfths">
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

            </div><!--
            --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

                <div class="body-content">

                    <#if document.incumbent??>
                        <#if document.incumbent.content?has_content>
                        <div class="leader  leader--first-para">
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
                <!-- /end .body-content -->
            </div><!--
            --><div class="grid__item medium--three-twelfths large--two-twelfths">
                <!-- RIGHT SIDE (BLANK) -->
            </div><!--
        --></div>

    </article>

    <div class="grid"><!--
        --><div class="grid__item  push--medium--four-twelfths  push--large--three-twelfths  medium--eight-twelfths  large--seven-twelfths">
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
    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Role"/>
    </@hst.headContribution>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <#if document.incumbent.image??>
    <@hst.link var="ogimage" path='/assets/images/logos/SGLogo1200x630.png' fullyQualified=true/>
    <@hst.headContribution category="facebookMeta">
        <meta property="og:image" content="<@hst.link hippobean=document.incumbent.image.xlargedoubled fullyQualified=true/>" />
    </@hst.headContribution>
    </#if>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
