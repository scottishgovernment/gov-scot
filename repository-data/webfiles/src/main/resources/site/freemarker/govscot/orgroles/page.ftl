<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if document??>
<div class="layout--org-roles">
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--about">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <#include "../common/metadata.ftl"/>
                </header>
            </div>

            <div class="ds_layout__sidebar">
                <!--noindex-->
                <@hst.include ref="side-menu"/>
                <!--endnoindex-->
            </div>

            <div class="ds_layout__content">
                <@hst.include ref="content"/>

                <#if document.updateHistory?has_content>
                    <div class="update-history">
                        <#include '../common/update-history.ftl'/>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
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

    <#include "../common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />
</#if>
