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

<@hst.link hippobean=document var="baseurl" canonical=true/>

<@hst.manageContent hippobean=document/>
    <header class="article-header  gov_sublayout  gov_sublayout--publication-header">
        <div class="grid"><!--
            --><div class="grid__item  medium--nine-twelfths">
                <div class="grid"><!--
                    --><div class="grid__item">
                        <div class="gov_sublayout__title">
                            <span class="article-header__label">Publication - ${document.label}</span>
                            <h1 class="article-header__title">${document.title}</h1>
                        </div>
                    </div><!--
                    --><div class="grid__item  large--three-ninths">
                        <div class="gov_sublayout__metadata">
                            <#include '../publication/metadata.ftl'/>
                        </div>
                    </div><!--
                    --><div class="grid__item large--six-ninths">
                        <div class="gov_sublayout__content">
                            <#if document.summary??>
                                <p class="leader">${document.summary}</p>
                            </#if>
                            <#include '../common/collections-list.ftl'/>
                        </div>
                    </div><!--
                --></div>
            </div><!--
            --><div class="grid__item  medium--three-twelfths  large--two-twelfths  push--large--one-twelfth">
                <div class="gov_sublayout__document">
                    <#if documents?? && documents?size gt 0>
                        <div class="gov_supporting-documents">
                            <#if document.displayPrimaryDocument == true>
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
                                    <a data-title="${document.title}" title="View supporting documents" href="${baseurl + 'documents/'}" class="file-icon--large  file-icon  file-icon--${filenameExtension!''}"></a>
                                </#if>
                            </#if>

                            <!--noindex-->
                            <a href="${baseurl + 'documents/'}" class="button  button--secondary  ds_no-margin--top  gov_supporting-documents__button">
                                <span class="gov_supporting-documents__button-icon">
                                    <svg aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron-right"></use></svg>
                                </span>
                                <span class="gov_supporting-documents__button-text">Supporting documents</span>
                            </a>
                            <!--endnoindex-->
                        </div>
                    </#if>
                </div>
            </div><!--
        --></div>
    </header>
</div>
<!-- exit a wrapper set in base-layout so we can make the document navigation span the whole width -->

<!--noindex-->
<div class="section-marker  <#if isHomePage??>visible-xsmall  visible-small</#if>">
    <div class="section-marker__part  section-marker__document-title">
        <div class="wrapper">
            <div class="grid"><!--
                --><div class="grid__item  large--ten-twelfths  push--large--three-twelfths">
                    <div class="section-marker__text-line" title="${document.title}">${document.title}</div>
                </div><!--
            --></div>
        </div>
    </div>
    <div class="section-marker__part  section-marker__section-title">
        <div class="wrapper">
            <div class="grid"><!--
                --><div class="grid__item  large--ten-twelfths  push--large--three-twelfths">
                    <div class="section-marker__text-line">${subsectionTitle}</div>
                </div><!--
            --></div>
        </div>
    </div>
</div>

<nav class="document-nav  document-nav--sticky">
    <div class="wrapper">
        <div class="grid"><!--
            --><div class="grid__item  large--nine-twelfths  push--large--three-twelfths">
                <@hst.link hippobean=prev var="prevlink"/>
                <<#if prevlink??>a<#else>span disabled</#if> class="document-nav__button  button--primary  button" href="<@hst.link hippobean=prev/>" title="Previous page">
                    <svg class="svg-icon  mg-icon  document-nav__icon">
                        <use xlink:href="${iconspath}#sharp-chevron_left-24px"></use>
                    </svg>
                    <span aria-hidden="true">Prev</span> <span class="hidden">Previous</span>
                <<#if prevlink??>/a<#else>/span</#if>><!--

                <@hst.link hippobean=document var="baseurl" canonical=true/>
                --><<#if disableMenuButton??>span disabled<#else>a</#if> class="document-nav__button  button--primary  button" href="${baseurl}" title="Menu">
                    <svg class="svg-icon  mg-icon  document-nav__icon">
                        <use xlink:href="${iconspath}#sharp-menu-24px"></use>
                    </svg>
                    Menu<<#if disableMenuButton??>/span<#else>/a</#if>><!--

                <@hst.link hippobean=next var="nextlink"/>
                --><<#if nextlink??>a<#else>span disabled</#if> class="document-nav__button  button--primary  button" href="<@hst.link hippobean=next/>" title="Next page">
                    <svg class="svg-icon  mg-icon  document-nav__icon">
                        <use xlink:href="${iconspath}#sharp-chevron_right-24px"></use>
                    </svg>
                    Next<<#if nextlink??>/a<#else>/span</#if>>
            </div><!--
        --></div>
    </div>
</nav>
<!--endnoindex-->

