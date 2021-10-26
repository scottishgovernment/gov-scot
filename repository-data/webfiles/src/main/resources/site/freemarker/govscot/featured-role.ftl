<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--featured-role">
        <@hst.manageContent hippobean=document/>
        <#assign contactInformation = document.incumbent.contactInformation/>
        <#assign postalAddress = document.incumbent.postalAddress/>




        <div class="grid"><!--
            --><div class="grid__item">
                <header class="gov_feature-header" id="page-content">
                    <div class="gov_feature-header__content">
                        <h1 class="gov_feature-header__title">${document.title}</h1>
                        <#if document.incumbent??>
                            <p class="gov_feature-header__subtitle">Current role holder:
                                <b>${document.incumbent.title}</b>
                            </p>
                        </#if>
                    </div>

                    <#if document.image??>
                        <div class="gov_feature-header__media">
                            <div class="ds_aspect-box">
                                <img alt="" aria-hidden="true" class="ds_aspect-box__inner"
                                    src="<@hst.link hippobean=document.image.largesixcolumns/>"
                                    srcset="<@hst.link hippobean=document.image.smallcolumns/> 130w,
                                        <@hst.link hippobean=document.image.smallcolumnsdoubled/> 260w,
                                        <@hst.link hippobean=document.image.mediumsixcolumns/> 344w,
                                        <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 688w,
                                        <@hst.link hippobean=document.image.largesixcolumns/> 454w,
                                        <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 908w,
                                        <@hst.link hippobean=document.image.xlargesixcolumns/> 554w,
                                        <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1108w"
                                    sizes="(min-width:1200px) 554px, (min-width:920px) 454px, (min-width:768px) 344px, 130px" />
                            </div>
                        </div>
                    </#if>
                </header>
            </div><!--




            --><div class="grid__item  medium--eight-twelfths" style="margin-bottom: 32px;">
                <h2 class="ds_no-margin--top">Responsibilities</h2>
                <@hst.html hippohtml=document.content />
            </div><!--




            --><div class="grid__item  medium--four-twelfths">
                <#if document.feature.title?has_content>
                    <aside class="ds_card  ds_card--grey  <#if document.feature.externallink?? || document.feature.internallink??>ds_card--hover</#if>  gov_feature-card">
                        <#if document.feature.image??>
                            <div class="ds_card__media">
                                <div class="ds_aspect-box">
                                    <img alt="" aria-hidden="true" class="ds_aspect-box__inner"
                                    src="<@hst.link hippobean=document.feature.image.largefourcolumns/>"
                                    srcset="<@hst.link hippobean=document.feature.image.smallcolumns/> 130w,
                                        <@hst.link hippobean=document.feature.image.smallcolumnsdoubled/> 260w,
                                        <@hst.link hippobean=document.feature.image.mediumfourcolumns/> 250w,
                                        <@hst.link hippobean=document.feature.image.mediumfourcolumnsdoubled/> 500w,
                                        <@hst.link hippobean=document.feature.image.largefourcolumns/> 323w,
                                        <@hst.link hippobean=document.feature.image.largefourcolumnsdoubled/> 646w,
                                        <@hst.link hippobean=document.feature.image.xlargefourcolumns/> 380w,
                                        <@hst.link hippobean=document.feature.image.xlargefourcolumnsdoubled/> 780w"
                                    sizes="(min-width:1200px) 380px, (min-width:920px) 323px, (min-width:768px) 250px, 130px" />
                                </div>
                            </div>
                        </#if>

                        <div class="ds_card__content">
                            <h2 class="ds_card__title">
                                <#if document.feature.internallink??>
                                    <a href="<@hst.link hippobean=document.feature.internallink/>" class="ds_card__link--cover">
                                        ${document.feature.title}
                                    </a>
                                <#elseif document.feature.externallink?has_content>
                                    <a href="${document.feature.externallink}" class="ds_card__link--cover">
                                        ${document.feature.title}
                                    </a>
                                <#else>
                                    ${document.feature.title}
                                </#if>
                            </h2>

                            ${document.feature.content}
                        </div>
                    </aside>

                </#if>
            </div><!--




            --><div class="grid__item">
                <section id="latest-news" class="topic-block">
                    <h2 class="emphasis  topic-block__title">
                        News
                    </h2>

                    <#if news?has_content>
                        <div class="grid"><!--
                            <#list news as newsItem>
                                --><div class="grid__item medium--four-twelfths homepage-news__item">
                                    <article class="narrow">
                                        <p class="homepage-news__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                        <h3 class="ds_no-margin--top">
                                            <a href="<@hst.link hippobean=newsItem/>" data-gtm="news-${newsItem?index + 1}">${newsItem.title}</a>
                                        </h3>
                                        <p class="homepage-news__summary">${newsItem.summary}</p>
                                    </article>
                                </div><!--
                            </#list>
                        --></div>
                    <#else>
                        <p>There are no news items to display.</p>
                    </#if>
                </section>
            </div><!--




            --><div class="grid__item">
                <section id="latest-news" class="topic-block">
                    <h2 class="emphasis  topic-block__title">
                        Speeches and statements
                    </h2>

                    <#if speeches?has_content>
                        <div class="grid"><!--
                            <#list speeches as speech>
                                --><div class="grid__item medium--four-twelfths homepage-news__item">
                                    <article class="narrow">
                                        <p class="homepage-news__date"><@fmt.formatDate value=speech.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                        <h3 class="ds_no-margin--top">
                                            <a href="<@hst.link hippobean=speech/>" data-gtm="speech-${speech?index + 1}">${speech.title}</a>
                                        </h3>
                                        <p class="homepage-news__summary">${speech.summary}</p>
                                    </article>
                                </div><!--
                            </#list>
                        --></div>
                    <#else>
                        <p>There are no speeches or statements to display.</p>
                    </#if>
                </section>
            </div><!--




            --><div class="grid__item">
                <div class="grid"><!--
                    --><div class="grid__item  gov_feature-cards">
                        <#list document.featurelist as feature>
                            <#if feature.title?has_content>
                                <div class="ds_card  ds_card--grey  <#if feature.externallink?? || feature.internallink??>ds_card--hover</#if>  gov_feature-card">
                                    <#if feature.image??>
                                        <div class="ds_card__media">
                                            <div class="ds_aspect-box">
                                                <img alt="" aria-hidden="true" class="ds_aspect-box__inner"
                                                src="<@hst.link hippobean=feature.image.largefourcolumns/>"
                                                srcset="<@hst.link hippobean=feature.image.smallcolumns/> 130w,
                                                    <@hst.link hippobean=feature.image.smallcolumnsdoubled/> 260w,
                                                    <@hst.link hippobean=feature.image.mediumfourcolumns/> 250w,
                                                    <@hst.link hippobean=feature.image.mediumfourcolumnsdoubled/> 500w,
                                                    <@hst.link hippobean=feature.image.largefourcolumns/> 323w,
                                                    <@hst.link hippobean=feature.image.largefourcolumnsdoubled/> 646w,
                                                    <@hst.link hippobean=feature.image.xlargefourcolumns/> 380w,
                                                    <@hst.link hippobean=feature.image.xlargefourcolumnsdoubled/> 780w"
                                                sizes="(min-width:1200px) 380px, (min-width:920px) 323px, (min-width:768px) 250px, 130px" />
                                            </div>
                                        </div>
                                    </#if>

                                    <div class="ds_card__content">
                                        <h2 class="ds_card__title">
                                            <#if feature.internallink??>
                                                <a href="<@hst.link hippobean=feature.internallink/>" class="ds_card__link--cover">
                                                    ${feature.title}
                                                </a>
                                            <#elseif feature.externallink?has_content>
                                                <a href="${feature.externallink}" class="ds_card__link--cover">
                                                    ${feature.title}
                                                </a>
                                            <#else>
                                                ${feature.title}
                                            </#if>
                                        </h2>

                                        ${feature.content}
                                    </div>
                                </div>
                            </#if>
                        </#list>
                    </div><!--
                --></div>
            </div><!--




            --><div class="grid__item  medium--four-twelfths">
                <#if contactInformation??>
                    <div class="contact-information__group">
                        <h2 class="emphasis  contact-information__title">Connect</h2>
                        <ul class="external-links">
                            <#if contactInformation.twitter?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="http://twitter.com/${contactInformation.twitter}"><span class="external-links__icon fa fa-twitter"></span>${contactInformation.twitter}</a>
                                </li>
                            </#if>
                            <#if contactInformation.flickr?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="${contactInformation.flickr}"><span class="external-links__icon fa fa-flickr"></span>Flickr images</a>
                                </li>
                            </#if>
                            <#if contactInformation.website?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="${contactInformation.website}"><span class="external-links__icon fa fa-link"></span>Website</a>
                                </li>
                            </#if>
                            <#if contactInformation.email?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="mailto:${contactInformation.email}"><span class="external-links__icon fa fa-envelope"></span>Email</a>
                                </li>
                            </#if>
                            <#if contactInformation.facebook?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="${contactInformation.facebook}"><span class="external-links__icon fa fa-facebook-square"></span>Facebook</a>
                                </li>
                            </#if>
                            <#if contactInformation.youtube?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="${contactInformation.youtube}"><span class="external-links__icon fa fa-youtube-square"></span>Youtube</a>
                                </li>
                            </#if>
                            <#if contactInformation.blog?has_content>
                                <li class="external-links__item">
                                    <a class="external-links__link" href="${contactInformation.blog}"><span class="external-links__icon fa fa-comment"></span>Blog</a>
                                </li>
                            </#if>
                        </ul>
                    </div>
                </#if>
            </div><!--
            --><div class="grid__item  medium--four-twelfths">
                <#if postalAddress??>
                    <div class="contact-information__group">
                        <h2 class="emphasis  contact-information__title">Contact</h2>
                        <@hst.html hippohtml=postalAddress/>
                    </div>
                </#if>
            </div><!--
        --></div>

    </article>

    <div class="grid"><!--
        --><div class="grid__item  medium--eight-twelfths">
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
