<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
<@hst.manageContent hippobean=document/>

    <header class="article-header has-icon has-icon--guide">
        <div class="grid"><!--
            --><div class="grid__item medium--two-twelfths  hidden-small  hidden-xsmall">
                <svg class="svg-icon  mg-icon  mg-icon--full">
                    <use xlink:href="${iconspath}#sharp-description-24px"></use>
                </svg>
            </div><!--
            --><div class="grid__item  medium--seven-twelfths">
                <p class="article-header__label">Publication - ${document.label}</p>
                <h1 class="article-header__title">${document.title}</h1>
            </div><!--
        --></div>
    </header>

    <div class="grid"><!--
        --><div class="grid__item  medium--seven-twelfths  push--medium--two-twelfths">
            <@hst.html var="executiveSummary" hippohtml=document.executiveSummary/>
            <#if executiveSummary?has_content>
                ${executiveSummary}
            <#else>
                ${document.summary}
            </#if>
        </div><!--
    --></div>

</div>
<!-- exit a wrapper set in base-layout so we can make the document navigation span the whole width -->
<#if currentPage != document>
    <div class="section-marker">
        <div class="section-marker__part  section-marker__document-title">
            <div class="wrapper">
                <div class="grid"><!--
                    --><div class="grid__item  medium--seven-twelfths  push--medium--two-twelfths">
                        <div class="section-marker__text-line">${document.title}</div>
                    </div><!--
                --></div>
            </div>
        </div>
        <div class="section-marker__part  section-marker__section-title">
            <div class="wrapper">
                <div class="grid"><!--
                    --><div class="grid__item  medium--seven-twelfths  push--medium--two-twelfths">
                        <div class="section-marker__text-line">${currentChapter.displayName}</div>
                    </div><!--
                --></div>
            </div>
        </div>
    </div>
</#if>

<nav class="document-nav  <#if currentPage != document>document-nav--sticky</#if>">
    <div class="wrapper">
        <div class="grid"><!--
            --><div class="grid__item  medium--ten-twelfths  push--medium--two-twelfths">
                <@hst.link hippobean=prev var="prevlink"/>
                <<#if prevlink??>a<#else>span disabled</#if> class="document-nav__button  button" href="<@hst.link hippobean=prev/>" title="Previous page">
                    <svg class="svg-icon  mg-icon  document-nav__icon">
                        <use xlink:href="${iconspath}#sharp-chevron_left-24px"></use>
                    </svg>
                    Back
                <<#if prevlink??>/a<#else>/span</#if>><!--

                <@hst.link hippobean=document var="baseurl" canonical=true/>
                --><<#if currentPage != document>a<#else>span disabled</#if> class="document-nav__button  button" href="${baseurl}" title="Menu">
                    <svg class="svg-icon  mg-icon  document-nav__icon">
                        <use xlink:href="${iconspath}#sharp-menu-24px"></use>
                    </svg>
                    Menu<<#if currentPage != document>/a<#else>/span</#if>><!--

                <@hst.link hippobean=next var="nextlink"/>
                --><<#if nextlink??>a<#else>span disabled</#if> class="document-nav__button  button" href="<@hst.link hippobean=next/>" title="Next page">
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

        <p>This is the About this publication page.</p>

        <p>Content: <@hst.html hippohtml=document.content/></p>

        <#if documents?has_content>
            <p>Main document: ${documents[0].title}</p>
        </#if>

    <#elseif isDocumentsPage??>
        <div class="grid"><!--
            --><div class="grid__item  medium--seven-twelfths  push--medium--two-twelfths">

                <h2>Supporting documents</h2>

                <#if documents??>
                    <#assign hasAttachedDocument = '' />
                    <#list documents as attachedDocument>
                        <#assign hasAttachedDocument = true />
                    </#list>

                    <#if hasAttachedDocument?has_content>
                        <section class="document-section">
                            <#list documents as attachedDocument>
                                <#include '../publication/body-document-info.ftl'/>
                            </#list>
                        </section>
                    </#if>

                    <#if groupedDocumentFolders??>
                        <#list groupedDocumentFolders as folder>
                            <section class="document-section">
                                <h2>${folder.displayName}</h2>
                                <#list folder.documents as attachedDocument>
                                    <#include '../publication/body-document-info.ftl'/>
                                </#list>
                            </section>
                        </#list>
                    </#if>
                </#if>
            </div><!--
        --></div>

    <#elseif currentPage == document>
        <div class="grid"><!--
            --><div class="grid__item  medium--seven-twelfths  push--medium--two-twelfths">
                <h2>Contents</h2>

                <button class="button  button--secondary  button--medium  button--no-margin  icon-button  js-expand-all">
                    <span class="icon-button__text  js-button-text">Expand all</span>
                    <span class="icon-button__icon-container">
                        <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more  icon-button__icon">
                            <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                        </svg>
                        <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less  icon-button__icon">
                            <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                        </svg>
                    </span>
                </button>
                <div class="expandable  contents-expandable">
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
            --><div class="grid__item  medium--three-twelfths  push--medium--two-twelfths">


                <#assign firstDocument = documents[0]/>
                <#assign filenameExtension = firstDocument.document.filename?keep_after_last(".")?upper_case/>
                <#assign filenameWithoutExtension = firstDocument.document.filename?keep_before_last(".")/>
                <#if filenameExtension == "PDF">
                    <a href="<@hst.link hippobean=firstDocument.document/>?inline=true">
                        <img
                            alt="View this document"
                            src="<@hst.link hippobean=firstDocument.thumbnails[0]/>"
                            srcset="
                                    <#list firstDocument.thumbnails as thumbnail>
                                        <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                    </#list>"
                            sizes="(min-width: 768px) 165px, 107px" />
                    </a>
                <#else>
                    <a title="View this document" href="<@hst.link hippobean=firstDocument.document/>?inline=true" class="file-icon--<#if attachedDocument.highlighted>large<#else>medium</#if>  file-icon  file-icon--${filenameExtension}"></a>
                </#if>

                        



                <a class="button  button--secondary  button--full-width" href="${baseurl + 'about/'}">About this publication</a><br><br>

                <#if documents?has_content>
                    <a class="button  button--secondary  button--full-width" href="${baseurl + 'documents/'}">Supporting documents</a><br><br>
                </#if>
            </div><!--
        --></div>
    <#else>
        <div class="grid"><!--
            --><div class="grid__item  medium--seven-twelfths  push--medium--two-twelfths">
                <h3>${currentChapter.displayName}</h3>
                <h4>${currentPage.title}</h4>

                <@hst.html hippohtml=currentPage.content/>
            </div><!--
        --></div>
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
    <@hst.headContribution category="pageTitle"><title>${document.title} - gov.scot</title></@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "../common/canonical.ftl" />
</#if>
