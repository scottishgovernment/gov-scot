<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#if document??>

<@hst.manageContent hippobean=document/>

<div class="grid layout--directorate" id="page-content"><!--
     --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

    <article>

        <h1 class="article-header">
            ${document.title}
        </h1>

        <#include "common/metadata.ftl"/>

        <@hst.html hippohtml=document.content/>

        <h2>Who we are</h2>

        <h3>Cabinet Secretary and Ministers</h3>

        <div class="overflow--large--one-twelfth overflow--xlarge--one-twelfth">
            <ul class="grid"><!--

                <#if document.orgRole??>
                    <#list document.orgRole as role>
                     --><li class="grid__item medium--four-twelfths large--three-twelfths no-left-margin">
                            <div class="person">
                                <@hst.link var="link" hippobean=role/>
                                <a class="person__link" href="${link}">

                                    <div class="person__image-container">
                                    <#if role.incumbent?has_content && role.incumbent.image??>
                                        <img alt="${role.incumbent.title}" class="person__image"
                                    src="<@hst.link hippobean=role.incumbent.image.xlarge/>"
                                    srcset="<@hst.link hippobean=role.incumbent.image.small/> 130w,
                                        <@hst.link hippobean=role.incumbent.image.smalldoubled/> 260w,
                                        <@hst.link hippobean=role.incumbent.image.medium/> 220w,
                                        <@hst.link hippobean=role.incumbent.image.mediumdoubled/> 440w,
                                        <@hst.link hippobean=role.incumbent.image.large/> 213w,
                                        <@hst.link hippobean=role.incumbent.image.largedoubled/> 426w,
                                        <@hst.link hippobean=role.incumbent.image.xlarge/> 263w,
                                        <@hst.link hippobean=role.incumbent.image.xlargedoubled/> 526w"
                                    sizes="(min-width:1200px) 213px, (min-width:920px) 130px, (min-width:768px) 213px, (min-width:480px) 213px, 130px">
                                    <#else>
                                    <img class="person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${role.title}">
                                    </#if>
                                    </div>

                                    <div class="person__text-container">
                                        <h4 class="person__name person__name--link"><#if role.incumbent??>${role.incumbent.title}<#else>${role.title}</#if></h4>
                                    </div>
                                </a>

                                <p class="person__roles">
                                    <#-- todo: allow for multiple here -->
                                    <a class="person__role-link" href="${link}">${role.title}</a>
                                </p>

                            </div>
                        </li><!--
                    </#list>
                </#if>
         --></ul>
        </div>

        <h3>Management</h3>

        <div class="overflow--large--one-twelfth overflow--xlarge--one-twelfth">
            <ul class="grid"><!--
                <#if document.secondaryOrgRole??>
                    <#list document.secondaryOrgRole as role>

                     --><li class="grid__item medium--four-twelfths large--three-twelfths no-left-margin">
                            <div class="person">
                                <@hst.link var="link" hippobean=role/>
                                <a class="person__link" href="${link}">
                                    <div class="person__image-container">
                                        <#if role.incumbent??>
                                            <#assign roleperson = role.incumbent/>
                                        <#else>
                                            <#assign roleperson = role/>
                                        </#if>

                                        <#if roleperson.image??>
                                        <img alt="${roleperson.title}" class="person__image"
                                        src="<@hst.link hippobean=roleperson.image.xlarge/>"
                                        srcset="<@hst.link hippobean=roleperson.image.small/> 130w,
                                            <@hst.link hippobean=roleperson.image.smalldoubled/> 260w,
                                            <@hst.link hippobean=roleperson.image.medium/> 220w,
                                            <@hst.link hippobean=roleperson.image.mediumdoubled/> 440w,
                                            <@hst.link hippobean=roleperson.image.large/> 213w,
                                            <@hst.link hippobean=roleperson.image.largedoubled/> 426w,
                                            <@hst.link hippobean=roleperson.image.xlarge/> 263w,
                                            <@hst.link hippobean=roleperson.image.xlargedoubled/> 526w"
                                        sizes="(min-width:1200px) 213px, (min-width:920px) 130px, (min-width:768px) 213px, 130px">
                                        <#else>
                                            <img class="person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${roleperson.title}">
                                        </#if>
                                    </div>

                                    <div class="person__text-container">
                                        <h4 class="person__name person__name--link">
                                            ${roleperson.title}
                                        </h4>
                                    </div>
                                </a>

                                <p class="person__roles">
                                    <#-- todo: allow for multiple here -->
                                    <#if role.incumbent??>
                                        <a class="person__role-link" href="${link}">${role.title}</a>
                                    <#else>
                                        ${role.title}
                                    </#if>
                                </p>

                            </div>
                        </li><!--
                    </#list>
                </#if>
         --></ul>
        </div>

        <#if document.updateHistory?has_content>
            <#include 'common/update-history.ftl'/>
        </#if>

    </article>
</div><!--

     --><div class="grid__item medium--four-twelfths large--three-twelfths push--large--two-twelfths">

    <#if document.contactInformation.twitter?has_content
        ||   document.contactInformation.flickr?has_content
        ||   document.contactInformation.website?has_content
        ||   document.contactInformation.email?has_content
        ||   document.contactInformation.facebook?has_content
        ||   document.contactInformation.youtube?has_content
        ||   document.contactInformation.blog?has_content>
            <#assign contactInformation = document.contactInformation/>
        <section class="sidebar-block">
            <#include 'common/contact-information.ftl' />
        </section>
    </#if>

    <#if document.relatedNews?has_content>
    <!--noindex-->
        <section class="sidebar-block">
            <h3 class="gamma emphasis sidebar-block__heading">News</h3>
            <ul class="no-bullets">
                <#list document.relatedNews as newsItem>
                    <li>
                        <@hst.link var="link" hippobean=newsItem/>
                        <a href="${link}" class="sidebar-block__link">${newsItem.title}</a>
                    </li>
                </#list>
            </ul>
        </section>
    <!--endnoindex-->
    </#if>

    <#if document.relatedPublications?has_content>
    <!--noindex-->
        <section class="sidebar-block">
            <h3 class="gamma emphasis sidebar-block__heading">Publications</h3>
            <ul class="no-bullets">
                <#list document.relatedPublications as publication>
                    <li>
                        <@hst.link var="link" hippobean=publication/>
                        <a href="${link}" class="sidebar-block__link">${publication.title}</a>
                    </li>
                </#list>
            </ul>
        </section>
    <!--endnoindex-->
    </#if>

    <#if policies?has_content>
    <!--noindex-->
        <section class="sidebar-block">
            <h3 class="gamma emphasis sidebar-block__heading">Policies</h3>
            <ul class="no-bullets">
                <#list policies as policy>
                    <li>
                        <@hst.link var="link" hippobean=policy/>
                        <a class="sidebar-block__link" data-gtm="policies-${policy?index + 1}"  href="${link}">${policy.title}</a>
                    </li>
                </#list>
            </ul>
        </section>
    <!--endnoindex-->
    </#if>

</div><!--
 --></div>

<div class="grid"><!--
    --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
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
