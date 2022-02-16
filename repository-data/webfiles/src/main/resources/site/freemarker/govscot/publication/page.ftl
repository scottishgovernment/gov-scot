<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">

<#if document??>
</div>


<@hst.manageContent hippobean=document />
<@hst.link hippobean=document var="baseurl" canonical=true/>

<article id="page-content" class="layout--publication">

    <#--------------------- HEADER SECTION --------------------->
    <div class="top-matter">
        <div class="wrapper">
            <header class="article-header no-bottom-margin">

                <div class="grid"><!--
                    --><div class="grid__item  medium--nine-twelfths"><!--
                        --><div class="grid"><!--
                            --><div class="grid__item">
                                <p class="article-header__label">Publication<#if document.label??> - <span id="sg-meta__publication-type">${document.label}</span></#if></p>
                                <h1 class="article-header__title">${document.title}</h1>
                            </div><!--
                            --><div class="grid__item  large--three-ninths">
                                <#include 'metadata.ftl'/>
                            </div><!--
                            --><div class="grid__item  large--six-ninths">
                                <#list document.summary?split("\n") as summaryParagraph>
                                    <p class="leader">${summaryParagraph}</p>
                                </#list>

                                <#include '../common/collections-list.ftl'/>
                            </div><!--
                        --></div><!--
                    --></div><!--
                    --><div class="grid__item  medium--three-twelfths  large--two-twelfths  push--large--one-twelfth">
                        <div class="gov_supporting-documents">
                            <#if pages?has_content>
                                <#if documents?has_content>
                                    <#assign hasSupportingDocs = true />
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
                                                class="gov_document-info__thumbnail-image"
                                                src="<@hst.link hippobean=mainDocument.thumbnails[0]/>"
                                                srcset="
                                                    <#list mainDocument.thumbnails as thumbnail>
                                                        <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                                    </#list>"
                                                sizes="(min-width: 768px) 165px, 107px" />
                                        </a>
                                    <#else>
                                        <a aria-hidden="true" href="<#if filenameExtension == "CSV">${documentdownload}<#else>${documentinline}</#if>" class="hidden-small  gov_document-info__thumbnail-link  file-icon  file-icon--large  file-icon--${filenameExtension}"></a>
                                    </#if>

                                    <!--noindex-->
                                    <a href="${baseurl + 'documents/'}" class="button  button--secondary  ds_no-margin--top  gov_supporting-documents__button">
                                        <span class="gov_supporting-documents__button-icon">
                                            <svg aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron-right"></use></svg>
                                        </span>
                                        <span class="gov_supporting-documents__button-text">Supporting documents</span>
                                    </a>
                                    <!--endnoindex-->
                                </#if>
                            </#if>
                        </div>
                    </div><!--
                --></div>
            </header>
        </div>
    </div>

    <!--noindex-->
    <#include 'sticky-document-info.ftl'/>
    <!--endnoindex-->

    <#--------------------- BODY SECTION --------------------->
    <div class="inner-shadow-top  js-sticky-header-position <#if isMultiPagePublication>inner-shadow-top--no-mobile</#if>">

        <div class="wrapper js-content-wrapper">
            <div class="grid"><!--

             --><div class="grid__item  medium--four-twelfths  large--three-twelfths <#if !isMultiPagePublication>hidden-xsmall  hidden-small  hidden-medium</#if>">
                    <#if pages??>
                        <!--noindex-->
                        <#include 'side-menu.ftl'/>
                        <!--endnoindex-->
                    </#if>
                </div><!--

                <#if isMultiPagePublication>
                 --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

                        <div class="body-content publication-content js-content-wrapper inner-shadow-top  inner-shadow-top--no-desktop ">
                            <@hst.manageContent hippobean=currentPage />
                            <@hst.html hippohtml=currentPage.content/>
                        </div>

                        <!--noindex-->
                        <div class="grid  page-nav"><!--
                         --><div class="grid__item  push--medium--six-twelfths  medium--six-twelfths  page-nav__item">
                                <#if next??>
                                    <a title="Next page" href="<@hst.link hippobean=next/>" class="page-nav__button  page-nav__button--right  js-next">
                                        <span data-label="next" class="page-nav__text">${next.title}</span>
                                    </a>
                                </#if>
                            </div><!--
                         --><div class="grid__item  medium--six-twelfths  pull--medium--six-twelfths  page-nav__item">
                                <#if prev??>
                                    <a title="Previous page" href="<@hst.link hippobean=prev/>" class="page-nav__button  page-nav__button--left  js-previous">
                                        <span data-label="previous" class="page-nav__text">${prev.title}</span>
                                    </a>
                                </#if>
                            </div><!--
                     --></div>
                     <!--endnoindex-->

                        <hr>

                        <@hst.html hippohtml=document.contact var="contact"/>
                        <#if contact?has_content>
                            <section class="publication-info__section publication-info__contact">
                                <h3 class="emphasis">Contact</h3>
                                ${contact}
                            </section>
                        </#if>

                        <#if document.updateHistory?has_content>
                            <section>
                                <#include '../common/update-history.ftl'/>
                            </section>
                        </#if>

                    </div><!--
                <#else>
                 --><div class="grid__item large--seven-twelfths">

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
                                <#if document.foiNumber?has_content>
                                    <strong>FOI reference:</strong> <span id="sg-meta__foi-number">${document.foiNumber}</span><br>
                                </#if>

                                <#if document.dateReceived?has_content>
                                    <strong>Date received:</strong> <span id="sg-meta__foi-received-date"><@fmt.formatDate value=document.dateReceived.time type="both" pattern="d MMM yyyy"/></span><br>
                                </#if>

                                <#if document.dateResponded?has_content>
                                    <strong>Date responded:</strong> <span id="sg-meta__foi-responded-date"><@fmt.formatDate value=document.dateResponded.time type="both" pattern="d MMM yyyy"/></span><br>
                                </#if>

                                <@hst.html hippohtml=document.request var="request"/>
                                <#if request?has_content>
                                    <div class="body-content publication-body">
                                        <strong>Information requested</strong><br>
                                        ${request}
                                    </div>
                                </#if>

                                <@hst.html hippohtml=document.response var="response"/>
                                <#if response?has_content>
                                    <strong>Response</strong><br>
                                    ${response}
                                </#if>
                                <#--! END 'FOI/EIR release' format-specific fields-->
                            </div>
                        </div>

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
                            <div class="publication-info__contact">
                                <h3 class="emphasis">Contact</h3>
                                ${contact}
                            </div>
                        </#if>

                        <#if document.updateHistory?has_content>
                            <#include '../common/update-history.ftl'/>
                        </#if>

                    </div><!--
                </#if>

         --></div>

        </div>
    </div>
</article>

<div class="wrapper">
    <div class="grid"><!--
        --><div class="grid__item  large--seven-twelfths  push--large--three-twelfths">
            <#include '../common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
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
