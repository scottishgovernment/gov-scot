<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<@hst.link hippobean=document var="baseurl" canonical=true/>

<@hst.manageContent hippobean=document/>
<!-- this outer div allows us to break the sticky header out of the layout grid -->
<div>
    <div class="ds_wrapper">
        <main id="main-content">
            <#if pages?has_content && documents?has_content>
                <#assign hasDocuments = true>
            </#if>
            <header class="ds_page-header  gov_sublayout  gov_sublayout--publication-header">
                <div class="gov_sublayout__title">
                    <span class="ds_page-header__label  ds_content-label">Publication<#if document.label??> - ${document.label}</#if></span>
                    <h1 class="ds_page-header__title">${document.title?html}</h1>
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
                </div>

                <#if hasDocuments!false>
                    <div class="gov_sublayout__document">
                        <div class="gov_supporting-documents">

                            <#assign mainDocument = documents[0]/>
                            <#assign filenameExtension = mainDocument.document.filename?keep_after_last(".")?upper_case/>

                            <@hst.link var="documentdownload" hippobean=mainDocument.document>
                                <@hst.param name="forceDownload" value="true"/>
                            </@hst.link>

                            <@hst.link var="documentinline" hippobean=mainDocument.document>
                            </@hst.link>

                            <#if filenameExtension == "PDF">
                                <a class="gov_supporting-documents__thumbnail-link" href="${baseurl + 'documents/'}">
                                    <img
                                        alt="View supporting documents"
                                        class="gov_supporting-documents__thumbnail"
                                        src="<@hst.link hippobean=mainDocument.thumbnails[0]/>"
                                        srcset="
                                            <#list mainDocument.thumbnails as thumbnail>
                                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                            </#list>"
                                        sizes="(min-width: 768px) 165px, 107px" />
                                </a>
                            <#else>
                                <a aria-hidden="true" href="${baseurl + 'documents/'}" class="gov_file-icon  gov_file-icon--${filenameExtension!''}">
                                    <svg class="gov_file-icon__label" viewBox="0 0 210 297">
                                        <text x="50%" y="55%" text-anchor="middle" dominant-baseline="middle" font-size="3em">${filenameExtension!''}</text>
                                    </svg>
                                    <svg class="gov_file-icon__image" role="img"><use xlink:href="${iconspath}#file-icon"></use></svg>
                                </a>
                            </#if>

                            <a href="${baseurl + 'documents/'}" class="ds_button  ds_button--secondary  ds_no-margin--top  gov_supporting-documents__button">
                                <span class="gov_supporting-documents__button-icon">
                                    <svg aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron-right"></use></svg>
                                </span>
                                <span class="gov_supporting-documents__button-text">Supporting documents</span>
                            </a>
                        </div>
                    </div>
                </#if>
            </header>

            <hr />

            <#if pages??>
                <#assign hasSidebar = true/>
            </#if>
            <div class="ds_layout  <#if hasSidebar!false>gov_layout--publication<#else>gov_layout--publication--no-sidebar</#if>">
                <#if pages??>
                    <div class="ds_layout__sidebar">
                        <#include 'side-menu.ftl'/>
                    </div>
                </#if>

                <div class="ds_layout__content">
                    <#if isMultiPagePublication>
                        <div class="body-content  publication-content  js-content-wrapper">
                            <@hst.html hippohtml=currentPage.content/>
                        </div>

                        <nav class="ds_sequential-nav" aria-label="Article navigation">
                            <#if prev??>
                                <@hst.link var="link" hippobean=prev/>
                                <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                    <a title="Previous page" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                        <span class="ds_sequential-nav__text" data-label="previous">
                                            ${prev.title}
                                        </span>
                                    </a>
                                </div>
                            </#if>

                            <#if next??>
                                <@hst.link var="link" hippobean=next/>
                                <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                    <a title="Next page" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                        <span class="ds_sequential-nav__text" data-label="next">
                                            ${next.title}
                                        </span>
                                    </a>
                                </div>
                            </#if>
                        </nav>

                        <@hst.html hippohtml=document.contact var="contact"/>
                        <#if contact?has_content>
                            <section class="gov_content-block">
                                <h3 class="gov_content-block__title">Contact</h3>
                                ${contact}
                            </section>
                        </#if>

                        <#if document.updateHistory?has_content>
                            <section>
                                <#include '../common/update-history.ftl'/>
                            </section>
                        </#if>
                    <#else>
                        <div id="preamble">
                            <div class="body-content publication-body">
                                <@hst.html hippohtml=document.content var="content"/>
                                <#if content?has_content>
                                    <#if content?contains("</")>
                                        <#--  CONTAINS CLOSING TAG  -->
                                        ${content}
                                    <#else>
                                        <#--  DOES NOT HAVE CLOSING TAG  -->
                                        <#list content?split("\\n") as contentParagraph>
                                            <p>${contentParagraph}</p>
                                        </#list>
                                    </#if>
                                </#if>

                                <#--! BEGIN 'minutes' format-specific fields-->
                                <@hst.html hippohtml=document.attendees var="attendees"/>
                                <#if attendees?has_content>
                                    <h2>Attendees and apologies</h2>
                                    ${attendees}
                                </#if>

                                <@hst.html hippohtml=document.actions var="actions"/>
                                <#if actions?has_content>
                                    <h2>Items and actions</h2>
                                    ${actions}
                                </#if>
                                <#--! END 'minutes' format-specific fields-->

                                <#--! BEGIN 'FOI/EIR release' format-specific fields-->
                                <@hst.html hippohtml=document.request var="request"/>
                                <#if request?has_content>
                                    <h2>Information requested</h2>
                                    ${request}
                                </#if>

                                <@hst.html hippohtml=document.response var="response"/>
                                <#if response?has_content>
                                    <h2>Response</h2>
                                    ${response}
                                </#if>
                                <#--! END 'FOI/EIR release' format-specific fields-->

                                <#if documents??>
                                    <#assign hasAttachedDocument = '' />
                                    <#list documents as attachedDocument>
                                        <#assign hasAttachedDocument = true />
                                    </#list>

                                    <#if hasAttachedDocument?has_content>
                                        <section class="document-section">
                                            <#list documents as attachedDocument>
                                                <#include 'body-document-info.ftl'/>
                                            </#list>
                                        </section>
                                    </#if>

                                    <#if groupedDocumentFolders??>
                                        <#list groupedDocumentFolders as folder>
                                            <section class="document-section">
                                                <h2>${folder.displayName}</h2>
                                                <#list folder.documents as attachedDocument>
                                                    <#include 'body-document-info.ftl'/>
                                                </#list>
                                            </section>
                                        </#list>
                                    </#if>
                                </#if>

                                <@hst.html hippohtml=document.epilogue var="epilogue"/>
                                <#if epilogue?has_content>
                                <div id="epilogue">
                                    ${epilogue}
                                </div>
                                </#if>

                                <@hst.html hippohtml=document.contact var="contact"/>
                                <#if contact?has_content>
                                    <div class="gov_content-block">
                                        <h3 class="gov_content-block__title">Contact</h3>
                                        ${contact}
                                    </div>
                                </#if>

                                <#if document.updateHistory?has_content>
                                    <#include '../common/update-history.ftl'/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                </div>

                <div class="ds_layout__feedback">
                    <#include '../common/feedback-wrapper.ftl'>
                </div>
            </div>
        </main>
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
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>

        <#if document.metaDescription??>
            <meta name="description" content="${document.metaDescription?html}"/>
        </#if>
    </@hst.headContribution>

    <#if isMultiPagePublication && (currentPage != pages[0])>
        <@hst.link var="canonicalitem" hippobean=currentPage canonical=true/>
        <#assign uuid = currentPage.getProperty('jcr:uuid')/>
        <#assign lastUpdated = currentPage.getProperty('hippostdpubwf:lastModificationDate')/>
        <#assign dateCreated = currentPage.getProperty('hippostdpubwf:creationDate')/>
        <#include "../common/gtm-datalayer.ftl"/>
    <#else>
        <@hst.link var="canonicalitem" hippobean=document canonical=true/>
        <#include "../common/gtm-datalayer.ftl"/>
    </#if>

    <#include "../common/canonical.ftl" />
</#if>
