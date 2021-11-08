<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  gov_layout--featured-role">
        <@hst.manageContent hippobean=document/>
        <#assign contactInformation = document.incumbent.contactInformation/>
        <#assign postalAddress = document.incumbent.postalAddress/>

        <div class="ds_layout__header">
            <header class="gov_feature-header" id="page-content">
                <div class="gov_feature-header__content">
                    <h1 class="gov_feature-header__title">${document.title}</h1>
                    <#if document.incumbent??>
                        <p class="gov_feature-header__subtitle">Current role holder:
                            <b>${document.incumbent.title}</b>
                        </p>
                    </#if>
                </div>

                <#--  <#if document.image??>  -->
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
                <#--  </#if>  -->
            </header>
        </div>

        <div class="ds_layout__content">
            <h2 class="ds_no-margin--top">Responsibilities</h2>
            <@hst.html hippohtml=document.content />
        </div>

        <div class="ds_layout__sidebar">
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
        </div>

        <div class="ds_layout__latest">
            <section id="latest-news" class="gov_content-block">
                <h2 class="gov_content-block__title">
                    News
                </h2>

                <#if news?has_content>
                <div class="gov_latest-feed__items  ds_layout  gov_sublayout--threecols">
                        <#list news as newsItem>
                            <article class="gov_latest-feed__item">
                                <h3 class="gov_latest-feed__item__title">
                                    <a href="<@hst.link hippobean=newsItem />" data-gtm="news-${newsItem?index + 1}">${newsItem.title}</a>
                                </h3>
                                <p class="gov_latest-feed__item__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                <p>${newsItem.summary}</p>
                            </article>
                        </#list>
                    </div>
                <#else>
                    <p>There are no news items to display.</p>
                </#if>
            </section>

            <section id="latest-speeches-statements" class="gov_content-block">
                <h2 class="gov_content-block__title">
                    Speeches and statements
                </h2>

                <#if speeches?has_content>
                    <div class="gov_latest-feed__items  ds_layout  gov_sublayout--threecols">
                        <#list speeches as speech>
                            <article class="gov_latest-feed__item">
                                <h3 class="gov_latest-feed__item__title">
                                    <a href="<@hst.link hippobean=speech />" data-gtm="speech-${speech?index + 1}">${speech.title}</a>
                                </h3>
                                <p class="gov_latest-feed__item__date"><@fmt.formatDate value=speech.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                <p>${speech.summary}</p>
                            </article>
                        </#list>
                    </div>
                <#else>
                    <p>There are no speeches or statements to display.</p>
                </#if>
            </section>
        </div>

        <div class="ds_layout__featured">
            <div class="gov_feature-cards">
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
            </div>
        </div>

        <div class="ds_layout__contact">
            <div class="ds_layout  gov_sublayout--threecols">
                <#if contactInformation??>
                    <div class="ds_contact-details">
                        <dl class="ds_contact-details__list">
                            <div class="ds_contact-details__item  ds_contact-details__social">
                                <dt class="beta  ds_contact-details__title">Connect</dt>
                                <#if contactInformation.facebook?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${contactInformation.facebook}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#facebook"></use></svg>
                                        Facebook
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.twitter?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="http://twitter.com/${contactInformation.twitter}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#twitter"></use></svg>
                                        ${contactInformation.twitter}
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.flickr?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="#${contactInformation.flickr}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#flickr"></use></svg>
                                        Flickr
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.youtube?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${contactInformation.youtube}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#youtube"></use></svg>
                                        YouTube
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.blog?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${contactInformation.blog}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#blog"></use></svg>
                                        Blog
                                    </a>
                                </dd>
                                </#if>
                            </div>
                        </dl>
                    </div>
                </#if>

                <#if postalAddress?? || contactInformation.email?has_content>
                    <div class="ds_contact-details">
                        <h2 class="ds_contact-details__title">Contact</h2>
                        <dl class="ds_contact-details__list">
                            <#if postalAddress??>
                                <div class="ds_contact-details__item">
                                    <dt>Address</dt>
                                    <dd translate="no">
                                        <address>
                                            <@hst.html hippohtml=postalAddress/>
                                        </address>
                                    </dd>
                                </div>
                            </#if>

                            <#if contactInformation.email?has_content>
                                <div class="ds_contact-details__item">
                                    <dt>Email</dt>
                                    <dd><a href="mailto:${contactInformation.email}">${contactInformation.email}</a></dd>
                                </div>
                            </#if>
                        </dl>
                    </div>
                </#if>
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

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
