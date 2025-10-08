<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<#if currentChapter??>
    <#assign subsectionTitle = currentChapter.displayName/>
<#elseif isAboutPage??>
    <#assign subsectionTitle = "About this publication"/>
<#elseif isDocumentsPage??>
    <#assign subsectionTitle = "Supporting documents"/>
<#else>
    <#assign subsectionTitle = "Contents"/>
    <#assign disableMenuButton = true/>
    <#assign isHomePage = true/>
</#if>

<@hst.link hippobean=prev var="prevlink"/>
<@hst.link hippobean=next var="nextlink"/>
<@hst.link hippobean=document var="baseurl" canonical=true/>

<@hst.manageContent hippobean=document/>
<!-- this outer div allows us to break the sticky header out of the layout grid -->
<div>
    <div class="ds_wrapper">
        <main id="main-content">
            <header class="ds_page-header  gov_sublayout  gov_sublayout--publication-header">
                <div class="gov_sublayout__title">
                    <span class="ds_page-header__label  ds_content-label">Publication - ${document.label}</span>
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </div>

                <div class="gov_sublayout__metadata">
                    <#include '../publication/metadata.ftl'/>
                </div>

                <div class="gov_sublayout__content">
                    <#if document.summary??>
                        <p class="ds_leader  ds_no-margin">${document.summary}</p>
                    </#if>

                    <#include '../common/collections-list.ftl'/>
                </div>

                <div class="gov_sublayout__document">
                    <#if documents?? && documents?size gt 0>
                        <div class="gov_supporting-documents">
                            <#if document.displayPrimaryDocument == true>
                                <#if documents??>
                                    <#assign firstDocument = documents[0]/>
                                    <#assign filenameExtension = firstDocument.document.filename?keep_after_last(".")?lower_case/>
                                    <#assign filenameWithoutExtension = firstDocument.document.filename?keep_before_last(".")/>
                                </#if>

                                <#switch filenameExtension>
                                    <#case "csv">
                                        <#assign fileDescription = "CSV file" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/csv.svg' />
                                        <#break>
                                    <#case "xls">
                                    <#case "xlsx">
                                    <#case "xlsm">
                                        <#assign fileDescription = "Excel document" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/excel.svg' />
                                        <#break>
                                    <#case "kml">
                                    <#case "kmz">
                                        <#assign fileDescription = "${filenameExtension} data" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/geodata.svg' />
                                        <#break>
                                    <#case "gif">
                                    <#case "jpg">
                                    <#case "jpeg">
                                    <#case "png">
                                    <#case "svg">
                                        <#assign fileDescription = "Image" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/image.svg' />
                                        <#break>
                                    <#case "pdf">
                                        <#assign fileDescription = "PDF" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/pdf.svg' />
                                        <#break>
                                    <#case "ppt">
                                    <#case "pptx">
                                    <#case "pps">
                                    <#case "ppsx">
                                        <#assign fileDescription = "Powerpoint document" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/ppt.svg' />
                                        <#break>
                                    <#case "rtf">
                                        <#assign fileDescription = "Rich text file" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/rtf.svg' />
                                        <#break>
                                    <#case "txt">
                                        <#assign fileDescription = "Text file" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/txt.svg' />
                                        <#break>
                                    <#case "doc">
                                    <#case "docx">
                                        <#assign fileDescription = "Word document" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/word.svg' />
                                        <#break>
                                    <#case "xml">
                                    <#case "xsd">
                                        <#assign fileDescription = "${filenameExtension} file" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/xml.svg' />
                                        <#break>
                                    <#case "odf">
                                        <#assign fileDescription = "OpenDocument Formula" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/odf.svg' />
                                        <#break>
                                    <#case "odg">
                                        <#assign fileDescription = "OpenDocument Graphics" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/odg.svg' />
                                        <#break>
                                    <#case "odp">
                                        <#assign fileDescription = "OpenDocument Presentation" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/odp.svg' />
                                        <#break>
                                    <#case "ods">
                                        <#assign fileDescription = "OpenDocument Spreadsheet" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/ods.svg' />
                                        <#break>
                                    <#case "odt">
                                        <#assign fileDescription = "OpenDocument Text" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/odt.svg' />
                                        <#break>
                                    <#default>
                                        <#assign fileDescription = "${filenameExtension} file" />
                                        <#assign fileThumbnailPath = '/assets/images/documents/svg/generic.svg' />
                                </#switch>

                                <!--noindex-->
                                <#if (filenameExtension!'') == "pdf" && firstDocument.thumbnails[0]??>
                                    <a class="gov_supporting-documents__thumbnail-link" href="${baseurl + 'documents/'}">
                                        <img
                                            class="gov_supporting-documents__thumbnail"
                                            alt="View supporting documents"
                                            src="<@hst.link hippobean=firstDocument.thumbnails[0]/>"
                                            srcset="
                                            <#list firstDocument.thumbnails as thumbnail>
                                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                            </#list>"
                                            sizes="(min-width: 768px) 165px, 107px" />
                                    </a>
                                <#else>
                                    <a class="ds_file-info__thumbnail-link  gov_supporting-documents__thumbnail-link" aria-hidden="true" tabindex="-1" href="${baseurl + 'documents/'}"">
                                        <img width="104" height="152" class="ds_file-info__thumbnail-image  ds_file-info__thumbnail-image--generic" src="<@hst.link path=fileThumbnailPath />" alt=""/>
                                    </a>
                                </#if>
                            </#if>

                            <a href="${baseurl + 'documents/'}" class="ds_button  ds_button--secondary  ds_no-margin--top  gov_supporting-documents__button">
                                <span class="gov_supporting-documents__button-icon">
                                    <svg aria-hidden="true" role="img"><use href="${iconspath}#expand_more"></use></svg>
                                </span>
                                <span class="gov_supporting-documents__button-text">Supporting documents</span>
                            </a>
                            <!--endnoindex-->
                        </div>
                    </#if>
                </div>
            </header>

            <hr />

            <div class="ds_layout  gov_layout--publication  <#if currentPage == document>gov_layout--publication--no-sidebar</#if>">
                <#if currentPage != document>
                    <!--noindex-->
                    <div class="ds_layout__sidebar">
                        <nav class="ds_side-navigation  ds_no-margin--top" data-module="ds-side-navigation">
                            <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
                            <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation">Choose section <span class="ds_side-navigation__expand-indicator"></span></label>

                            <ul class="ds_side-navigation__list" id="side-navigation-root">
                                <li class="ds_side-navigation__item">
                                    <a class="ds_side-navigation__link" href="${baseurl}">
                                        <b>Table of contents</b>
                                    </a>
                                </li>

                                <#list chapters as chapter>
                                    <#if chapter.documents?has_content>
                                        <li class="ds_side-navigation__item">
                                            <#if chapter == currentChapter>
                                                <span class="ds_side-navigation__link  ds_side-navigation__link--inactive">
                                                    <b>${chapter.displayName}</b>
                                                </span>

                                                <ul class="ds_side-navigation__list">
                                                    <#list chapter.documents as page>
                                                        <li class="ds_side-navigation__item">
                                                            <a class="ds_side-navigation__link<#if page == currentPage>  ds_current</#if>" href="<@hst.link hippobean=page/>"<#if page == currentPage> aria-current="page"</#if>>
                                                                ${page.title}
                                                            </a>
                                                        </li>
                                                    </#list>
                                                </ul>
                                            <#else>

                                                <@hst.link var="link" hippobean=chapter.documents?first/>
                                                <a class="ds_side-navigation__link" href="${link}">
                                                    ${chapter.displayName}
                                                </a>
                                            </#if>
                                        </li>
                                    </#if>
                                </#list>
                            </ul>
                        </nav>
                    </div>
                    <!--endnoindex-->
                </#if>

                <div class="ds_layout__content">
                    <#if isAboutPage??>
                        <h2>About this publication</h2>

                        <#include '../publication/metadata.ftl'/>

                        <@hst.html hippohtml=document.content/>

                        <#if documents?has_content>
                            <div>
                                <#assign attachedDocument = documents[0]/>
                                <#assign useCoverPage = true/>
                                <#assign docindex = "main" />
                                <#include '../publication/body-document-info.ftl'/>
                            </div>
                        </#if>

                        <@hst.html hippohtml=document.revisions var="docRevisions"/>
                        <#if docRevisions?has_content>
                            <div class="ds_accordion" data-module="ds-accordion">
                                <div class="ds_accordion-item">
                                    <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-revisions" aria-labelledby="panel-revisions-heading" />
                                    <div class="ds_accordion-item__header">
                                        <h3 id="panel-revisions-heading" class="ds_accordion-item__title">
                                            Revisions
                                        </h3>
                                        <span class="ds_accordion-item__indicator"></span>
                                        <label class="ds_accordion-item__label" for="panel-revisions"><span class="visually-hidden">Show this section</span></label>
                                    </div>
                                    <div class="ds_accordion-item__body">
                                        ${docRevisions?no_esc}
                                    </div>
                                </div>
                            </div>
                        </#if>

                        <#if document.updateHistory?has_content>
                            <#include '../common/update-history.ftl'/>
                        </#if>
                    <#elseif isDocumentsPage??>
                        <h2>Supporting documents</h2>

                        <#if documents?? && documents?size gt 0>
                            <section class="document-section">
                                <#list documents as attachedDocument>
                                    <#if attachedDocument?index == 0>
                                        <#assign isHighlightedItem = true/>
                                        <#assign useCoverPage = true/>
                                    </#if>
                                    <#assign docindex = attachedDocument?counter />
                                    <#include '../publication/body-document-info.ftl'/>
                                    <#assign isHighlightedItem = false/>
                                    <#assign useCoverPage = false/>
                                </#list>
                            </section>
                        </#if>

                        <#if groupedDocumentFolders??>
                            <#list groupedDocumentFolders as folder>
                                <#if folder.documents?has_content>
                                <section class="document-section">
                                    <h2>${folder.displayName}</h2>
                                    <#list folder.documents as attachedDocument>
                                        <#assign docindex = attachedDocument?counter />
                                        <#include '../publication/body-document-info.ftl'/>
                                    </#list>
                                </section>
                                </#if>
                            </#list>
                        </#if>

                        <#if document.updateHistory?has_content>
                            <#include '../common/update-history.ftl'/>
                        </#if>

                    <#elseif currentPage == document>
                        <h2>Contents</h2>

                        <div class="ds_accordion" data-module="ds-accordion">
                            <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

                            <#list chapters as chapter>
                                <#if chapter.documents?has_content>
                                    <div class="ds_accordion-item">
                                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${chapter.name}" aria-labelledby="panel-${chapter.name}-heading" />
                                        <div class="ds_accordion-item__header">
                                            <h3 id="panel-${chapter.name}-heading" class="ds_accordion-item__title">
                                                ${chapter.displayName}
                                            </h3>
                                            <span class="ds_accordion-item__indicator"></span>
                                            <label class="ds_accordion-item__label" for="panel-${chapter.name}"><span class="visually-hidden">Show this section</span></label>
                                        </div>
                                        <div class="ds_accordion-item__body">
                                            <nav role="navigation" class="ds_contents-nav" aria-label="Sections">
                                                <ul class="ds_contents-nav__list">
                                                    <#list chapter.documents as section>
                                                        <li class="ds_contents-nav__item">
                                                            <a class="ds_contents-nav__link" href="<@hst.link hippobean=section/>">${section.title}</a>
                                                        </li>
                                                    </#list>
                                                </ul>
                                            </nav>
                                        </div>
                                    </div>
                                </#if>
                            </#list>
                        </div>

                        <#if document.updateHistory?has_content>
                            <#include '../common/update-history.ftl'/>
                        </#if>
                    <#else>
                        <article class="complex-document">
                            <h2>${currentPage.title}</h2>

                            <@hst.html hippohtml=currentPage.content/>

                            <!--noindex-->
                            <nav class="ds_sequential-nav" aria-label="Article navigation">
                                <#if prev??>
                                    <@hst.link var="link" hippobean=prev/>
                                    <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                        <a href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                            <span class="ds_sequential-nav__text" data-label="previous">
                                                ${prev.title}
                                            </span>
                                        </a>
                                    </div>
                                </#if>

                                <#if next??>
                                    <@hst.link var="link" hippobean=next/>
                                    <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                        <a href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                            <span class="ds_sequential-nav__text" data-label="next">
                                                ${next.title}
                                            </span>
                                        </a>
                                    </div>
                                </#if>
                            </nav>
                            <!--endnoindex-->
                        </article>

                        <#if document.updateHistory?has_content>
                            <#include '../common/update-history.ftl'/>
                        </#if>
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

<#if document??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Publication"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>

    <#include "../common/canonical.ftl" />

    <#if currentPage != document>
        <#assign uuid = currentPage.getSingleProperty('jcr:uuid')/>
        <#assign lastUpdated = currentPage.getSingleProperty('hippostdpubwf:lastModificationDate')/>
        <#assign dateCreated = currentPage.getSingleProperty('hippostdpubwf:creationDate')/>
    </#if>

    <#include "../common/gtm-datalayer.ftl"/>
</#if>
