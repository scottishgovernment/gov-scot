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
        <div class="ds_layout  gov_layout--publication">
            <div class="ds_layout__header">
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
                            <p class="ds_leader">${document.summary}</p>
                        </#if>

                        <#include '../common/collections-list.ftl'/>
                    </div>

                    <div class="gov_sublayout__document">
                        <#if document.displayPrimaryDocument == true && (documents?? && documents?size gt 0)>
                            <div class="gov_supporting-documents">
                                <#if documents??>
                                    <#assign firstDocument = documents[0]/>
                                    <#assign filenameExtension = firstDocument.document.filename?keep_after_last(".")?upper_case/>
                                    <#assign filenameWithoutExtension = firstDocument.document.filename?keep_before_last(".")/>
                                </#if>
                                <#if (filenameExtension!'') == "PDF" || document.coverimage?has_content>
                                    <a class="gov_supporting-documents__thumbnail-link" href="${baseurl + 'documents/'}">
                                        <#if document.coverimage?has_content>
                                            <img
                                                alt="View supporting documents"
                                                class="gov_supporting-documents__thumbnail"
                                                src="<@hst.link hippobean=document.coverimage.smallcover/>"
                                                srcset="<@hst.link hippobean=document.coverimage.smallcover/> 107w,
                                                    <@hst.link hippobean=document.coverimage.mediumcover/> 165w,
                                                    <@hst.link hippobean=document.coverimage.largecover/> 214w,
                                                    <@hst.link hippobean=document.coverimage.xlargecover/> 330w"
                                                sizes="(min-width: 768px) 165px, 107px" />
                                        <#else>
                                            <img
                                                class="gov_supporting-documents__thumbnail"
                                                alt="View supporting documents"
                                                src="<@hst.link hippobean=firstDocument.thumbnails[0]/>"
                                                srcset="
                                                <#list firstDocument.thumbnails as thumbnail>
                                                    <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                                </#list>"
                                                sizes="(min-width: 768px) 165px, 107px" />
                                        </#if>
                                    </a>
                                <#else>
                                    <a aria-hidden="true" data-title="${document.title}" href="${baseurl + 'documents/'}" class="gov_file-icon  gov_file-icon--${filenameExtension!''}">
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
                        </#if>
                    </div>
                </header>
            </div>
        </div>
    </div>

    <div class="gov_document-banner">
        <div class="ds_wrapper">
            <div class="ds_layout  gov_layout--publication">
                <div class="ds_layout__content">
                    <#if isDocumentsPage?? || currentPage == document>
                    <#else>
                        <div class="gov_document-banner__title">
                            <span class="gov_document-banner__title-main">${document.title}</span>
                            <span class="gov_document-banner__title-sub">${subsectionTitle}</span>
                        </div>
                    </#if>
                    <div class="gov_document-banner__nav">
                        <#if prevlink??>
                            <a href="${prevlink}" class="ds_button  gov_document-banner__button">
                                <svg class="ds_icon  gov_document-banner__button-icon">
                                    <use xlink:href="${iconspath}#chevron-left"></use>
                                </svg>
                                <span aria-hidden="true">Prev</span> <span class="visually-hidden">Previous</span>
                            </a>
                        <#else>
                            <span class="ds_button  gov_document-banner__button  ds_current">
                                <svg class="ds_icon  gov_document-banner__button-icon">
                                    <use xlink:href="${iconspath}#chevron-left"></use>
                                </svg>
                                <span aria-hidden="true">Prev</span> <span class="visually-hidden">Previous</span>
                            </span>
                        </#if>

                        <#if !disableMenuButton??>
                            <a href="${baseurl}" class="ds_button  gov_document-banner__button">
                                <svg class="ds_icon  gov_document-banner__button-icon">
                                    <use xlink:href="${iconspath}#menu"></use>
                                </svg>
                                Menu
                            </a>
                        <#else>
                            <span class="ds_button  gov_document-banner__button  ds_current">
                                <svg class="ds_icon  gov_document-banner__button-icon">
                                    <use xlink:href="${iconspath}#menu"></use>
                                </svg>
                                Menu
                            </span>
                        </#if>

                        <#if nextlink??>
                            <a href="${nextlink}" class="ds_button  gov_document-banner__button">
                                <svg class="ds_icon  gov_document-banner__button-icon">
                                    <use xlink:href="${iconspath}#chevron-right"></use>
                                </svg>
                                Next
                            </a>
                        <#else>
                            <span class="ds_button  gov_document-banner__button  ds_current">
                                <svg class="ds_icon  gov_document-banner__button-icon">
                                    <use xlink:href="${iconspath}#chevron-right"></use>
                                </svg>
                                Next
                            </span>
                        </#if>
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

        const element = document.querySelector('.gov_document-banner');
        const observer = new IntersectionObserver(
            function ([e]) {
                window.foo = e.isIntersecting;
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

    </style>

    <div class="ds_wrapper">
        <div class="ds_layout  gov_layout--publication">
            <div class="ds_layout__content">
                <#if isAboutPage??>
                    <h2>About this publication</h2>

                    <#include '../publication/metadata.ftl'/>

                    <@hst.html hippohtml=document.content/>

                    <#if documents?has_content>
                        <div>
                            <#assign attachedDocument = documents[0]/>
                            <#assign useCoverPage = true/>
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
                                    ${docRevisions}
                                </div>
                            </div>
                        </div>
                    </#if>
                <#elseif isDocumentsPage??>
                    <h2>Supporting documents</h2>

                    <#if documents?? && documents?size gt 0>
                        <section class="document-section">
                            <#list documents as attachedDocument>
                                <#if attachedDocument?index == 0>
                                    <#assign isLimelitItem = true/>
                                    <#assign useCoverPage = true/>
                                </#if>
                                <#include '../publication/body-document-info.ftl'/>
                                <#assign isLimelitItem = false/>
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
                                    <#include '../publication/body-document-info.ftl'/>
                                </#list>
                            </section>
                            </#if>
                        </#list>
                    </#if>
                <#elseif currentPage == document>
                    <h2>Contents</h2>

                    <div class="ds_accordion" data-module="ds-accordion">
                        <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

                        <#list chapters as chapter>
                            <div class="ds_accordion-item">
                                <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-{chapter.name}" aria-labelledby="panel-{chapter.name}-heading" />
                                <div class="ds_accordion-item__header">
                                    <h3 id="panel-{chapter.name}-heading" class="ds_accordion-item__title">
                                        ${chapter.displayName}
                                    </h3>
                                    <span class="ds_accordion-item__indicator"></span>
                                    <label class="ds_accordion-item__label" for="panel-{chapter.name}"><span class="visually-hidden">Show this section</span></label>
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
                        </#list>
                    </div>
                <#else>
                    <article class="complex-document">
                        <h3>${currentPage.title}</h3>

                        <@hst.html hippohtml=currentPage.content/>

                        <nav class="ds_sequential-nav" aria-label="Article navigation">
                            <#if prev??>
                                <@hst.link var="link" hippobean=prev/>
                                <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                                    <a title="Previous section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                                        <span class="ds_sequential-nav__text" data-label="previous">
                                            ${prev.title}
                                        </span>
                                    </a>
                                </div>
                            </#if>

                            <#if next??>
                                <@hst.link var="link" hippobean=next/>
                                <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                                    <a title="Next section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                                        <span class="ds_sequential-nav__text" data-label="next">
                                            ${next.title}
                                        </span>
                                    </a>
                                </div>
                            </#if>
                        </nav>
                    </article>
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
    <script type="module" src="<@hst.webfile path="/assets/scripts/complex-document.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/complex-document.es5.js"/>"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>

    <#include "../common/canonical.ftl" />

    <#if currentPage != document>
        <#assign uuid = currentPage.getProperty('jcr:uuid')/>
        <#assign lastUpdated = currentPage.getProperty('hippostdpubwf:lastModificationDate')/>
        <#assign dateCreated = currentPage.getProperty('hippostdpubwf:creationDate')/>
    </#if>
    <#include "../common/gtm-datalayer.ftl"/>
</#if>
