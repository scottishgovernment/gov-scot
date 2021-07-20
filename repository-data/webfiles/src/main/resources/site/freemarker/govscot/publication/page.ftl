<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<@hst.link hippobean=document var="baseurl" canonical=true/>

<@hst.manageContent hippobean=document/>
<!-- this outer div allows us to break the sticky header out of the layout grid -->
<div>
    <div class="ds_wrapper">
        <div class="ds_layout  gov_layout--publication">
            <div class="ds_layout__header">
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
                                <p class="ds_leader">${summaryParagraph}</p>
                            </#list>
                        </#if>

                        <#include '../common/collections-list.ftl'/>
                    </div>

                    <div class="gov_sublayout__document">
                        <div class="gov_supporting-documents">
                            <#if pages?has_content>
                                <#if documents?has_content>
                                    <#assign mainDocument = documents[0]/>
                                    <#assign filenameExtension = mainDocument.document.filename?keep_after_last(".")?upper_case/>

                                    <@hst.link var="documentdownload" hippobean=mainDocument.document>
                                        <@hst.param name="forceDownload" value="true"/>
                                    </@hst.link>

                                    <@hst.link var="documentinline" hippobean=mainDocument.document>
                                    </@hst.link>

                                    <#if filenameExtension == "PDF">
                                        <a class="gov_document-info__thumbnail-link" href="${baseurl + 'documents/'}">
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

                                    <a href="${baseurl + 'documents/'}" class="ds_button  ds_button--secondary  ds_no-margin--top  gov_supporting-documents__button">
                                        <span class="gov_supporting-documents__button-icon">
                                            <svg aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron-right"></use></svg>
                                        </span>
                                        <span class="gov_supporting-documents__button-text">Supporting documents</span>
                                    </a>
                                </#if>
                            </#if>
                        </div>
                    </div>
                </header>
            </div>
        </div>
    </div>

    <div class="gov_sticky-document-title">
        <div class="ds_wrapper">
            <div class="ds_layout  gov_layout--publication">
                <div class="ds_layout__content">
                    <div>
                        <span class="doctitle">${document.title}</span>
                        ${currentPage.title}
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script>
        function getOffsetFromDocumentTop(element) {
            let offset = element.offsetTop;

            while (element.offsetParent) {
                element = element.offsetParent;
                offset = offset + element.offsetTop
            }

            return offset;
        }

        const element = document.querySelector('.gov_sticky-document-title');
        const observer = new IntersectionObserver(
            function ([e]) {
                if (!e.isIntersecting && getOffsetFromDocumentTop(element) - 10 < window.scrollY) {
                    e.target.classList.add('is-pinned');
                } else {
                    e.target.classList.remove('is-pinned');
                }
            },
            { rootMargin: '-1px 0px 0px 0px', threshold: [1] }
        );

        observer.observe(element);
    </script>
    <style>
        .gov_sticky-document-title {
            position: sticky;
            top: -1px;
            background: #ebebeb;
            padding: 16px;
            z-index: 2
        }

        .gov_sticky-document-title::after {
            background: linear-gradient(rgba(0,0,0,0.2), rgba(0,0,0,0));
            bottom: -8px;
            content: '';
            left: 0;
            opacity: 0;
            pointer-events: none;
            position: absolute;
            right: 0;
            top: 100%;

            transition: opacity 0.2s;
        }

        .gov_sticky-document-title .doctitle {
            display: none;
            font-weight: 400;
        }

        .gov_sticky-document-title .doctitle::after {
            content: '—';
            margin: 0 0.35em 0 0.5em;
        }

        .gov_sticky-document-title.is-pinned .doctitle {
            display: inline;
        }

        .gov_sticky-document-title.is-pinned {
            border-top: 1px solid transparent;
        }

        .gov_sticky-document-title.is-pinned::after {
            opacity: 1;
        }
    </style>

    <div class="ds_wrapper">
        <div class="ds_layout  gov_layout--publication">
            <div class="ds_layout__sidebar">
                <#if pages??>
                    <#include 'side-menu.ftl'/>
                </#if>
            </div>

            <div class="ds_layout__content">
                <#if isMultiPagePublication>
                    <div class="body-content publication-content js-content-wrapper">
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
                            <#if document.foiNumber?has_content>
                                <strong>FOI reference:</strong> ${document.foiNumber}<br>
                            </#if>

                            <#if document.dateReceived?has_content>
                                <strong>Date received:</strong> <@fmt.formatDate value=document.dateReceived.time type="both" pattern="d MMM yyyy"/><br>
                            </#if>

                            <#if document.dateResponded?has_content>
                                <strong>Date responded:</strong> <@fmt.formatDate value=document.dateResponded.time type="both" pattern="d MMM yyyy"/><br>
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
                        </div>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
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
