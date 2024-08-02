<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  gov_layout--featured-role">
        <@hst.manageContent hippobean=document/>
        <#assign contactInformation = document.incumbent.contactInformation/>
        <#assign postalAddress = document.incumbent.postalAddress/>

        <div class="ds_layout__header">
            <header class="ds_feature-header  ds_feature-header--background" id="page-content">
                <div class="ds_feature-header__primary">
                    <h1 class="ds_feature-header__title" id="sg-meta__person-role">${document.title}</h1>
                    <#if document.incumbent??>
                        <p class="ds_feature-header__subtitle">Current role holder:
                            <b id="sg-meta__person-name">${document.incumbent.title}</b>
                        </p>
                    </#if>
                </div>

                <#--  <#if document.image??>  -->
                    <div class="ds_feature-header__secondary">
                        <div class="ds_aspect-box">
                            <img alt="" aria-hidden="true" class="ds_aspect-box__inner"
                                width="${document.image.xlargesixcolumnsdoubled.width?c}"
                                height="${document.image.xlargesixcolumnsdoubled.height?c}"
                                loading="lazy"
                                src="<@hst.link hippobean=document.image.largesixcolumns/>"
                                srcset="<@hst.link hippobean=document.image.smallcolumns/> 360w,
                                    <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                                    <@hst.link hippobean=document.image.mediumsixcolumns/> 352w,
                                    <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 708w,
                                    <@hst.link hippobean=document.image.largesixcolumns/> 448w,
                                    <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 896w,
                                    <@hst.link hippobean=document.image.xlargesixcolumns/> 544w,
                                    <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1088w"
                                sizes="(min-width:1200px) 544px, (min-width:920px) 448px, (min-width:768px) 352px, 360px" />
                        </div>
                    </div>
                <#--  </#if>  -->
            </header>
        </div>

        <div class="ds_layout__content">
            <div class="ds_!_margin-bottom--9">
                <h2 class="ds_no-margin--top">Responsibilities</h2>
                <@hst.html hippohtml=document.content />
            </div>
        </div>

        <div class="ds_layout__sidebar">
            <#if document.feature.title?has_content>
                <aside class="ds_card  ds_card--grey  <#if document.feature.externallink?? || document.feature.internallink??>ds_card--hover</#if>  gov_feature-card">
                    <#if document.feature.image??>
                        <div class="ds_card__media">
                            <div class="ds_aspect-box">
                                <img alt="" aria-hidden="true" class="ds_aspect-box__inner"
                                src="<@hst.link hippobean=document.feature.image.largefourcolumns/>"
                                srcset="<@hst.link hippobean=document.feature.image.smallcolumns/> 360w,
                                    <@hst.link hippobean=document.feature.image.smallcolumnsdoubled/> 720w,
                                    <@hst.link hippobean=document.feature.image.mediumfourcolumns/> 224w,
                                    <@hst.link hippobean=document.feature.image.mediumfourcolumnsdoubled/> 448w,
                                    <@hst.link hippobean=document.feature.image.largefourcolumns/> 288w,
                                    <@hst.link hippobean=document.feature.image.largefourcolumnsdoubled/> 576w,
                                    <@hst.link hippobean=document.feature.image.xlargefourcolumns/> 352w,
                                    <@hst.link hippobean=document.feature.image.xlargefourcolumnsdoubled/> 704w"
                                sizes="(min-width:1200px) 352px, (min-width:920px) 288px, (min-width:768px) 224px, 360px" />
                            </div>
                        </div>
                    </#if>

                    <div class="ds_card__content">
                        <h2 class="ds_card__title">
                            <#if document.feature.internallink??>
                                <a data-navigation="feature-main" href="<@hst.link hippobean=document.feature.internallink/>" class="ds_card__link--cover">
                                    ${document.feature.title}
                                </a>
                            <#elseif document.feature.externallink?has_content>
                                <a data-navigation="feature-main" href="${document.feature.externallink}" class="ds_card__link--cover">
                                    ${document.feature.title}
                                </a>
                            <#else>
                                ${document.feature.title}
                            </#if>
                        </h2>

                        ${document.feature.content?no_esc}
                    </div>
                </aside>

            </#if>
        </div>

        <div class="ds_layout__latest">
            <!--noindex-->
            <section id="latest-news" class="gov_content-block">
                <h2 class="gov_content-block__title">
                    News
                </h2>

                <#if news?has_content>
                    <div class="gov_latest-feed__items  ds_layout  gov_sublayout--threecols">
                        <#list news as newsItem>
                            <article class="gov_latest-feed__item">
                                <p class="gov_latest-feed__item__date  gov_latest-feed__item__date--bar"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></p>
                                <h3 class="gov_latest-feed__item__title">
                                    <a href="<@hst.link hippobean=newsItem />" data-navigation="news-${newsItem?index + 1}">${newsItem.title}</a>
                                </h3>
                                <p class="gov_latest-feed__item__summary">${newsItem.summary}</p>
                            </article>
                        </#list>
                    </div>
                <#else>
                    <p>There are no news items to display.</p>
                </#if>
            </section>
            <!--endnoindex-->

            <!--noindex-->
            <section id="latest-speeches-statements" class="gov_content-block">
                <h2 class="gov_content-block__title">
                    Speeches and statements
                </h2>

                <#if speeches?has_content>
                    <div class="gov_latest-feed__items  ds_layout  gov_sublayout--threecols">
                        <#list speeches as speech>
                            <article class="gov_latest-feed__item">
                                <p class="gov_latest-feed__item__date  gov_latest-feed__item__date--bar"><@fmt.formatDate value=speech.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></p>
                                <h3 class="gov_latest-feed__item__title">
                                    <a href="<@hst.link hippobean=speech />" data-navigation="speech-${speech?index + 1}">${speech.title}</a>
                                </h3>
                                <p class="gov_latest-feed__item__summary">${speech.summary}</p>
                            </article>
                        </#list>
                    </div>
                <#else>
                    <p>There are no speeches or statements to display.</p>
                </#if>
            </section>
            <!--endnoindex-->
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
                                        srcset="<@hst.link hippobean=feature.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=feature.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=feature.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=feature.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=feature.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=feature.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=feature.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=feature.image.xlargefourcolumnsdoubled/> 704w"
                                        sizes="(min-width:1200px) 352px, (min-width:920px) 288px, (min-width:768px) 224px, 360px" />
                                    </div>
                                </div>
                            </#if>

                            <div class="ds_card__content">
                                <h2 class="ds_card__title">
                                    <#if feature.internallink??>
                                        <a data-navigation="featured-item-${feature?index + 1}"  href="<@hst.link hippobean=feature.internallink/>" class="ds_card__link--cover">
                                            ${feature.title}
                                        </a>
                                    <#elseif feature.externallink?has_content>
                                        <a data-navigation="featured-item-${feature?index + 1}"  href="${feature.externallink}" class="ds_card__link--cover">
                                            ${feature.title}
                                        </a>
                                    <#else>
                                        ${feature.title}
                                    </#if>
                                </h2>

                                ${feature.content?no_esc}
                            </div>
                        </div>
                    </#if>
                </#list>
            </div>
        </div>

        <div class="ds_layout__contact">
            <h2 class="ds_contact-details__title">Contact</h2>

            <div class="ds_contact-details">
                <div class="ds_layout  gov_sublayout--threecols">
                    <@hst.html var="htmladdress" hippohtml=postalAddress/>
                    <#if htmladdress?has_content>
                    <dl class="ds_contact-details__list">
                        <div class="ds_contact-details__item">
                            <dt>Address</dt>
                            <dd translate="no">
                                <address>
                                    <@hst.html hippohtml=postalAddress/>
                                </address>
                            </dd>
                        </div>
                    </dl>
                    </#if>

                    <#if contactInformation??>
                        <dl class="ds_contact-details__list">

                            <div class="ds_contact-details__item  ds_contact-details__social">
                                <dt class="visually-hidden">Email and social media</dt>

                                <#if contactInformation.email?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="mailto:${contactInformation.email}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#email"></use></svg>
                                        <span class="visually-hidden">Email: </span>${contactInformation.email}
                                    </a>
                                </dd>
                                </#if>

                                <#if contactInformation.facebook?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${contactInformation.facebook}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#facebook"></use></svg>
                                        Facebook
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.twitter?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="http://x.com/${contactInformation.twitter}">
                                        <svg class="ds_contact-details__social-icon  ds_icon  ds_icon--20" aria-hidden="true" role="img"><use href="${iconspath}#x"></use></svg>
                                        ${contactInformation.twitter}
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.flickr?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="#${contactInformation.flickr}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#flickr"></use></svg>
                                        Flickr
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.youtube?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${contactInformation.youtube}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#youtube"></use></svg>
                                        YouTube
                                    </a>
                                </dd>
                                </#if>
                                <#if contactInformation.blog?has_content>
                                <dd class="ds_contact-details__social-item">
                                    <a class="ds_contact-details__social-link" href="${contactInformation.blog}">
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#blog"></use></svg>
                                        Blog
                                    </a>
                                </dd>
                                </#if>
                            </div>
                        </dl>
                    </#if>
                </div>
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
        <meta name="dc.format" content="Featured role"/>
    </@hst.headContribution>

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#if document.incumbent.image??>
        <@hst.link var="imagelink" hippobean=document.incumbent.image.xlargethreecolumnsdoubledsquare fullyQualified=true/>
    </#if>

    <#include "common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>
