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
            <#if !document.active>
                <div class="ds_inset-text">
                    <div class="ds_inset-text__text">
                        <p>This group is no longer active.</p>
                    </div>
                </div>
            </#if>
        </div>

        <div class="ds_layout__content">
            <h2>Overview</h2>
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
                ${members?no_esc}
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
        </div>

        <div class="ds_layout__sidebar">
            <#if document.relatedPolicies?has_content>
                <!--noindex-->
                <section class="ds_article-aside">
                    <h3>Policies</h3>

                    <ul class="ds_no-bullets">
                        <#list document.relatedPolicies as policy>
                            <li>
                                <@hst.link var="link" hippobean=policy />
                                <a data-gtm="policies-${policy?index + 1}" href="${link}">${policy.title}</a>
                            </li>
                        </#list>
                    </ul>
                </section>
                <!--endnoindex-->
            </#if>

            <@hst.html hippohtml=document.contactDetails var="contactDetails"/>
            <#if contactDetails?has_content>
                <section class="ds_article-aside">
                    <h3>Contact</h3>
                    ${contactDetails?no_esc}
                </section>
            </#if>
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
        <meta name="dc.format" content="Group"/>
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

    <#include "common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>
