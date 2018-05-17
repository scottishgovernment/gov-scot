<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">
        <@hst.manageContent hippobean=document/>
        <#if document.incumbent??>
            <#if document.incumbent.contactInformation??>
                <#assign contactInformation = document.incumbent.contactInformation/>
            </#if>
        </#if>
        <#if document.incumbent.postalAddress??>
            <#assign postalAddress = document.incumbent.postalAddress/>
        </#if>

        <div class="grid"><!--
            --><div class="grid__item medium--eight-twelfths">
                <header class="article-header">
                    <h1 class="article-header__title">${document.title}</h1>
                    <#if document.incumbent??>
                        <p class="article-header__subtitle">Current role holder:
                            <b class=article-header__subtitle__b>${document.incumbent.title}</b>
                        </p>
                    </#if>
                </header>
            </div><!--
        --></div>

        <div class="grid"><!--
            --><div class="grid__item medium--four-twelfths large--three-twelfths">

                <div class="person person--bordered-mobile">
                    <div class="person__image-container person__image-container--centred-mobile">
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
                    </div>
                </div>

                <div class="hidden-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>

            </div><!--
            --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

                <div class="body-content">

                    <div class="leader  leader--first-para">
                        <@hst.html hippohtml=document.incumbent.content var="biography"/>
                        ${biography?trim?keep_before("\n")}
                    </div>

                    <h2>Responsibilities</h2>
                    ${document.content.content}

                    <#if document.incumbent??>
                        <h2>Biography</h2>
                        ${biography?trim?keep_after("\n")}

                        <div class="visible-xsmall">
                            <#include 'common/contact-information.ftl' />
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
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>
