<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--featured-role">
    <@hst.manageContent hippobean=document/>
    <#assign contactInformation = document.incumbent.contactInformation/>
    <#assign postalAddress = document.incumbent.postalAddress/>

        <div class="grid"><!--
            --><div class="grid__item medium--three-twelfths large--three-twelfths">
                <@hst.include ref="side-menu"/>
            </div><!--

            --><div class="grid__item  medium--nine-twelfths large--seven-twelfths">

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
                </div><!--
                --><div class="grid"><!--

                    --><section id="latest-news" class="topic-block">
                        <h2 class="emphasis  topic-block__title">
                            News
                        </h2>

                        <div id="news-container">
                            <#if news?has_content>
                                <#list news as newsItem>
                                    <article class="homepage-publication">
                                    <h3 class="js-truncate  homepage-publication__title">
                                        <a href="<@hst.link hippobean=newsItem />" data-gtm="news-${newsItem?index + 1}" title="${newsItem.title}">${newsItem.title}</a>
                                    </h3>
                                    <p class="homepage-publication__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                    </article>
                                </#list>
                            <#else>
                                <p>There are no news items to display.</p>
                            </#if>
                        </div>
                    </section>
                    <section id="latest-news" class="topic-block">
                        <h2 class="emphasis  topic-block__title">
                            Speeches and statements
                        </h2>

                        <div id="news-container">
                            <#if speeches?has_content>
                                <#list speeches as speech>
                                    <article class="homepage-publication">
                                    <h3 class="js-truncate  homepage-publication__title">
                                        <a href="<@hst.link hippobean=speech />" data-gtm="news-${speech?index + 1}" title="${speech.title}">${speech.title}</a>
                                    </h3>
                                    <p class="homepage-publication__date"><@fmt.formatDate value=speech.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                    </article>
                                </#list>
                            <#else>
                                <p>There are no speeches or statements to display.</p>
                            </#if>
                        </div>
                    </section>
                </div><!--
            --></div><!--

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

    <#include "common/gtm-datalayer.ftl"/>
</#if>