<!-- reopen the wrapper in base-layout -->
<div class="wrapper" id="page-content">

    <#if isAboutPage??>

        <div class="grid"><!--
            --><div class="grid__item  large--seven-twelfths  push--large--three-twelfths">

                <@hst.html hippohtml=document.content/>

                <#if documents?has_content>
                    <div class="documents">
                        <#assign attachedDocument = documents[0]/>
                        <#assign useCoverPage = true/>
                        <#include '../publication/body-document-info.ftl'/>
                    </div>
                </#if>

                <@hst.html hippohtml=document.revisions var="docRevisions"/>
                <#if docRevisions?has_content>
                    <div class="expandable">
                        <div class="expandable-item">
                            <button class="expandable-item__header  js-toggle-expand" role="tab" id="revisions-heading" data-toggle="collapse" aria-expanded="false" aria-controls="revisions-body">
                                <h3 class="expandable-item__title  gamma"><span class="link-text">Revisions</span></h3>
                                <span class="expandable-item__icon">
                                    <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                                        <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                                    </svg>
                                    <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                                        <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                                    </svg>
                                </span>
                            </button>

                            <div id="revisions-body" class="expandable-item__body  expandable-item__body--with-padding" role="tabpanel" aria-expanded="false" aria-labelledby="revisions-heading">
                                ${docRevisions}
                            </div>
                        </div>
                        <!-- /end .expandable-item -->
                    </div>
                </#if>

                <#if document.updateHistory?has_content>
                    <#include '../common/update-history.ftl'/>
                </#if>
            </div><!--
        --></div>

    <#elseif isDocumentsPage??>
        <div class="grid"><!--
            --><div class="grid__item  large--seven-twelfths  push--large--three-twelfths">

                <h2><b>Supporting documents</b></h2>

                <#if documents?? && documents?size gt 0>
                        <section class="document-section">
                            <#list documents as attachedDocument>
                                <#if attachedDocument?index == 0>
                                    <#assign isHighlightedItem = true/>
                                    <#assign useCoverPage = true/>
                                </#if>
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
                                <#assign isHighlightedItem = attachedDocument?is_first/>
                                <#include '../publication/body-document-info.ftl'/>
                            </#list>
                        </section>
                        </#if>
                    </#list>
                </#if>

                <#if document.updateHistory?has_content>
                    <#include '../common/update-history.ftl'/>
                </#if>
            </div><!--
        --></div>

    <#elseif currentPage == document>
        <div class="grid"><!--
            --><div class="grid__item  large--ten-twelfths  push--large--three-twelfths">
                <h2><b>Contents</b></h2>

                <div class="grid"><!--
                    --><div class="grid__item  medium--seven-tenths  xlarge--eight-tenths">

                        <div class="expandable  contents-expandable">
                            <button class="button  button--secondary  button--small  button--no-margin  expand-all-button  js-expand-all">
                                <span class="expand-all-button__text  js-button-text">Expand all</span>
                                <span class="expand-all-button__icon-container">
                                    <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more  expand-all-button__icon">
                                        <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                                    </svg>
                                    <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less  expand-all-button__icon">
                                        <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                                    </svg>
                                </span>
                            </button>

                            <#list chapters as chapter>
                                <div class="expandable-item">
                                    <button class="expandable-item__header  js-toggle-expand" role="tab" id="${chapter.name}-heading" data-toggle="collapse" aria-expanded="false" aria-controls="${chapter.name}-body">
                                        <h3 class="expandable-item__title  gamma"><span class="link-text">${chapter.displayName}</span></h3>
                                        <span class="expandable-item__icon">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                                                <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                                            </svg>
                                            <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                                                <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                                            </svg>
                                        </span>
                                    </button>

                                    <div id="${chapter.name}-body" class="expandable-item__body" role="tabpanel" aria-expanded="false" aria-labelledby="${chapter.name}-heading">
                                        <ul class="contents-list">
                                            <#list chapter.documents as section>

                                                <li class="contents-list__item">
                                                    <svg class="svg-icon  mg-icon  contents-list__icon">
                                                        <use xlink:href="${iconspath}#sharp-chevron_right-24px"></use>
                                                    </svg>
                                                    <a class="contents-list__link" href="<@hst.link hippobean=section/>">${section.title}</a>
                                                </li>

                                            </#list>
                                        </ul>
                                    </div>
                                </div>
                                <!-- /end .expandable-item -->
                            </#list>
                        </div>

                        <#if document.updateHistory?has_content>
                            <#include '../common/update-history.ftl'/>
                        </#if>

                    </div><!--
                --></div>
            </div><!--
        --></div>
    <#else>
        <article class="complex-document">
            <div class="grid"><!--
                --><div class="grid__item  large--seven-twelfths  push--large--three-twelfths">
                    <h3 class="complex-document__title">${currentPage.title}</h3>

                    <@hst.html hippohtml=currentPage.content/>


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

                    <#if document.updateHistory?has_content>
                        <#include '../common/update-history.ftl'/>
                    </#if>
                </div><!--
            --></div>
        </article>
    </#if>
</div>
<!-- /end .wrapper -->


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
    <script type="module" src="<@hst.webfile path="/assets/scripts/complex-document.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/complex-document.es5.js"/>"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Publication"/>
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
