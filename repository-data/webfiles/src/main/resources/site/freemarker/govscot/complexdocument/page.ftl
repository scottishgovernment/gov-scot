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

<@hst.manageContent hippobean=document/>

    <header class="article-header   has-icon  has-icon--guide">
    <p class="article-header__label">Publication - ${document.label}</p>
        <div class="grid"><!--
            --><div class="grid__item  large--two-twelfths  hidden-small  hidden-xsmall  hidden-medium">
                <svg class="svg-icon  mg-icon  mg-icon--full  article-header__icon">
                    <use xlink:href="${iconspath}#format-publication"></use>
                </svg>
            </div><!--
            --><div class="grid__item  large--seven-twelfths">
                
                <h1 class="article-header__title">${document.title}</h1>
                <p>${document.summary}</p>
            </div><!--
        --></div>
    </header>
</div>
<!-- exit a wrapper set in base-layout so we can make the document navigation span the whole width -->

<div class="section-marker  <#if isHomePage??>visible-xsmall  visible-small</#if>">
    <div class="section-marker__part  section-marker__document-title">
        <div class="wrapper">
            <div class="grid"><!--
                --><div class="grid__item  large--ten-twelfths  push--large--two-twelfths">
                    <div class="section-marker__text-line" title="${document.title}">${document.title}</div>
                </div><!--
            --></div>
        </div>
    </div>
    <div class="section-marker__part  section-marker__section-title">
        <div class="wrapper">
            <div class="grid"><!--
                --><div class="grid__item  large--ten-twelfths  push--large--two-twelfths">
                    <div class="section-marker__text-line">${subsectionTitle}</div>
                </div><!--
            --></div>
        </div>
    </div>
</div>

<nav class="document-nav  document-nav--sticky">
    <div class="wrapper">
        <div class="grid"><!--
            --><div class="grid__item  large--ten-twelfths  push--large--two-twelfths">
                <@hst.link hippobean=prev var="prevlink"/>
                <<#if prevlink??>a<#else>span disabled</#if> class="document-nav__button  button--primary  button" href="<@hst.link hippobean=prev/>" title="Previous page">
                    <svg class="svg-icon  mg-icon  document-nav__icon">
                        <use xlink:href="${iconspath}#sharp-chevron_left-24px"></use>
                    </svg>
                    Back
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

