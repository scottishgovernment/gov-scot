<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout-featured-role">
    <@hst.manageContent hippobean=document/>
    <#assign contactInformation = document.incumbent.contactInformation/>
    <#assign postalAddress = document.postalAddress/>

        <div class="grid"><!--
            --><div class="grid__item
            medium--three-twelfths large--three-twelfths">
                <div>
                    <@hst.include ref="side-menu"/>
                </div>

                <div class="hidden-xsmall">
                    <#include 'common/contact-information.ftl' />
                </div>


            </div><!--
            --><div class="grid__item medium--nine-twelfths large--nine-twelfths">
                
                <!-- dev note: inline styles to illustrate image dimensions only. remove when we have correct images. -->
                <img class="full-width-image"
                    src=""
                    alt="First Minister"
                    style="padding-bottom: 42.5531914893617%; height: 0; background: #e5f0f8;">
                
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
                            ${document.content.content}

                            <#if document.incumbent??>
                                <h2>Biography</h2>
                                ${document.incumbent.content.content?keep_after("\n")}

                                <div class="visible-xsmall">
                                    <#include 'common/contact-information.ftl' />
                                </div>
                            </#if>
                        </div>
                    </div>
                </div>
            </div><!--

        --></div>
    </article>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>
