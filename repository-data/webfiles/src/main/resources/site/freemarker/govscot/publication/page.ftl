<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">
<#include "../common/macros/lang-attributes.ftl">

<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<@hst.link hippobean=document var="baseurl" canonical=true/>

<@hst.manageContent hippobean=document/>
<!-- this outer div allows us to break the sticky header out of the layout grid -->
<div>

    <div class="ds_wrapper">
        <main <@lang document/> id="main-content">
            <#if pages?has_content && documents?has_content>
                <#assign hasDocuments = true>
            </#if>
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
                </div>

                <#if hasDocuments!false>
                    <div class="gov_sublayout__document">
                        <div <@revertlang document /> class="gov_supporting-documents">

                            <#assign mainDocument = documents[0]/>
                            <#assign filenameExtension = mainDocument.document.filename?keep_after_last(".")?lower_case/>

                            <@hst.link var="documentdownload" hippobean=mainDocument.document>
                                <@hst.param name="forceDownload" value="true"/>
                            </@hst.link>

                            <@hst.link var="documentinline" hippobean=mainDocument.document>
                            </@hst.link>

                            <#switch filenameExtension>
                                <#case "csv">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/csv.svg' />
                                    <#break>
                                <#case "xls">
                                <#case "xlsx">
                                <#case "xlsm">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/excel.svg' />
                                    <#break>
                                <#case "kml">
                                <#case "kmz">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/geodata.svg' />
                                    <#break>
                                <#case "gif">
                                <#case "jpg">
                                <#case "jpeg">
                                <#case "png">
                                <#case "svg">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/image.svg' />
                                    <#break>
                                <#case "pdf">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/pdf.svg' />
                                    <#break>
                                <#case "ppt">
                                <#case "pptx">
                                <#case "pps">
                                <#case "ppsx">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/ppt.svg' />
                                    <#break>
                                <#case "rtf">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/rtf.svg' />
                                    <#break>
                                <#case "txt">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/txt.svg' />
                                    <#break>
                                <#case "doc">
                                <#case "docx">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/word.svg' />
                                    <#break>
                                <#case "xml">
                                <#case "xsd">
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/xml.svg' />
                                    <#break>
                                <#default>
                                    <#assign fileThumbnailPath = '/assets/images/documents/svg/generic.svg' />
                            </#switch>

                            <!--noindex-->
                            <#if filenameExtension == "pdf" &&mainDocument.thumbnails[0]??>
                                <a class="gov_supporting-documents__thumbnail-link" href="${baseurl + 'documents/'}">
                                    <img
                                        alt="View supporting documents"
                                        class="gov_supporting-documents__thumbnail"
                                        src="<@hst.link hippobean=mainDocument.thumbnails[0]/>"
                                        srcset="
                                            <#list mainDocument.thumbnails as thumbnail>
                                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                            </#list>"
                                        sizes="(min-width: 768px) 104px, 72px" />
                                </a>
                            <#else>
                                <a class="ds_file-info__thumbnail-link  gov_supporting-documents__thumbnail-link" aria-hidden="true" href="${baseurl + 'documents/'}">
                                    <img width="104" height="152" class="ds_file-info__thumbnail-image  ds_file-info__thumbnail-image--generic" src="<@hst.link path=fileThumbnailPath />" alt=""/>
                                </a>
                            </#if>
                            <!--endnoindex-->

                            <a href="${baseurl + 'documents/'}" class="ds_button  ds_button--secondary  ds_no-margin--top  gov_supporting-documents__button">
                                <span class="gov_supporting-documents__button-icon">
                                    <svg aria-hidden="true" role="img"><use href="${iconspath}#expand_more"></use></svg>
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
                    <!--noindex-->
                    <div class="ds_layout__sidebar">
                        <#include 'side-menu.ftl'/>
                    </div>
                    <!--endnoindex-->
                </#if>

                <div class="ds_layout__content">
                    <#if isMultiPagePublication>
                        <@hst.manageContent hippobean=currentPage />

                        <div  class="body-content  publication-content  js-content-wrapper">
                            <div <@langcompare currentPage document />>
                                <@hst.html hippohtml=currentPage.content/>
                            </div>
                        </div>

                        <!--noindex-->
                        <nav class="ds_sequential-nav" aria-label="Article navigation">
                            <#if prev??>
                                <@hst.link var="link" hippobean=prev/>
                                <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                    <a <@langcompare prev document /> title="Previous page" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left  js-publication-navigation">
                                        <span class="ds_sequential-nav__text" data-label="previous">
                                            ${prev.title}
                                        </span>
                                    </a>
                                </div>
                            </#if>

                            <#if next??>
                                <@hst.link var="link" hippobean=next/>
                                <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                    <a <@langcompare next document /> title="Next page" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right  js-publication-navigation">
                                        <span class="ds_sequential-nav__text" data-label="next">
                                            ${next.title}
                                        </span>
                                    </a>
                                </div>
                            </#if>
                        </nav>
                        <!--endnoindex-->

                        <@hst.html hippohtml=document.contact var="contact"/>
                        <#if contact?has_content>
                            <section <@revertlang document /> class="gov_content-block">
                                <h3 class="gov_content-block__title">Contact</h3>
                                ${contact?no_esc}
                            </section>
                        </#if>

                        <#if document.updateHistory?has_content>
                            <section <@revertlang document />>
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
                                        ${content?no_esc}
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
                                    <h2 <@revertlang document />>Attendees and apologies</h2>
                                    ${attendees?no_esc}
                                </#if>

                                <@hst.html hippohtml=document.actions var="actions"/>
                                <#if actions?has_content>
                                    <h2 <@revertlang document />>Items and actions</h2>
                                    ${actions?no_esc}
                                </#if>
                                <#--! END 'minutes' format-specific fields-->

                                <@hst.html hippohtml=document.request var="request"/>
                                <#if request?has_content>
                                    <h2 <@revertlang document />>Information requested</h2>
                                    ${request?no_esc}
                                </#if>

                                <@hst.html hippohtml=document.response var="response"/>
                                <#if response?has_content>
                                    <h2 <@revertlang document />>Response</h2>
                                    ${response?no_esc}
                                </#if>
                                <#--! END 'FOI/EIR release' format-specific fields-->

                                <#if documents??>
                                    <#assign hasAttachedDocument = '' />
                                    <#list documents as attachedDocument>
                                        <#assign hasAttachedDocument = true />
                                    </#list>

                                    <#if hasAttachedDocument?has_content>
                                        <section <@revertlang document /> class="document-section">
                                            <#list documents as attachedDocument>
                                                <#assign docindex= attachedDocument?counter />
                                                <#include 'body-document-info.ftl'/>
                                            </#list>
                                        </section>
                                    </#if>

                                    <#if groupedDocumentFolders??>
                                        <#list groupedDocumentFolders as folder>
                                            <#assign groupindex = folder?counter + '-'/>
                                            <section <@revertlang document /> class="document-section">
                                                <h2>${folder.displayName}</h2>
                                                <#list folder.documents as attachedDocument>
                                                    <#assign docindex = groupindex + attachedDocument?counter />
                                                    <#include 'body-document-info.ftl'/>
                                                </#list>
                                            </section>
                                        </#list>
                                    </#if>
                                </#if>

                                <@hst.html hippohtml=document.epilogue var="epilogue"/>
                                <#if epilogue?has_content>
                                <div id="epilogue">
                                    ${epilogue?no_esc}
                                </div>
                                </#if>

                                <@hst.html hippohtml=document.contact var="contact"/>
                                <#if contact?has_content>
                                    <div <@revertlang document /> class="gov_content-block">
                                        <h3 class="gov_content-block__title">Contact</h3>
                                        ${contact?no_esc}
                                    </div>
                                </#if>

                                <#if document.updateHistory?has_content>
                                    <#include '../common/update-history.ftl'/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                </div>

                <div <@revertlang document /> class="ds_layout__feedback">
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
        <#include "../common/gtm-datalayer.ftl"/>
    <#else>
        <@hst.link var="canonicalitem" hippobean=document canonical=true/>
        <#include "../common/gtm-datalayer.ftl"/>
    </#if>

    <#include "../common/canonical.ftl" />
</#if>
