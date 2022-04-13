<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <h2>Who we are</h2>

                <h3>Cabinet Secretary and Ministers</h3>

                <div>
                    <ul class="gov_person-grid">
                        <#if document.orgRole??>
                            <#list document.orgRole as role>
                                <#assign person = role.incumbent />
                                <@hst.link var="link" hippobean=role/>

                                <li class="gov_person-grid__item">
                                    <div class=gov_person>
                                        <div class="gov_person__image-container">
                                            <a class="gov_person__link" href="${link}">
                                                <#if person.image?? && person.image.xlargethreecolumnssquare??>
                                                    <img alt="${person.title}" class="gov_person__image"
                                                        src="<@hst.link hippobean=person.image.xlargethreecolumnssquare/>"
                                                        srcset="<@hst.link hippobean=person.image.smalltwocolumnssquare/> 53w,
                                                                <@hst.link hippobean=person.image.smalltwocolumnsdoubledsquare/> 106w,
                                                                <@hst.link hippobean=person.image.mediumfourcolumnssquare/> 224w,
                                                                <@hst.link hippobean=person.image.mediumfourcolumnsdoubledsquare/> 448w,
                                                                <@hst.link hippobean=person.image.largethreecolumnssquare/> 208w,
                                                                <@hst.link hippobean=person.image.largethreecolumnsdoubledsquare/> 416w,
                                                                <@hst.link hippobean=person.image.xlargethreecolumnssquare/> 256w,
                                                                <@hst.link hippobean=person.image.xlargethreecolumnsdoubledsquare/> 512w"
                                                        sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width:768px) 224px, 148px" />
                                                <#else>
                                                <img class="gov_person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${role.title}">
                                                </#if>
                                            </a>
                                        </div>

                                        <div class="gov_person__text-container">

                                            <h4 class="gov_person__name">
                                                <#if role.incumbent??>
                                                    ${role.incumbent.title}
                                                <#else>
                                                    ${role.title}
                                                </#if>
                                            </h4>

                                            <p class="gov_person__roles">
                                                <#-- todo: allow for multiple here -->
                                                <#if role.incumbent??>
                                                    <a class="gov_person__role-link" href="${link}">${role.title}</a>
                                                <#else>
                                                    ${role.roleTitle}
                                                </#if>
                                            </p>
                                        </div>
                                    </div>
                                </li>
                            </#list>
                        </#if>
                    </ul>
                </div>

                <h3>Management</h3>

                <div>

                    <ul class="gov_person-grid">
                        <#if document.secondaryOrgRole??>
                            <#list document.secondaryOrgRole as role>
                                <@hst.link var="link" hippobean=role/>

                                <#if role.incumbent??>
                                    <#assign person = role.incumbent/>
                                <#else>
                                    <#assign person = role/>
                                </#if>

                                <li class="gov_person-grid__item">
                                    <div class="gov_person">
                                        <div class="gov_person__image-container">
                                            <a class="gov_person__link" href="${link}">
                                                <#if person.image?? && person.image.xlargethreecolumnssquare??>
                                                    <img alt="${person.title}" class="gov_person__image"
                                                        src="<@hst.link hippobean=person.image.xlargethreecolumnssquare/>"
                                                        srcset="<@hst.link hippobean=person.image.smalltwocolumnssquare/> 53w,
                                                                <@hst.link hippobean=person.image.smalltwocolumnsdoubledsquare/> 106w,
                                                                <@hst.link hippobean=person.image.mediumfourcolumnssquare/> 224w,
                                                                <@hst.link hippobean=person.image.mediumfourcolumnsdoubledsquare/> 448w,
                                                                <@hst.link hippobean=person.image.largethreecolumnssquare/> 208w,
                                                                <@hst.link hippobean=person.image.largethreecolumnsdoubledsquare/> 416w,
                                                                <@hst.link hippobean=person.image.xlargethreecolumnssquare/> 256w,
                                                                <@hst.link hippobean=person.image.xlargethreecolumnsdoubledsquare/> 512w"
                                                        sizes="(min-width:1200px) 256px, (min-width:992px) 208px, (min-width:768px) 224px, 148px" />
                                                <#else>
                                                    <img class="gov_person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${role.title}">
                                                </#if>
                                            </a>
                                        </div>

                                        <div class="person__text-container">
                                            <h4 class="person__name  person__name--link">${person.title}</h4>

                                            <p class="person__roles">
                                                <#-- todo: allow for multiple here -->
                                                <#if role.incumbent??>
                                                    <a class="person__role-link" href="${link}">${role.title}</a>
                                                <#else>
                                                    ${role.roleTitle}
                                                </#if>
                                            </p>
                                        </div>
                                    </div>
                                </li>
                            </#list>
                        </#if>
                    </ul>
                </div>

                <#if document.updateHistory?has_content>
                    <div class="update-history">
                        <#include 'common/update-history.ftl'/>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__sidebar">
                <!--noindex-->
                <#if document.contactInformation.twitter?has_content
                    ||   document.contactInformation.flickr?has_content
                    ||   document.contactInformation.website?has_content
                    ||   document.contactInformation.email?has_content
                    ||   document.contactInformation.facebook?has_content
                    ||   document.contactInformation.youtube?has_content
                    ||   document.contactInformation.blog?has_content>
                        <#assign contactInformation = document.contactInformation/>
                        <#assign contactInformationHeadingModifier = 'gamma' />
                    <section class="gov_content-block">
                        <#include 'common/contact-information.ftl' />
                    </section>
                </#if>

                <#if document.relatedNews?has_content>
                    <section class="gov_content-block">
                        <h3 class="gov_content-block__title">News</h3>
                        <ul class="ds_no-bullets">
                            <#list document.relatedNews as newsItem>
                                <li>
                                    <@hst.link var="link" hippobean=newsItem/>
                                    <a data-navigation="news-${newsItem?index + 1}" href="${link}">${newsItem.title}</a>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </#if>

                <#if document.relatedPublications?has_content>
                    <section class="gov_content-block">
                        <h3 class="gov_content-block__title">Publications</h3>
                        <ul class="ds_no-bullets">
                            <#list document.relatedPublications as publication>
                                <li>
                                    <@hst.link var="link" hippobean=publication/>
                                    <a data-navigation="publication-${publication?index + 1}" href="${link}">${publication.title}</a>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </#if>

                <#if policies?has_content>
                    <section class="gov_content-block">
                        <h3 class="gov_content-block__title">Policies</h3>
                        <ul class="ds_no-bullets">
                            <#list policies as policy>
                                <li>
                                    <@hst.link var="link" hippobean=policy/>
                                    <a data-navigation="policies-${policy?index + 1}" href="${link}">${policy.title}</a>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </#if>
                <!--endnoindex-->
            </div>

            <div class="ds_layout__feedback">
                <#include './common/feedback-wrapper.ftl'>
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
        <meta name="dc.format" content="Directorate"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
