<#include "../include/imports.ftl">

<#if document??>

<@hst.manageContent hippobean=document/>

<div class="grid layout--directorate" id="page-content"><!--
     --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

    <article>

        <h1 class="article-header">
            ${document.title}
        </h1>

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
                                        ${role.roleTitle}
                                    </#if>
                                </p>

                            </div>
                        </li><!--
                    </#list>
                </#if>
         --></ul>
        </div>

    </article>
</div><!--

     --><div class="grid__item medium--four-twelfths large--three-twelfths push--large--two-twelfths">

    <aside class="sidebar-content">

        <#if document.contactInformation.twitter?has_content
        ||   document.contactInformation.flickr?has_content
        ||   document.contactInformation.website?has_content
        ||   document.contactInformation.email?has_content
        ||   document.contactInformation.facebook?has_content
        ||   document.contactInformation.youtube?has_content
        ||   document.contactInformation.blog?has_content
        ||   document.contactInformation.postalAddress.content?has_content>
            <#assign contactInformation = document.contactInformation/>
            <h2>Connect with us</h2>
            <#include 'common/contact-information.ftl' />
        </#if>

        <#if document.relatedNews?has_content>
            <h2 class="heading--underline heading--underline--heavy">News</h2>
            <#list document.relatedNews as newsItem>
                <ul class="no-bullets">
                    <li>
                        <@hst.link var="link" hippobean=newsItem/>
                        <a href="${link}">${newsItem.title}</a>
                    </li>
                </ul>
            </#list>
        </#if>

        <#if document.relatedPublications?has_content>
            <h2 class="heading--underline heading--underline--heavy">Publications</h2>
            <#list document.relatedPublications as publication>
                <ul class="no-bullets">
                    <li>
                        <@hst.link var="link" hippobean=publication/>
                        <a href="${link}">${publication.title}</a>
                    </li>
                </ul>
            </#list>
        </#if>

        <#if policies?has_content>
            <h2 class="heading--underline heading--underline--heavy">Policies</h2>
            <#list policies as policy>
                <ul class="no-bullets">
                    <li>
                        <@hst.link var="link" hippobean=policy/>
                        <a href="${link}">${policy.title}</a>
                    </li>
                </ul>
            </#list>
        </#if>

    </aside>

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
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "common/canonical.ftl" />
</#if>
