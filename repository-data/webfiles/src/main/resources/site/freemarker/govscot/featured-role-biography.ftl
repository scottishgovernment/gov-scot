<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--role">
        <@hst.manageContent hippobean=document/>
        <#assign contactInformation = document.person.contactInformation />
        <#assign postalAddress = document.person.postalAddress />

        <div class="grid"><!--
            --><div class="grid__item  medium--eight-twelfths  large--seven-twelfths">
                <header class="article-header">
                    <h1 class="article-header__title">${document.title}</h1>
                </header>

                <@hst.html hippohtml=document.person.content/>

                <div class="page-nav">
                    <a title="Previous page" href="../" class="page-nav__button  page-nav__button--left">
                        <span data-label="previous" class="page-nav__text">First Minister</span>
                    </a>
                </div>
            </div><!--
            --><div class="grid__item  medium--four-twelfths  push--large--one-twelfth">
                <aside class="gov_sidebar-feature">
                    <#if document.image??>
                        <img class="gov_sidebar-feature__image" alt="" aria-hidden="true"
                            src="<@hst.link hippobean=document.image.largefourcolumns/>"
                            srcset="<@hst.link hippobean=document.image.smallcolumns/> 360w,
                                <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                                <@hst.link hippobean=document.image.mediumfourcolumns/> 220w,
                                <@hst.link hippobean=document.image.mediumfourcolumnsdoubled/> 440w,
                                <@hst.link hippobean=document.image.largefourcolumns/> 294w,
                                <@hst.link hippobean=document.image.largefourcolumnsdoubled/> 588w,
                                <@hst.link hippobean=document.image.xlargefourcolumns/> 360w,
                                <@hst.link hippobean=document.image.xlargefourcolumnsdoubled/> 720w"
                            sizes="(min-width:1200px) 360px, (min-width:920px) 294px, (min-width:768px) 220px, 360px" />
                    </#if>

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

                    <#if postalAddress??>
                        <div class="contact-information__group">
                            <h2 class="emphasis  contact-information__title">Contact</h2>
                            <@hst.html hippohtml=postalAddress/>
                        </div>
                    </#if>
                </aside>
            </div><!--
        --></div>

    </article>

    <div class="grid"><!--
        --><div class="grid__item  medium--eight-twelfths  large--seven-twelfths">
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
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
