<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  gov_layout--featured-role-article">
        <@hst.manageContent hippobean=document/>
        <#assign contactInformation = document.person.contactInformation />
        <#assign postalAddress = document.person.postalAddress />

        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">${document.title}</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <@hst.html hippohtml=document.person.content/>

            <nav class="ds_sequential-nav" aria-label="Article navigation">
                <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                    <a href="../" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                        <span class="ds_sequential-nav__text" data-label="previous">
                            First Minister
                        </span>
                    </a>
                </div>
            </nav>
        </div>

        <div class="ds_layout__sidebar">
            <aside class="gov_sidebar-feature">
                <#if document.image??>
                    <img class="gov_sidebar-feature__image" alt="" aria-hidden="true"
                        width="${document.image.xlargefourcolumnsdoubled.width?c}"
                        height="${document.image.xlargefourcolumnsdoubled.height?c}"
                        loading="lazy"
                        src="<@hst.link hippobean=document.image.largefourcolumns/>"
                        srcset="<@hst.link hippobean=document.image.smallcolumns/> 360w,
                            <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                            <@hst.link hippobean=document.image.mediumfourcolumns/> 224w,
                            <@hst.link hippobean=document.image.mediumfourcolumnsdoubled/> 448w,
                            <@hst.link hippobean=document.image.largefourcolumns/> 288w,
                            <@hst.link hippobean=document.image.largefourcolumnsdoubled/> 576w,
                            <@hst.link hippobean=document.image.xlargefourcolumns/> 352w,
                            <@hst.link hippobean=document.image.xlargefourcolumnsdoubled/> 704w"
                        sizes="(min-width:1200px) 352px, (min-width:920px) 288px, (min-width:768px) 224px, 360px" />
                </#if>

                <#if contactInformation??>
                    <div class="ds_contact-details">
                        <h2 class="ds_contact-details__title">Contact</h2>

                        <dl class="ds_contact-details__list">
                            <@hst.html var="htmladdress" hippohtml=postalAddress/>
                            <#if htmladdress?has_content>
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

                            <div class="ds_contact-details__item  ds_contact-details__social">
                                <dt class="visually-hidden  ds_contact-details__title">Social media</dt>
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
                                        <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#x"></use></svg>
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
                    </div>
                </#if>
            </aside>
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

    <#include "common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>
