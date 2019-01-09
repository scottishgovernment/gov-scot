<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--person">
        <@hst.manageContent hippobean=document/>
        <#if document.contactInformation??>
            <#assign contactInformation = document.contactInformation/>
        </#if>
        <#if document.postalAddress??>
            <#assign postalAddress = document.postalAddress/>
        </#if>

        <div class="grid"><!--
            --><div class="grid__item medium--eight-twelfths">
                <header class="article-header">
                    <h1 class="article-header__title">${document.title}</h1>

                    <#if document.roleTitle??>
                        <p class="article-header__subtitle">${document.roleTitle}</p>
                    </#if>
                </header>
            </div><!--
        --></div>

        <div class="grid"><!--
            --><div class="grid__item medium--four-twelfths large--three-twelfths">

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

            </div><!--
            --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

                <div class="body-content">

                    <div class="leader leader--first-para">
                        <@hst.html hippohtml=document.content/>
                    </div>

                    <div class="visible-xsmall">
                        <#include 'common/contact-information.ftl' />
                    </div>
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
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "common/canonical.ftl" />
</#if>
