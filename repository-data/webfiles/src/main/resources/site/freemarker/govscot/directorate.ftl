<#include "../include/imports.ftl">

<#if document??>
<div class="grid" id="page-content"><!--
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
                                <@hst.link var="link" hippobean=role.incumbent/>
                                <a class="person__link" href="${link}">

                                    <div class="person__image-container">
                                        <div class="person__image-container">
                                            <img class="person__image" src="/site/assets/images/people/placeholder.png" alt="" />
                                        </div>
                                    </div>

                                    <div class="person__text-container">
                                        <h4 class="person__name person__name--link">${role.incumbent.title}</h4>
                                        <p class="person__roles">
                                            <#-- todo: allow for multiple here -->
                                            <@hst.link var="link" hippobean=role/>
                                            <a class="person__role-link" href="${link}">${role.title}</a>
                                        </p>
                                    </div>

                                </a>
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
                                        <img class="person__image" src="/site/assets/images/people/placeholder.png" alt="" />
                                    </div>

                                    <div class="person__text-container">
                                        <h4 class="person__name person__name--link">
                                            <#if role.incumbent??>
                                                ${role.incumbent.title}
                                            <#else>
                                                ${role.title}
                                            </#if>
                                        </h4>

                                        <p class="person__roles">
                                            <#-- todo: allow for multiple here -->
                                            <#if role.incumbent??>
                                                <object><a class="person__role-link" href="${link}">${role.title}</a></object>
                                            <#else>
                                                ${role.roleTitle}
                                            </#if>
                                        </p>
                                    </div>
                                </a>
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
            <h2>Connect with us</h2>
            <#include 'common/contact-information.ftl' />
        </#if>


        <h2 class="heading--underline heading--underline--heavy">News</h2>
        <ul class="no-bullets">
            <li>
                <a href="{{url}}"></a>
            </li>
        </ul>

        <h2 class="heading--underline heading--underline--heavy">Publications</h2>
        <ul class="no-bullets">
            <li>
                <a href="{{url}}"></a>
            </li>
        </ul>

        <h2 class="heading--underline heading--underline--heavy">Policies</h2>
        <ul class="no-bullets">
            <li>
                <a href="{{url}}"></a>
            </li>
        </ul>

    </aside>

</div><!--
 --></div>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
<div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
</div>
</#if>
