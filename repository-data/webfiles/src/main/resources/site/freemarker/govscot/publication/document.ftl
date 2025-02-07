<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if document??>

<@hst.manageContent hippobean=document/>
<@hst.link hippobean=document var="baseurl" canonical=true/>

<div class="ds_wrapper">
    <header class="ds_page-header  gov_sublayout  gov_sublayout--publication-header">
        <div class="gov_sublayout__title">
            <span <@revertlang document /> class="ds_page-header__label  ds_content-label">Publication<#if document.label??> - <span id="sg-meta__publication-type">${document.label}</span></#if></span>
            <h1 class="ds_page-header__title">${document.title}</h1>
        </div>

        <div class="gov_sublayout__metadata">
            <#include 'metadata.ftl'/>
        </div>

        <div class="gov_sublayout__content">
            <#if document.summary??>
                <#list document.summary?split("\n") as summaryParagraph>
                    <p class="ds_leader  ds_no-margin--bottom">${summaryParagraph}</p>
                </#list>
            </#if>
            <#include '../common/collections-list.ftl'/>
            <#include 'consultation-response.ftl'/>
        </div>
    </header>
    <hr />
    <div class="ds_layout  gov_layout--publication--no-sidebar">
        <div class="ds_layout__content">
            <!--noindex-->
            <h2>Supporting documents</h2>
            <!--endnoindex-->

            <#if groupedDocumentFolders??>
                <#list groupedDocumentFolders as folder>
                    <section class="document-section">
                        <h2>${folder.displayName}</h2>
                        <#list folder.documents as attachedDocument>
                            <#assign isTargetedItem = false/>
                            <#if doc?? && doc = attachedDocument>
                                <#assign isTargetedItem = true/>
                            </#if>

                            <#assign isHighlightedItem = attachedDocument?is_first/>
                            <#assign docindex = "group-" + folder?counter />
                            <#include 'body-document-info.ftl'/>
                        </#list>
                    </section>
                </#list>
            <#elseif documents?? && documents?size &gt; 0>
                <#list documents as attachedDocument>
                    <#assign isTargetedItem = false/>

                    <#if doc?? && doc = attachedDocument>
                        <#assign isTargetedItem = true/>
                    </#if>
                    <#assign isHighlightedItem = attachedDocument?is_first/>
                    <#assign docindex= attachedDocument?counter />
                    <#include 'body-document-info.ftl'/>
                </#list>
            <#else>
            <!--noindex-->
            <p>There are no supporting documents for this publication.</p>
            <!--endnoindex-->
            </#if>

            <!--noindex-->
            <nav class="ds_sequential-nav" aria-label="Article navigation">
                <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                    <a href="${baseurl}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                        <span class="ds_sequential-nav__text" data-label="return">
                            Main publication
                        </span>
                    </a>
                </div>
            </nav>
            <!--endnoindex-->

            <#if document.updateHistory?has_content>
                <#include '../common/update-history.ftl'/>
            </#if>
        </div>

        <div class="ds_layout__feedback">
            <#include '../common/feedback-wrapper.ftl'>
        </div>
    </div>
</div>
</#if>

<#include "../common/schema.article.ftl"/>

<@hst.headContribution category="footerScripts">
<script type="module" src="<@hst.webfile path="/assets/scripts/publication.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
<script nomodule="true" src="<@hst.webfile path="/assets/scripts/publication.es5.js"/>"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Publication"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <#if document.metaDescription??>
            <meta name="description" content="${document.metaDescription}"/>
        </#if>
    </@hst.headContribution>

    <#if isMultiPagePublication && (currentPage != pages[0])>
        <@hst.link var="canonicalitem" hippobean=currentPage canonical=true/>
        <#assign uuid = currentPage.getSingleProperty('jcr:uuid')/>
        <#assign lastUpdated = currentPage.getSingleProperty('hippostdpubwf:lastModificationDate')/>
        <#assign dateCreated = currentPage.getSingleProperty('hippostdpubwf:creationDate')/>
    <#else>
        <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    </#if>

    <@hst.headContribution>
        <link rel="canonical" href="https://www.gov.scot${canonicalitem?replace("^/site/", "/", "r")}documents/"/>
    </@hst.headContribution>

    <#include "../common/gtm-datalayer.ftl"/>
</#if>