<!-- reopen the wrapper in base-layout -->
<div class="wrapper" id="document-content">

    <#if isAboutPage??>

        <div class="grid"><!--
            --><div class="grid__item  large--seven-twelfths  push--large--two-twelfths">

                <h2><b>About this publication</b></h2>

                <dl class="content-data  content-data__list">
                    <dt class="content-data__label">Published:</dt>
                    <dd class="content-data__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="dd MMM yyyy"/></strong></dd>

                    <#if document.responsibleRole??>
                        <dt class="content-data__label">From:</dt>

                        <dd class="content-data__value">
                            <@hst.link var="link" hippobean=document.responsibleRole/>
                            <a href="${link}">${document.responsibleRole.title}</a><!--

                        --><#if document.secondaryResponsibleRole?first??><!--
                        -->, <!--
                            --><a href="#secondary-responsible-roles" class="content-data__expand js-display-toggle">
                                &#43;${document.secondaryResponsibleRole?size}&nbsp;more&nbsp;&hellip;</a>

                                <span id="secondary-responsible-roles" class="content-data__additional">
                                    <#list document.secondaryResponsibleRole as secondaryRole>

                                        <@hst.link var="link" hippobean=secondaryRole/>
                                        <a href="${link}">${secondaryRole.title}</a><#sep>, </#sep>
                                    </#list>
                                </span>

                        </#if>
                        </dd>
                    </#if>

                    <#if document.responsibleDirectorate??>
                        <dt class="content-data__label">Directorate:</dt>

                        <dd class="content-data__value">
                            <@hst.link var="link" hippobean=document.responsibleDirectorate/>
                            <a href="${link}">${document.responsibleDirectorate.title}</a><!--
                            --><#if document.secondaryResponsibleDirectorate?has_content><!--
                            -->, <!--
                            --><a href="#secondary-responsible-directorates" class="content-data__expand  js-display-toggle">
                            &#43;${document.secondaryResponsibleDirectorate?size}&nbsp;more&nbsp;&hellip;</a>

                                <span id="secondary-responsible-directorates" class="content-data__additional">
                                    <#list document.secondaryResponsibleDirectorate as secondaryDirectorate>

                                        <@hst.link var="link" hippobean=secondaryDirectorate/>
                                        <a href="${link}">${secondaryDirectorate.title}</a><#sep>, </#sep>
                                    </#list>
                                </span>
                            </#if>
                        </dd>
                    </#if>

                    <#if document.topics?first??>
                        <dt class="content-data__label">Part of:</dt>

                        <dd class="content-data__value">
                            <#list document.topics?sort_by("title") as topic>
                                <@hst.link var="link" hippobean=topic/>
                                <a href="${link}">${topic.title}</a><#sep>, </sep>
                            </#list>
                        </dd>
                    </#if>

                    <#if document.isbn?has_content>
                        <dt class="content-data__label"><abbr title="International Standard Book Number">ISBN</abbr>:</dt>
                        <dd class="content-data__value">${document.isbn}</dd>
                    </#if>
                </dl>

                <@hst.html hippohtml=document.content/>

                <#if documents?has_content>
                    <div class="documents">
                        <#assign attachedDocument = documents[0]/>
                        <#assign useCoverPage = true/>
                        <#include '../publication/body-document-info.ftl'/>
                    </div>
                </#if>

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
                            <@hst.html hippohtml=document.revisions/>
                        </div>
                    </div>
                    <!-- /end .expandable-item -->
                </div>
            </div><!--
        --></div>

    <#elseif isDocumentsPage??>
        <div class="grid"><!--
            --><div class="grid__item  large--seven-twelfths  push--large--two-twelfths">

                <h2><b>Supporting documents</b></h2>

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
            </div><!--
        --></div>

    <#elseif currentPage == document>
        <div class="grid"><!--
            --><div class="grid__item  large--ten-twelfths  push--large--two-twelfths">
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

                    </div><!--
                    --><div class="grid__item  medium--three-tenths  xlarge--two-tenths">
                        <div class="document-info  document-info--old-style  hidden-small  hidden-xsmall">
                            <#if documents??>
                                <#assign firstDocument = documents[0]/>
                                <#assign filenameExtension = firstDocument.document.filename?keep_after_last(".")?upper_case/>
                                <#assign filenameWithoutExtension = firstDocument.document.filename?keep_before_last(".")/>
                            </#if>
                            <#if (filenameExtension!'') == "PDF" || document.coverimage?has_content>
                                <a class="document-info__thumbnail-link" href="${baseurl + 'about/'}">
                                    <#if document.coverimage?has_content>
                                        <img
                                            alt="View this document"
                                            class="document-info__thumbnail-image"
                                            src="<@hst.link hippobean=document.coverimage.smallcover/>"
                                            srcset="<@hst.link hippobean=document.coverimage.smallcover/> 107w,
                                                <@hst.link hippobean=document.coverimage.mediumcover/> 165w,
                                                <@hst.link hippobean=document.coverimage.largecover/> 214w,
                                                <@hst.link hippobean=document.coverimage.xlargecover/> 330w"
                                            sizes="(min-width: 768px) 165px, 107px" />
                                    <#else>
                                        <img
                                            class="document-info__thumbnail-image"
                                            alt="View this document"
                                            src="<@hst.link hippobean=firstDocument.thumbnails[0]/>"
                                            srcset="
                                            <#list firstDocument.thumbnails as thumbnail>
                                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                            </#list>"
                                            sizes="(min-width: 768px) 165px, 107px" />
                                    </#if>
                                </a>
                            <#else>
                                <a title="View this document" href="${baseurl + 'about/'}" class="file-icon--large  file-icon  file-icon--${filenameExtension!''}"></a>
                            </#if>
                        </div>

                        <a class="button  button--secondary  button--full-width  button--small-margin  icon-button" href="${baseurl + 'about/'}">
                            <div class="icon-button__content">
                                <span class="icon-button__icon">
                                    <svg class="svg-icon  mg-icon">
                                        <use xlink:href="${iconspath}#sharp-info_no_circle-24px"></use>
                                    </svg>
                                </span>
                                <span class="icon-button__text">
                                    About this publication
                                </span>
                            </div>
                        </a>

                        <#if (documents?? && documents?size gt 1) || groupedDocumentFolders??>
                            <a class="button  button--secondary  button--full-width  button--small-margin  icon-button" href="${baseurl + 'documents/'}">
                                <div class="icon-button__content">
                                    <span class="icon-button__icon">
                                        <svg class="svg-icon  mg-icon">
                                            <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                                        </svg>
                                    </span>
                                    <span class="icon-button__text">
                                        Supporting documents
                                    </span>
                                </div>
                            </a>
                        </#if>
                    </div><!--
                --></div>
            </div><!--
        --></div>
    <#else>
        <article class="complex-document">
            <div class="grid"><!--
                --><div class="grid__item  large--seven-twelfths  push--large--two-twelfths">
                    <h3 class="complex-document__title">${currentPage.title}</h3>

                    <@hst.html hippohtml=currentPage.content/>
                </div><!--
            --></div>
        </article>
    </#if>
</div>
<!-- /end .wrapper -->


<div class="wrapper">
    <div class="grid"><!--
        --><div class="grid__item  large--seven-twelfths  push--large--two-twelfths">
            <#include '../common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</div>

</#if>

<@hst.headContribution category="footerScripts">
<script src="<@hst.webfile path="/assets/scripts/complex-document.js"/>" type="text/javascript"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "../common/canonical.ftl" />
</#if>
