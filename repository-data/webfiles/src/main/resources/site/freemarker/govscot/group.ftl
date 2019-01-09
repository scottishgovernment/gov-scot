<#include "../include/imports.ftl">

<#if document??>

<@hst.manageContent hippobean=document/>

<article id="page-content" class="layout--group">

    <div class="grid"><!--

        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">

        <div class="article-header">
            <h1>${document.title}</h1>
        </div>

        <h2>Role of the group</h2>
        <@hst.html hippohtml=document.content/>

        <#if document.relatedGroups?has_content>
            <h2>Related groups</h2>

            <ul>
                <#list document.relatedGroups as group>
                    <li>
                        <@hst.link var="link" hippobean=group />
                        <a href="${link}">${group.title}</a>
                    </li>
                </#list>
            </ul>
        </#if>

        <@hst.html hippohtml=document.members var="members" />
        <#if members?has_content>
            <h2>Members</h2>
        ${members}
        </#if>

        <#if document.relatedPublications?has_content>
            <h2>Documents</h2>

            <ul>
                <#list document.relatedPublications as document>
                    <li>
                        <@hst.link var="link" hippobean=document />
                        <a href="${link}">${document.title}</a>
                    </li>
                </#list>
            </ul>
        </#if>

    </div><!--

         --><div class="grid__item medium--nine-twelfths large--three-twelfths push--large--two-twelfths">
        <aside>
            <#if document.relatedPolicies?has_content>
                <div class="sidebar-block">
                    <h3 class="emphasis sidebar-block__heading">Related policies</h3>

                    <ul class="sidebar-block__list no-bullets">
                        <#list document.relatedPolicies as policy>
                            <li class="sidebar-block__list-item">
                                <@hst.link var="link" hippobean=policy />
                                <a href="${link}">${policy.title}</a>
                            </li>
                        </#list>
                    </ul>
                </div>
            </#if>

            <@hst.html hippohtml=document.contactDetails var="contactDetails"/>
            <#if contactDetails?has_content>
                <div class="sidebar-block">
                    <h3 class="emphasis sidebar-block__heading">Contacts</h3>
                    ${contactDetails}
                </div>
            </#if>
        </aside>
    </div><!--
     --></div>

</article>

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
