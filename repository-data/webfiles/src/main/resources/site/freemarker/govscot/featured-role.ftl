<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--featured-role">
    <@hst.manageContent hippobean=document/>
    <#assign contactInformation = document.incumbent.contactInformation/>
    <#assign postalAddress = document.incumbent.postalAddress/>

        <div class="grid"><!--
            
            --><div class="grid__item  medium--nine-twelfths   push--medium--three-twelfths">
                
                <!-- dev note: inline styles to illustrate image dimensions only. remove when we have correct images. -->
                <img class="full-width-image"
                    src="<@hst.link path='/assets/images/people/first_minister_desktop.jpg'/>"
                    srcset="<@hst.link path='/assets/images/people/first_minister_mob.jpg'/> 767w,
                        <@hst.link path='/assets/images/people/first_minister_desktop.jpg'/> 848w,
                        <@hst.link path='/assets/images/people/first_minister_desktop_@2x.jpg'/> 1696w"
                    alt="First Minister">
                
                <div class="grid">
                    <div class="grid__item large--ten-twelfths">
                        <header class="article-header">
                            <h1 class="article-header__title">${document.title}</h1>
                            <#if document.incumbent??>
                                <p class="article-header__subtitle">Current role holder:
                                    <b class=article-header__subtitle__b>${document.incumbent.title}</b>
                                </p>
                            </#if>
                        </header>
                        <div class="body-content">
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
                    </div>
                </div>
            </div><!--

            --><div class="grid__item  medium--three-twelfths  pull--medium--nine-twelfths">
                <div>
                    <@hst.include ref="side-menu"/>
                </div>

                <div class="hidden-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>


            </div><!--

        --></div>
    </article>

    <div class="grid"><!--
        --><div class="grid__item  push--medium--three-twelfths  push--large--three-twelfths  medium--eight-twelfths  large--seven-twelfths">
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
